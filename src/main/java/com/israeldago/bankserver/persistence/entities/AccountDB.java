/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.israeldago.bankserver.persistence.entities;

import com.israeldago.bankserver.persistence.PersistenceEntity;
import com.israeldago.bankserver.dto.AccountDTO;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
@Entity
@Table(name = "accounts")
@XmlRootElement
@NamedQuery(name = "AccountDB.findAll", query = "SELECT a FROM AccountDB a")
public class AccountDB implements Serializable , PersistenceEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "amount")
    private Double currentSold;
    @Column(name = "creationDate")
    private String creationDate;
    @Column(name = "iban")
    private String iban;
    @JoinColumn(name = "holder", referencedColumnName = "id")
    @ManyToOne
    private AppUserDB holder;

    public AccountDB() {}
    
    public AccountDB(Double currentSold, String creationDate, String iban, AppUserDB holder) {
        this(0, currentSold, creationDate, iban, holder);
    }
  
    public AccountDB(Integer id, Double currentSold, String creationDate, String iban, AppUserDB holder) {
        this.id = Objects.requireNonNull(id);
        this.currentSold = Objects.requireNonNull(currentSold);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.iban = Objects.requireNonNull(iban);
        this.holder = Objects.requireNonNull(holder);
    }
    
   public AccountDTO toAccountDTO(){
        return AccountDTO.builder()
                .id(this.id)
                .iban(this.iban)
                .currentSold(this.currentSold)
                .creationDate(this.creationDate)
                .holder(this.holder.toAppUserDTO())
                .build();
    }
    
    public Integer getHolderId(){
        return this.getHolder().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCurrentSold() {
        return currentSold;
    }

    public void setCurrentSold(Double currentSold) {
        this.currentSold = currentSold;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public AppUserDB getHolder() {
        return holder;
    }

    public void setHolder(AppUserDB holder) {
        this.holder = holder;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.creationDate);
        hash = 53 * hash + Objects.hashCode(this.iban);
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
        final AccountDB other = (AccountDB) obj;
        if (!Objects.equals(this.creationDate, other.creationDate)) {
            return false;
        }
        return Objects.equals(this.iban, other.iban);
    }
    
    @Override
    public String toString() {
        return "AccountDB{" + "id=" + id + ", currentSold=" + currentSold + ", creationDate=" + creationDate + ", iban=" + iban + ", holder=" + holder + '}';
    }
}
