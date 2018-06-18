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
package com.israeldago.bankserver.persistence;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @author Israel Dago at https://github.com/ivoireNoire
 * @param <T> representing the concrete Entity type
 */
public final class DAO<T extends PersistenceEntity> {

    private final Class<T> entityClass;
    private final EntityManager ENTITY_MANAGER;

    private DAO(Class<T> entityClass, EntityManager em) {
        this.entityClass = entityClass;
        ENTITY_MANAGER = em;
    }

    public static <T extends PersistenceEntity> DAO<T> of(final Class<T> entityClass, final Supplier<EntityManager> entityManagerSupplier) {
        return new DAO<>(entityClass, entityManagerSupplier.get());
    }

    public void persist(final T entity) {
        Objects.requireNonNull(entity);
        ENTITY_MANAGER.persist(entity);
    }

    public Optional<T> edit(final T entity) {
        Objects.requireNonNull(entity);
        return Optional.of(ENTITY_MANAGER.merge(entity));
    }

    public void remove(final T entity) {
        Objects.requireNonNull(entity);
        ENTITY_MANAGER.remove(ENTITY_MANAGER.merge(entity));
    }

    public Optional<T> findOne(final Integer id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(ENTITY_MANAGER.find(entityClass, id));
    }

    public Stream<T> findAll() {
        CriteriaQuery cq = ENTITY_MANAGER.getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return ENTITY_MANAGER.createQuery(cq).getResultStream();
    }

    public Optional<T> findByQueryWithOneParam(final String namedQuery, String paramKey, String queryValue) {
        Objects.requireNonNull(namedQuery);
        return ENTITY_MANAGER.createNamedQuery(namedQuery, entityClass)
                .setParameter(paramKey, queryValue)
                .getResultStream()
                .findFirst();
    }

    public Stream<T> findByQueryWithMultipleParams(final String namedQuery, Map<String, String> queryParams) {
        Objects.requireNonNull(namedQuery);
        TypedQuery<T> query = ENTITY_MANAGER.createNamedQuery(namedQuery, entityClass);
        Optional.ofNullable(queryParams).ifPresent(params -> params.entrySet().forEach(key -> query.setParameter(key.getKey(), key.getValue())));
        return query.getResultStream();
    }
}
