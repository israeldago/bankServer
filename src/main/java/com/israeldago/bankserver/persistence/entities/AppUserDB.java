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
package com.israeldago.bankserver.persistence.entities;

import com.israeldago.bankserver.persistence.PersistenceEntity;
import com.israeldago.bankserver.dto.AppUserDTO;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
@Entity
@Table(name = "app_users")
@XmlRootElement
@NamedQuery(name = "AppUserDB.findAll", query = "SELECT a FROM AppUserDB a")
@NamedQuery(name = "AppUserDB.findByIdentityCardNumber", query = "SELECT a FROM AppUserDB a WHERE a.identityCardNumber = :identityCardNumber")
@NamedQuery(name = "AppUserDB.findByUsername", query = "SELECT a FROM AppUserDB a WHERE a.username = :username")
public class AppUserDB implements Serializable, PersistenceEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "birthDate")
    private String birthDate;
    @Column(name = "identityCardNumber")
    private String identityCardNumber;
    @Column(name = "registerDate")
    private String registerDate;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @JoinColumn(name = "role", referencedColumnName = "id")
    @ManyToOne(cascade = CascadeType.ALL)
    private RoleDB role;
    @OneToMany(mappedBy = "holder")
    private Collection<AccountDB> accountDBCollection;

    public AppUserDB() {}

    public AppUserDB(AppUserDB current) {
        this(current.id, current.lastName, current.firstName, current.birthDate, current.identityCardNumber, current.registerDate, current.username, current.password, current.role);
    }

    public AppUserDB(Integer id, String lastName, String firstName, String birthDate, String identityCardNumber, String registerDate, String username, String password, RoleDB role) {
        this.id = Objects.requireNonNull(id);
        this.lastName = Objects.requireNonNull(lastName);
        this.firstName = Objects.requireNonNull(firstName);
        this.birthDate = Objects.requireNonNull(birthDate);
        this.identityCardNumber = Objects.requireNonNull(identityCardNumber);
        this.registerDate = Objects.requireNonNull(registerDate);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.role = Objects.requireNonNull(role);
    }

    public AppUserDTO toAppUserDTO() {
        return AppUserDTO.builder()
                .id(this.id)
                .lastName(this.lastName)
                .firstName(this.firstName)
                .identityCardNumber(this.identityCardNumber)
                .registerDate(this.registerDate)
                .username(this.username)
                .password(this.password)
                .roleDTO(this.role.toRoleDTO())
                .build();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleDB getRole() {
        return role;
    }

    public void setRole(RoleDB role) {
        this.role = role;
    }

    public Collection<AccountDB> getAccountDBCollection() {
        return accountDBCollection;
    }

    public void setAccountDBCollection(Collection<AccountDB> accountDBCollection) {
        this.accountDBCollection = accountDBCollection;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.identityCardNumber);
        hash = 29 * hash + Objects.hashCode(this.username);
        hash = 29 * hash + Objects.hashCode(this.password);
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
        final AppUserDB other = (AppUserDB) obj;
        if (!Objects.equals(this.identityCardNumber, other.identityCardNumber)) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "AppUserDB{" + "id=" + id + ", lastName=" + lastName + ", firstName=" + firstName + ", birthDate=" + birthDate + ", identityCardNumber=" + identityCardNumber + ", registerDate=" + registerDate + ", username=" + username + ", password=" + password + ", role=" + role + '}';
    }   
}
