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

import com.israeldago.bankserver.dto.enums.BankOperationType;
import static com.israeldago.bankserver.dto.enums.BankOperationType.UNDEFINED;
import java.util.Objects;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class TransactionDTO implements java.io.Serializable {

    private final Integer depositAccoutId;
    private final Integer withdrawAccountId;
    private final Double amount;
    private final BankOperationType bankOperationType;

    public static Builder builder() {
        return new Builder();
    }

    private TransactionDTO(Integer depositAccoutId, Integer withdrawAccountId, Double amount, BankOperationType bankOperationType) {
        this.depositAccoutId = depositAccoutId == null ?  -1 : depositAccoutId;
        this.withdrawAccountId = withdrawAccountId == null ?  -1 : withdrawAccountId;
        this.amount = amount;
        this.bankOperationType = bankOperationType;
    }
    
    public static class Builder {

        private Integer depositAccoutId = -1;
        private Integer withdrawAccountId = -1;
        private Double amount = 0d;
        private BankOperationType bankOperationType = UNDEFINED;

        private Builder() {}

        public Builder depositAccoutId(Integer depositAccoutId) {
            this.depositAccoutId = Objects.requireNonNull(depositAccoutId);
            return this;
        }
        
        public Builder withdrawAccountId(Integer withdrawAccountId) {
            this.withdrawAccountId = Objects.requireNonNull(withdrawAccountId);
            return this;
        }
        
        public Builder amount(Double amount) {
            this.amount = Objects.requireNonNull(amount);
            return this;
        }

        public Builder bankOperationType(BankOperationType bankOperationType) {
            this.bankOperationType = Objects.requireNonNull(bankOperationType);
            return this;
        }

        public TransactionDTO build() {
            return new TransactionDTO(depositAccoutId, withdrawAccountId, amount, bankOperationType);
        }
    }

    public Integer getDepositAccoutId() {
        return depositAccoutId;
    }

    public Integer getWithdrawAccountId() {
        return withdrawAccountId;
    }

    public Double getAmount() {
        return amount;
    }

    public BankOperationType getBankOperationType() {
        return bankOperationType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.depositAccoutId);
        hash = 83 * hash + Objects.hashCode(this.amount);
        hash = 83 * hash + Objects.hashCode(this.bankOperationType);
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
        final TransactionDTO other = (TransactionDTO) obj;
        if (!Objects.equals(this.depositAccoutId, other.depositAccoutId)) {
            return false;
        }
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        return this.bankOperationType == other.bankOperationType;
    }

    @Override
    public String toString() {
        return "TransactionDTO{" + "depositAccoutId=" + depositAccoutId + ", withdrawAccountId=" + withdrawAccountId + ", amount=" + amount + ", bankOperationType=" + bankOperationType + '}';
    }
}
