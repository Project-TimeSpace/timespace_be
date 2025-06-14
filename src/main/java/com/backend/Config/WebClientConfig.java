package com.backend.Config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    // ─── 공통 설정 ───────────────────────────────────────────────
    // 요청·응답 로깅 필터 (공통)
    // 개발환경에서만 사용하고, 배포 이후에는 출력 하지 않게 변경.
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            System.out.println("[WebClient][Request] " + req.method() + " " + req.url());
            return Mono.just(req);
        });
    }
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            System.out.println("[WebClient][Response] status=" + res.statusCode());
            return Mono.just(res);
        });
    }

    /** Reactor Netty connector 생성 헬퍼 */
    private ReactorClientHttpConnector connector(int connectTimeoutMs, int readTimeoutMs) {
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)  // 원격 서버에 TCP 연결을 시도할 때 제한 시간(ms)을 지정
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                );
        return new ReactorClientHttpConnector(http);
    }

    // 카카오 WebClient
    @Bean("kakaoWebClient")
    public WebClient kakaoWebClient(WebClient.Builder builder,
            @Value("${social.kakao.base-url}") String baseUrl,
            @Value("${social.kakao.connect-timeout:3000}") int connectTimeout,
            @Value("${social.kakao.read-timeout:3000}")    int readTimeout
    ) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .clientConnector(connector(connectTimeout, readTimeout))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    // 구글 WebClient
    /*
    @Bean("googleWebClient")
    public WebClient googleWebClient(
            WebClient.Builder builder,
            @Value("${social.google.base-url}") String baseUrl,
            @Value("${social.google.connect-timeout:3000}") int connectTimeout,
            @Value("${social.google.read-timeout:3000}")    int readTimeout
    ) {
        return builder
                .baseUrl(baseUrl)                           // ex: https://oauth2.googleapis.com
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(connector(connectTimeout, readTimeout))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    // 네이버 WebClient
    @Bean("naverWebClient")
    public WebClient naverWebClient(
            WebClient.Builder builder,
            @Value("${social.naver.base-url}") String baseUrl,
            @Value("${social.naver.connect-timeout:3000}") int connectTimeout,
            @Value("${social.naver.read-timeout:3000}")    int readTimeout
    ) {
        return builder
                .baseUrl(baseUrl)                           // ex: https://openapi.naver.com
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(connector(connectTimeout, readTimeout))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }
    */
}

