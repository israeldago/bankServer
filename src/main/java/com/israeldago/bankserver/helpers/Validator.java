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
package com.israeldago.bankserver.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 * @param <T>
 */
public final class Validator<T> {
    private final T validatedObject;
    private final List<Throwable> THROWABLES = new ArrayList<>();

    private Validator(T validatedObject) {
        this.validatedObject = Objects.requireNonNull(validatedObject, () -> "The Object to validate cannot be null");
    }
    
    public static <T> Validator<T> of(T objToValidate) {
        return new Validator<>(objToValidate);
    }

    
    public Validator<T> testing (Predicate<T> condition, Supplier<String> errorMessage) {
        if(!condition.test(validatedObject)){
            THROWABLES.add(new IllegalStateException(errorMessage.get()));
        }
        return this;
    }
    
    public <U> Validator<T> testing (Function<T,U> projection, Predicate<U> condition, Supplier<String> errorMessage) {
       return Validator.this.testing(projection.andThen(condition::test)::apply, errorMessage);
    }
    
    
    public T validate() throws IllegalStateException{
        if(THROWABLES.isEmpty()){ return validatedObject;}
        IllegalStateException exp = new IllegalStateException();
        THROWABLES.forEach(exp::addSuppressed);
        throw exp;
    }    
    
    public void whenCompletedAccept(Consumer<T> tConsumer , Consumer<Throwable> errorConsumer) {
        try {
            tConsumer.accept(this.validate());
        } catch (IllegalStateException error) {
            errorConsumer.accept(error);
        }
    } 
    
    public <U> Optional<U> whenCompletedApply(Function<T, U> tConsumer , Consumer<Throwable> errorConsumer) {
        try {
            return Optional.ofNullable(tConsumer.apply(this.validate()));
        } catch (IllegalStateException error) {
            errorConsumer.accept(error);
            return Optional.empty();
        }       
    } 
}
