//package com.example;
//
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.netty.http.client.HttpClient;
//import reactor.netty.tcp.TcpClient;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class WebClientConfig {
//
//    private static final int APPLICATION_TIMEOUT_MILLIS = 10000;
//
//    @Bean
//    public WebClient getReactiveRestClient(){
//        TcpClient tcpClient = TcpClient.create();
//
//        tcpClient
//            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, APPLICATION_TIMEOUT_MILLIS)
//            .doOnConnected(connection -> {
//                connection.addHandlerLast(new ReadTimeoutHandler(APPLICATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
//                connection.addHandlerLast(new WriteTimeoutHandler(APPLICATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
//            });
//
//        WebClient webClient = WebClient.builder()
//            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient).wiretap(true)))
//            .build();
//
//        return webClient;
//    }
//
//}
