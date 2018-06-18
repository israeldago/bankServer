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

import com.israeldago.bankserver.persistence.entities.AppUserDB;
import java.util.Objects;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class AppUserDTO {

    private final Integer id;
    private final String lastName;
    private final String firstName;
    private final String birthDate;
    private final String identityCardNumber;
    private final String registerDate;
    private final String username;
    private final String password;
    private final RoleDTO roleDTO;

    private AppUserDTO(Integer id, String lastName, String firstName, String birthDate, String identityCardNumber, String registerDate, String username, String password, RoleDTO roleDTO) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.identityCardNumber = identityCardNumber;
        this.registerDate = registerDate;
        this.username = username;
        this.password = password;
        this.roleDTO = roleDTO;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer id = -1;
        private String lastName = "";
        private String firstName = "";
        private String birthDate = "";
        private String identityCardNumber = "";
        private String registerDate = "";
        private String username = "";
        private String password = "";
        private RoleDTO roleDTO = RoleDTO.builder().build();

        private Builder() {}

        public Builder id(Integer id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = Objects.requireNonNull(lastName);
            return this;
        }
        
        public Builder firstName(String firstName) {
            this.firstName = Objects.requireNonNull(firstName);
            return this;
        }
        
        public Builder birthDate(String birthDate) {
            this.birthDate = Objects.requireNonNull(birthDate);
            return this;
        }
        
        public Builder identityCardNumber(String identityCardNumber) {
            this.identityCardNumber = Objects.requireNonNull(identityCardNumber);
            return this;
        }
        
        public Builder registerDate(String registerDate) {
            this.registerDate = Objects.requireNonNull(registerDate);
            return this;
        }
        
        public Builder username(String username) {
            this.username = Objects.requireNonNull(username);
            return this;
        }
        
        public Builder password(String password) {
            this.password = Objects.requireNonNull(password);
            return this;
        }
        
        public Builder roleDTO(RoleDTO roleDTO) {
            this.roleDTO = Objects.requireNonNull(roleDTO);
            return this;
        }

        public AppUserDTO build() {
            return new AppUserDTO(id, lastName, firstName, birthDate, identityCardNumber, registerDate, username, password, roleDTO);
        }
    }

    public AppUserDB toAppUserDB() {
        return new AppUserDB(id, lastName, firstName, birthDate, identityCardNumber, registerDate, username, password, roleDTO.toRoleDB());
    }

    public Integer getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public RoleDTO getRoleDTO() {
        return roleDTO;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.identityCardNumber);
        hash = 97 * hash + Objects.hashCode(this.username);
        hash = 97 * hash + Objects.hashCode(this.password);
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
        final AppUserDTO other = (AppUserDTO) obj;
        if (!Objects.equals(this.identityCardNumber, other.identityCardNumber)) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return Objects.equals(this.password, other.password);
    }

    @Override
    public String toString() {
        return "AppUserDTO{" + "id=" + id + ", lastName=" + lastName + ", firstName=" + firstName + ", birthDate=" + birthDate + ", identityCardNumber=" + identityCardNumber + ", registerDate=" + registerDate + ", username=" + username + ", password=" + password + ", roleDTO=" + roleDTO + '}';
    }
}
