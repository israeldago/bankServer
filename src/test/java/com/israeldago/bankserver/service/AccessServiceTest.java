/*
 * The MIT License
 *
 * Copyright 2018 Israel Dago at https://github.com/israeldago.
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
package com.israeldago.bankserver.service;

import com.israeldago.bankserver.dto.AppUserDTO;
import com.israeldago.bankserver.dto.RoleDTO;
import java.io.IOException;
import java.security.AccessControlException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.crypto.AEADBadTagException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import static org.apache.commons.lang.RandomStringUtils.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
//@RunWith(JUnit4ClassRunner.class)
public class AccessServiceTest {

    private static final Supplier<EntityManagerFactory> EMF = () -> /*null; */ Persistence.createEntityManagerFactory("com.israeldago_bankServer_jar_1.0.0PUTestDB");

    @Test @Ignore
    public void testUseService_Provided_NotNull() {
        assertThat(EMF.get()).isNotNull();
    }

    @Test @Ignore
    public void testUseService_NotNull() {
        AccessService.use(EMF, service -> assertThat(service).isNotNull() , Throwable::printStackTrace);
    }

    @Test @Ignore
    public void testRegisterRandomUserAsync() {
        AccessService.use(EMF, service -> {
            service.registerUserAsync(createRandomUser())
                   .thenAcceptAsync(userRegistered -> assertTrue(userRegistered));
        }, Throwable::printStackTrace);

    }

    @Test @Ignore
    public void given_a_registered_user_then_test_IDCARD_are_the_same_at_login() {
        AccessService.use(EMF, service -> {
            AppUserDTO userToLogIn = createRandomUser();
            service.registerUserAsync(userToLogIn)
                    .thenRunAsync(() -> {
                        service.loginUserAsync(userToLogIn.getUsername(), userToLogIn.getPassword())
                                .thenAcceptAsync(loggedUser -> assertEquals(userToLogIn.getIdentityCardNumber(), loggedUser.get().getIdentityCardNumber()));
                    });
        }, Throwable::printStackTrace);
    }
    
    @Test
    public void given_an_user_then_register_him_twice() {
        AccessService.use(EMF, service -> {
            AppUserDTO userToLogIn = createRandomUser();
            service.registerUserAsync(userToLogIn)
                    .thenRunAsync(() -> {
                        service.registerUserAsync(userToLogIn)
                                .exceptionally(error -> { 
                                   System.out.println(">>>> "+ error);
                                    assertThat(error)
                                         .hasCauseInstanceOf(Error.class);
                                    throw new RuntimeException(error);});
                                
                    });
        }, Throwable::printStackTrace);
        
        ForkJoinPool.commonPool().awaitQuiescence(15, TimeUnit.SECONDS);
    }

   
    public void await5SecQuiescenceFromCommonPool() {
        ForkJoinPool.commonPool().awaitQuiescence(5, TimeUnit.SECONDS);
    }

    private AppUserDTO createRandomUser() {
        return AppUserDTO.builder()
                        .birthDate(LocalDate.now().minusYears(Long.parseLong(randomNumeric(2))).toString())
                        .firstName(randomAlphabetic(7))
                        .lastName(randomAlphabetic(5))
                        .identityCardNumber(randomAlphanumeric(15))
                        .password(randomAlphanumeric(8))
                        .registerDate(LocalDateTime.now().toString())
                        .username(randomAlphabetic(6))
                        .roleDTO(RoleDTO.builder().roleName(randomAlphabetic(5)).build())
                        .build();
    }

}
