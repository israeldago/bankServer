/*
 * The MIT License
 *
 * Copyright 2018 Israel Dago at https://github.com/ivoireNoire.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.israeldago.bankserver.service;

import com.israeldago.bankserver.dto.AccountDTO;
import com.israeldago.bankserver.dto.ResponseDTO;
import com.israeldago.bankserver.dto.TransactionDTO;
import com.israeldago.bankserver.dto.enums.BankOperationType;
import static com.israeldago.bankserver.dto.enums.BankOperationType.*;
import com.israeldago.bankserver.helpers.BankingOperation;
import com.israeldago.bankserver.helpers.Validator;
import com.israeldago.bankserver.persistence.DAO;
import com.israeldago.bankserver.persistence.entities.AccountDB;
import com.israeldago.bankserver.persistence.entities.AppUserDB;
import java.util.function.Consumer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.israeldago.bankserver.service.callsPublic.BankingCalls;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.lang.RandomStringUtils;
import static com.israeldago.bankserver.helpers.Cleaner.*;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class BankingService implements BankingCalls {

    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("com.israeldago_bankServer_jar_1.0.0PU");
    private static final BankingService SINGLETON = new BankingService();

    private BankingService() {}

    public static void use(final Consumer<BankingCalls> serviceConsumer , final Consumer<Throwable> errorConsumer) {
        try {
            serviceConsumer.accept(SINGLETON);
        } catch (Exception e) {
            errorConsumer.accept(e);
        }
    }

    @Override
    public CompletableFuture<Optional<AccountDTO>> createNewAccountForUserAsync(final Integer userUniqueId) {
        return CompletableFuture.supplyAsync(() -> applyEntityManagerTransactionally(BankingService.EMF, entityManager -> {
            DAO<AppUserDB> userDAO = DAO.of(AppUserDB.class, () -> entityManager);
            DAO<AccountDB> accountDAO = DAO.of(AccountDB.class, () -> entityManager);
            AppUserDB userValidated = validateUser(userUniqueId, userDAO);
            AccountDB newAccountDB = new AccountDB(0.0, LocalDateTime.now().toString(), "RO" + RandomStringUtils.randomAlphanumeric(12).toUpperCase(), userValidated);
            accountDAO.persist(newAccountDB);
            entityManager.getTransaction().commit();
            return accountDAO.findByQueryWithOneParam("AccountDB.findByIban", "iban", newAccountDB.getIban()).map(AccountDB::toAccountDTO);
        }));
    }

    @Override
    public CompletableFuture<Boolean> deleteAccountAsync(final Integer accountId, final Integer holderId) {
        return CompletableFuture.supplyAsync(() -> applyEntityManagerTransactionally(BankingService.EMF, entityManager -> {
            DAO<AppUserDB> userDAO = DAO.of(AppUserDB.class, () -> entityManager);
            DAO<AccountDB> accountDAO = DAO.of(AccountDB.class, () -> entityManager);
            Validator.of(accountDAO.findOne(accountId).orElseThrow(() -> new RuntimeException("Cannot find any account with the given accountID param")))
                     .checking(AccountDB::getCurrentSold, currentSold -> currentSold == 0d , () -> "Cannot process this operation -> the target account still have money")
                     .checking(givenAccountDB -> userDAO.findOne(holderId).isPresent(), () -> "Cannot find any user with the given accountHolder ID : "+ holderId)
                     .checking(AccountDB::getHolderId, givenHolderId -> holderId.equals(givenHolderId), () -> "Given user does not hold this account")
                     .whenCompletedAccept(accountDAO::remove, Throwable::printStackTrace);
            entityManager.getTransaction().commit();
            return !accountDAO.findOne(accountId).isPresent();
        }));
    }    

    @Override
    public CompletableFuture<Stream<AccountDTO>> allAccountsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            return applyEntityManager(EMF, entityManager -> DAO.of(AccountDB.class, () -> entityManager).findAll().map(AccountDB::toAccountDTO));
        });
    }

    @Override
    public CompletableFuture<ResponseDTO> makeBankingOperationAsync(final TransactionDTO dto) {
        return CompletableFuture.supplyAsync(() -> registerSupportedBankingOperations(dto))
                                .thenComposeAsync(supportedOperations -> CompletableFuture.supplyAsync(() -> supportedOperations.runOperation(dto.getBankOperationType())))
                                .thenApplyAsync(currentResponse -> new ResponseDTO(currentResponse, dto));
    }
    
    //************PRIVATE METHODS*************
    private BankingOperation<BankOperationType> registerSupportedBankingOperations(final TransactionDTO dto) {
        return BankingOperation.registerBankingOperation(dto, BankingService.EMF , builder -> {
            builder.register(DEPOSIT, (depositRequest, emf) -> depositOperation(depositRequest::getAmount, depositRequest::getDepositAccoutId, emf));
            builder.register(WITHDRAW, (withdrawRequest, emf) -> withDrawOperation(withdrawRequest::getAmount, withdrawRequest::getWithdrawAccountId, emf));
            builder.register(TRANSFER, (transferRequest, emf) -> transferMoneyOperation(transferRequest::getAmount, transferRequest::getWithdrawAccountId, transferRequest::getDepositAccoutId, emf));
        });
    }

    private ResponseDTO depositOperation(Supplier<Double> amount, Supplier<Integer> depositAccountId, EntityManagerFactory emf) {
        return applyEntityManagerTransactionally(emf, entityManager -> {
            Validator.of(amount.get())
                     .checking(givenAmount -> givenAmount > 0d , () -> "Cannot make a deposit with a negative amount or amount == 0$")
                     .validate();
            DAO<AccountDB> accountDAO = DAO.of(AccountDB.class, () -> entityManager);
            AccountDB targetAccountDB = accountDAO.findOne(depositAccountId.get()).orElseThrow(() -> new RuntimeException("Cannot find any account with the given accountID param"));
            targetAccountDB.setCurrentSold(targetAccountDB.getCurrentSold() + amount.get());
            entityManager.getTransaction().commit();
            return ResponseDTO.builder().taskStatus(Boolean.TRUE).message("Deposit Operation Successfull").build();
        });
    }

    private ResponseDTO withDrawOperation(Supplier<Double> amount, Supplier<Integer> withdrawAccountId, EntityManagerFactory emf) {
        return applyEntityManagerTransactionally(emf, entityManager -> {
            Validator.of(amount.get())
                     .checking(givenAmount -> givenAmount > 0d , () -> "Cannot make a withdraw with a negative amount or amount == 0$")
                     .validate();
            DAO<AccountDB> accountDAO = DAO.of(AccountDB.class, () -> entityManager);
            Validator.of(accountDAO.findOne(withdrawAccountId.get()).orElseThrow(() -> new RuntimeException("Cannot find any account with the given accountID param")))
                     .checking(AccountDB::getCurrentSold, currentSold -> currentSold > amount.get() , () -> "insufficient funds to process this operation")
                     .whenCompletedAccept(givenAccountDB -> givenAccountDB.setCurrentSold(givenAccountDB.getCurrentSold() - amount.get()), Throwable::printStackTrace);
            entityManager.getTransaction().commit();
            return ResponseDTO.builder().taskStatus(Boolean.TRUE).message("WithDraw Operation Successfull").build();
        });
    }

    private ResponseDTO transferMoneyOperation(Supplier<Double> amount, Supplier<Integer> withdrawAccountId, Supplier<Integer> depositAccountId, EntityManagerFactory emf) {
        CompletableFuture<ResponseDTO> withDrawTask = CompletableFuture.supplyAsync(() -> withDrawOperation(amount, withdrawAccountId, emf));
        CompletableFuture<ResponseDTO> depositTask = CompletableFuture.supplyAsync(() -> depositOperation(amount, depositAccountId, emf));
        return withDrawTask.thenCombineAsync(depositTask, (withDrawOp, depositOp) -> ResponseDTO.builder().taskStatus(Boolean.TRUE).message("WithDraw Operation Successfull").build())
                           .thenApplyAsync(taskResp -> ResponseDTO.builder().taskStatus(taskResp.getTaskStatus()).message("Transfer Money Operation Successfull").build())
                           .join();
    }

    private AppUserDB validateUser(Integer userId, DAO<AppUserDB> userDAO) {
        Optional<AppUserDB> optionalUser = userDAO.findOne(userId);
        return Validator.of(optionalUser.orElseThrow(() -> new RuntimeException("Cannot find any user with the given ID")))
                .checking(user -> user.getRole().getRoleName(), "ADMIN"::equalsIgnoreCase, () -> "Only Admins can call the createNewAccountForUser Method")
                //more checking cases here if needed
                .validate();
    }
}