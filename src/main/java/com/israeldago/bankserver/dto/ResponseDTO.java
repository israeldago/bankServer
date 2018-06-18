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
package com.israeldago.bankserver.dto;

import java.util.Objects;
/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class ResponseDTO implements java.io.Serializable {

    private final String message;
    private final Boolean taskStatus;
    private final TransactionDTO originalTransaction;

    public ResponseDTO(ResponseDTO curentResponseDTO, TransactionDTO originalTransactionDTO) {
        this(curentResponseDTO.getMessage(), curentResponseDTO.getTaskStatus(), originalTransactionDTO);
    }

    public ResponseDTO(String message, Boolean taskStatus, TransactionDTO originalTransaction) {
        this.message = Objects.requireNonNull(message);
        this.taskStatus = Objects.requireNonNull(taskStatus);
        this.originalTransaction = Objects.requireNonNull(originalTransaction);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String message = "";
        private Boolean taskStatus = false;
        private TransactionDTO originalTransaction = TransactionDTO.builder().build();

        private Builder() {}

        public Builder message(String message) {
            this.message = Objects.requireNonNull(message);
            return this;
        }
        
        public Builder taskStatus(Boolean taskStatus) {
            this.taskStatus = Objects.requireNonNull(taskStatus);
            return this;
        }

        public Builder originalTransaction(TransactionDTO originalTransaction) {
            this.originalTransaction = Objects.requireNonNull(originalTransaction);
            return this;
        }

        public ResponseDTO build() {
            return new ResponseDTO(message, taskStatus, originalTransaction);
        }
    }

    public String getMessage() {
        return message;
    }

    public Boolean getTaskStatus() {
        return taskStatus;
    }

    public TransactionDTO getOriginalTransaction() {
        return originalTransaction;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.taskStatus);
        hash = 23 * hash + Objects.hashCode(this.originalTransaction);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResponseDTO other = (ResponseDTO) obj;
        if (!Objects.equals(this.taskStatus, other.taskStatus)) {
            return false;
        }
        return Objects.equals(this.originalTransaction, other.originalTransaction);
    }

    @Override
    public String toString() {
        return "ResponseDTO{" + "message=" + message + ", taskStatus=" + taskStatus + ", originalTransaction=" + originalTransaction + '}';
    }
}
