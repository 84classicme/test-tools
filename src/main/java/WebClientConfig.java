import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

public class WebClientConfig {

    @Bean
    public WebClient reactiveSoapClient(){
        TcpClient tcpClient = TcpClient.create();

        tcpClient
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

//        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs( clientCodecConfigurer -> {
//            clientCodecConfigurer.customCodecs().register(new Jaxb2SoapEncoder());
//            clientCodecConfigurer.customCodecs().register(new Jaxb2SoapDecoder());
//        }).build();

        WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient).wiretap(true)))
//            .exchangeStrategies( exchangeStrategies )
            .build();

        return webClient;
    }
}
