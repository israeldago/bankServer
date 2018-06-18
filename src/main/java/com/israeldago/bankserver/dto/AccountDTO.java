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

import com.israeldago.bankserver.persistence.entities.AccountDB;
import java.util.Objects;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class AccountDTO implements java.io.Serializable {

    private final Integer id;
    private final String iban;
    private final Double currentSold;
    private final String creationDate;
    private final AppUserDTO holder;

    private AccountDTO(Integer id, String iban, Double currentSold, String creationDate, AppUserDTO holder) {
        this.id = id;
        this.iban = iban;
        this.currentSold = currentSold;
        this.creationDate = creationDate;
        this.holder = holder;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer id;
        private String iban;
        private Double currentSold;
        private String creationDate;
        private AppUserDTO holder;

        private Builder() {}
        
        public Builder id(Integer id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder iban(String iban) {
            this.iban = Objects.requireNonNull(iban);
            return this;
        }
        
        public Builder currentSold(Double currentSold) {
            this.currentSold = Objects.requireNonNull(currentSold);
            return this;
        }

        public Builder creationDate(String creationDate) {
            this.creationDate = Objects.requireNonNull(creationDate);
            return this;
        }
        
        public Builder holder(AppUserDTO holder) {
            this.holder = Objects.requireNonNull(holder);
            return this;
        }

        public AccountDTO build() {
            return new AccountDTO(id, iban, currentSold, creationDate, holder); 
        }
    }

    public AccountDB toAccountDB(){
        return new AccountDB(this.id, this.currentSold, this.creationDate, this.iban, this.holder.toAppUserDB());
    }

    public Integer getId() {
        return id;
    }

    public String getIban() {
        return iban;
    }

    public Double getCurrentSold() {
        return currentSold;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public AppUserDTO getHolder() {
        return holder;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.iban);
        hash = 97 * hash + Objects.hashCode(this.creationDate);
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
        final AccountDTO other = (AccountDTO) obj;
        if (!Objects.equals(this.iban, other.iban)) {
            return false;
        }
        return Objects.equals(this.creationDate, other.creationDate);
    }

    @Override
    public String toString() {
        return "AccountDTO{" + "id=" + id + ", iban=" + iban + ", currentSold=" + currentSold + ", creationDate=" + creationDate + ", holder=" + holder + '}';
    }        
}
