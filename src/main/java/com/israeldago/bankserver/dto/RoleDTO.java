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

import com.israeldago.bankserver.persistence.entities.RoleDB;
import java.util.Objects;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class RoleDTO implements java.io.Serializable{
    
    private final Integer id;
    private final String roleName;

    private RoleDTO(Integer id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Integer id = -1;
        private String roleName = "undefined";

        private Builder() {}

        public Builder id(Integer id) {
            this.id = Objects.requireNonNull(id);
            return this;
        }

        public Builder roleName(String roleName) {
            this.roleName = Objects.requireNonNull(roleName);
            return this;
        }

        public RoleDTO build() {
            return new RoleDTO(id, roleName); 
        }
    }

    public Integer getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
    public RoleDB toRoleDB(){
        return new RoleDB(id, roleName);
    }      

    @Override
    public String toString() {
        return "RoleDTO{" + "id=" + id + ", roleName=" + roleName + '}';
    }
}
