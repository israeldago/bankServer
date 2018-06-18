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
import com.israeldago.bankserver.dto.RoleDTO;
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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
@Entity
@Table(name = "roles")
@XmlRootElement
@NamedQuery(name = "RoleDB.findAll", query = "SELECT r FROM RoleDB r")
@NamedQuery(name = "RoleDB.findByRoleName", query = "SELECT r FROM RoleDB r WHERE r.roleName = :roleName")
public class RoleDB implements Serializable , PersistenceEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "roleName")
    private String roleName;
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Collection<AppUserDB> appUserDBCollection;

    public RoleDB() {}

    public RoleDB(String roleName) {
        this(0, roleName);
    }
    
    public RoleDB(Integer id, String roleName) {
        this.id = Objects.requireNonNull(id);
        this.roleName = Objects.requireNonNull(roleName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = Objects.requireNonNull(roleName);
    }

    public Collection<AppUserDB> getAppUserDBCollection() {
        return appUserDBCollection;
    }

    public void setAppUserDBCollection(Collection<AppUserDB> appUserDBCollection) {
        this.appUserDBCollection = Objects.requireNonNull(appUserDBCollection);
    }
    
    public RoleDTO toRoleDTO(){
        return RoleDTO.builder().id(this.id).roleName(this.roleName).build();
    }  
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.roleName);
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
        final RoleDB other = (RoleDB) obj;
        if (!Objects.equals(this.roleName, other.roleName)) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }
    
    @Override
    public String toString() {
        return "RoleDB{" + "id=" + id + ", roleName=" + roleName + '}';
    }
}
