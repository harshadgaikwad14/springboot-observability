package com.example.demo;


import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;


@Configuration
public class WebConfig {

  Logger log = LoggerFactory.getLogger(WebConfig.class);

  @Bean
  public WebClient webClient() {

    WebClient webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).filter(logRequest())
            .filter(logResponse())
            .build();

    return webClient;
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      log.info("Request {} {} {}", clientRequest.method(), clientRequest.url(),clientRequest.headers().toString());
      return Mono.just(clientRequest);
    });
  }

  private ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("Response status code {} ", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }

}