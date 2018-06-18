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

import com.israeldago.bankserver.dto.AppUserDTO;
import com.israeldago.bankserver.persistence.DAO;
import com.israeldago.bankserver.persistence.entities.AppUserDB;
import com.israeldago.bankserver.persistence.entities.RoleDB;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.israeldago.bankserver.service.callsPublic.AccessCalls;
import com.israeldago.bankserver.helpers.Validator;
import static com.israeldago.bankserver.helpers.Cleaner.*;
import java.util.function.Supplier;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class AccessService implements AccessCalls {

    private  /*static final */ EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.israeldago_bankServer_jar_1.0.0PU");
    private static final AccessService SINGLETON = new AccessService();
    
    private AccessService() {}

    public static void use(final Consumer<AccessCalls> consumer, final Consumer<Throwable> errorConsumer) {
        try {
            consumer.accept(SINGLETON);
        } catch (Exception e) {
            errorConsumer.accept(e);
        }
    }

    public static void use(final Supplier<EntityManagerFactory> emfSupplier, final Consumer<AccessCalls> consumer,  final Consumer<Throwable> errorConsumer) {
       try {
            SINGLETON.entityManagerFactory = emfSupplier.get();
            consumer.accept(SINGLETON);
        } catch (Exception e) {
            errorConsumer.accept(e);
        }
    } 

    @Override
    public CompletableFuture<Boolean> registerUserAsync(final AppUserDTO userDTO) {
        return CompletableFuture.supplyAsync(() -> applyEntityManagerTransactionally(entityManagerFactory, entityManager -> {
            DAO<AppUserDB> userDAO = DAO.of(AppUserDB.class, () -> entityManager);
            DAO<RoleDB> roleDAO = DAO.of(RoleDB.class, () -> entityManager);
            Validator.of(userDTO.toAppUserDB())
                    .checking(user -> user != null, () -> "User cannot be null for registration")
                    .checking(AppUserDB::getIdentityCardNumber, cardNumber -> !isPersisted(userDAO, cardNumber), () -> "User already exist in Database")
                    .whenCompletedAccept(userValidated -> userDAO.persist(setOrPersistRoleName(roleDAO, userValidated)), th -> {throw new RuntimeException(th);});
            entityManager.getTransaction().commit();
            return isPersisted(userDAO, userDTO.getIdentityCardNumber());
        }));
    }

    @Override
    public CompletableFuture<Optional<AppUserDTO>> loginUserAsync(final String givenUsername, final String givenPassword) {
        return CompletableFuture.supplyAsync(() -> {
            return applyEntityManager(entityManagerFactory, entityManager -> {
                DAO<AppUserDB> userDAO = DAO.of(AppUserDB.class, () -> entityManager);
                Optional<AppUserDB> optionalUser = userDAO.findByQueryWithOneParam("AppUserDB.findByUsername", "username", givenUsername);
                return Validator.of(optionalUser.orElseThrow(() -> new RuntimeException("Wrong Username - cannot find this user with the given username")))
                        .checking(AppUserDB::getPassword, password -> password.equals(givenPassword), () -> "Wrong Password")
                        .whenCompletedApply(AppUserDB::toAppUserDTO, Throwable::printStackTrace);
            });
        });
    }

    ///*******PRIVATE METHODS*******************      
    private boolean isPersisted(final DAO<AppUserDB> userDAO, final String identityCardNumber) {
        return userDAO.findByQueryWithOneParam("AppUserDB.findByIdentityCardNumber", "identityCardNumber", identityCardNumber).isPresent();
    }

    private AppUserDB setOrPersistRoleName(final DAO<RoleDB> roleDAO, final AppUserDB userDB) {
        Optional<RoleDB> userRole = roleDAO.findByQueryWithOneParam("RoleDB.findByRoleName", "roleName", userDB.getRole().getRoleName());
        if (userRole.isPresent()) {
            userDB.setRole(userRole.get());
        } else {
            userDB.getRole().setRoleName(userDB.getRole().getRoleName().toUpperCase());
            roleDAO.persist(userDB.getRole());
        }
        return new AppUserDB(userDB);
    }

}
