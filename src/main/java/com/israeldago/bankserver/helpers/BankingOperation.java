/*
 * The MIT License
 *
 * Copyright 2018 Israel Dago at https://github.com/israeldago.
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
package com.israeldago.bankserver.helpers;

import com.israeldago.bankserver.dto.ResponseDTO;
import com.israeldago.bankserver.dto.TransactionDTO;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 * @param <T> for the Type of Operation, could be String or a custom enum
 */
@FunctionalInterface
public interface BankingOperation<T> {

    public ResponseDTO runOperation(T bankOperation);
   
    public static <T> BankingOperation registerBankingOperation(TransactionDTO givenBankOperation , EntityManagerFactory givenEMF, Consumer<BankingOperationBuilder<T>> builder) {
        HashMap<T, BiFunction<TransactionDTO , EntityManagerFactory, ResponseDTO>> registry = new HashMap<>();
        builder.accept(registry::put);
        return bankOperation -> registry.getOrDefault(bankOperation, (operation, emf) -> {
            throw new UnsupportedOperationException(givenBankOperation.getBankOperationType()+ " Operation Not supported yet");
        }).apply(givenBankOperation, givenEMF); 
    }
}
