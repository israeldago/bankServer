/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.israeldago.bankserver.main;

import com.israeldago.bankserver.dto.AccountDTO;
import com.israeldago.bankserver.dto.AppUserDTO;
import com.israeldago.bankserver.dto.ResponseDTO;
import com.israeldago.bankserver.dto.RoleDTO;
import com.israeldago.bankserver.dto.TransactionDTO;
import com.israeldago.bankserver.dto.enums.BankOperationType;
import com.israeldago.bankserver.service.AccessService;
import com.israeldago.bankserver.service.BankingService;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.reactivex.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import static io.vertx.core.http.HttpServerOptions.DEFAULT_HTTP2_CONNECTION_WINDOW_SIZE;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.FlowableHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import javax.persistence.Persistence;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Israel Dago at https://github.com/israeldago
 */
public class Main extends AbstractVerticle {

    private static final Vertx myVertx = Vertx.vertx();

    @Override
    public void start() throws Exception {
        HttpServerOptions config = new HttpServerOptions().setPort(9609).setHost("localhost").addCrlPath("qwerty");
        HttpServer server = myVertx.createHttpServer(config);
        server.requestStream().toFlowable().subscribe(this::handleRequest);
        server.rxListen()
                .subscribe(ser -> System.out.println("connected on port " + ser.actualPort()));
                //.listen(this::handleconnection);

    }

    private void handleRequest(HttpServerRequest request) {
        System.out.println(request.uri());
        System.out.println("host >>>" + " *-----* " + request.version() + " ** " + request.absoluteURI());

        if (!"/".equals(request.uri())) {
            request.response().end("got -> " + request.uri());
        } else {
            request.response().end("welcome to the jungle");
        }
        request.connection().closeHandler(con -> System.out.println("Connection close")).exceptionHandler(Throwable::printStackTrace).close();
    }

    private void handleconnection(AsyncResult<HttpServer> ar) {
        if (ar.failed()) {
            System.out.println("Server failed deployed -> " + ar.cause().getMessage());
        } else {
            System.out.println("Deployed on port -> " + ar.result().actualPort());

        }
    }

    public static void main(String[] args) throws Exception {
        
        
        FileSystem fileSystem = myVertx.fileSystem();
fileSystem.open("../pom.xmls", new OpenOptions(), result -> {
    result.result().toFlowable()
                    .subscribe(System.out::println, Throwable::printStackTrace, () -> System.out.println("COMPLETE"));
   //                .forEach(data -> System.out.println("Read data: " + data.toString("UTF-8")));
   

});


        DeploymentOptions opt = new DeploymentOptions().setInstances(3);
        myVertx.deployVerticle(Main.class.getName());

        Thread.sleep(4000);

        myVertx.createHttpClient().getNow(9609, "localhost", "/bnm",
                response -> {
                    response.exceptionHandler(Throwable::printStackTrace);

                    response.handler(body -> {
                        System.out.println("RESPONSE : " + body.toString());
                    });
                });
    }

//    @SuppressWarnings("CallToPrintStackTrace")
//    public static void main(String[] args) {
//        
//        System.out.println(new Main());
////
////        AccessService.use(() -> Persistence.createEntityManagerFactory("com.israeldago_bankServer_jar_1.0.0PUTestDB") , service -> {
////            AppUserDTO toRegister = AppUserDTO.builder()
////                    .birthDate("03-05-1985")
////                    .firstName("Pascalle")
////                    .lastName("N'Goa")
////                    .identityCardNumber("NzL5HXUsgqndterqa")
////                    .password("abcd")
////                    .registerDate(LocalDateTime.now().toString())
////                    .username("pgoaaa")
////                    //  .roleDTO(new RoleDTO("simple_user"))
////                    .build();
////            service.registerUserAsync(toRegister)
////                    .exceptionally(th -> {th.printStackTrace(); return null;})
////                            .thenAcceptAsync( userRegistered -> System.out.println("userRegistered =>  " + userRegistered))
////                            .thenRunAsync(() -> {
////                                service.loginUserAsync("pgoaaa", "abcd")
////                                   .exceptionally(th -> {
////                                       th.printStackTrace();
////                                       return null;
////                                   })
////                                   .thenAcceptAsync(user -> {
////                                       System.out.println("LOG IN USER =>  " + user);
////                                   });
////                            });
////            
////
////        }, Throwable::printStackTrace);
//
//        
//        
//        
//        
//        
//        
//      /*  
//        BankingService.use(Bank -> {
//                                Bank.makeBankingOperationAsync(TransactionDTO.builder()
//                                 .amount(-50d)
//                                        .bankOperationType(BankOperationType.DEPOSIT)
//                                       // .withdrawAccountId(33)
//                                        .depositAccoutId(38)
//                                        .build())
//                                        .exceptionally(Main::processError)
//                                        .thenAcceptAsync(System.out::println);
////                                Bank.deleteAccountAsync(37, 1)
////                                        .exceptionally(th -> {th.printStackTrace(); return false;})
////                                        .exceptionally(th -> {th.printStackTrace(); return false;})
//////                                       .thenAcceptAsync(System.out::println);
////                                        
////                                Bank.createNewAccountForUserAsync(1)
////                                        .exceptionally(th -> {th.printStackTrace(); System.out.println("IN ERROR BLOCK");return null;})
////                                       .thenAcceptAsync(System.out::println);
//
////                                Bank.allAccountsAsync()
////                                                    .thenAcceptAsync(str -> str
////                                                            .map(AccountDTO::getIban)
////                                                            .forEach(System.out::println));
//        }, Throwable::printStackTrace); */
//
//        ForkJoinPool.commonPool().awaitQuiescence(5, TimeUnit.SECONDS);
//    }
    public static ResponseDTO processError(Throwable throwable) {
        throwable.printStackTrace();
        return ResponseDTO.builder().message(throwable.getMessage()).taskStatus(false).build();
    }
}
