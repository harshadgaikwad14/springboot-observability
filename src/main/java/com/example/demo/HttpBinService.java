package com.example.demo;

import io.micrometer.observation.annotation.Observed;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class HttpBinService {

    //@Autowired
    private WebClient webClient;

    public HttpBinService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Observed(name = "http.bin.service.retrieve.data")
    public String retrieveData() {
        String sampleResponse = null;
        try {
            Random random = new Random();
            int randomNum = random.nextInt(9000) + 1000;
            Thread.sleep(randomNum);
            sampleResponse = "Test Data with Sleep Time "+randomNum;
            throw new RuntimeException();
            //sampleResponse = restTemplate.getForObject("https://httpbin.org/get/123", String.class);
        } catch (HttpServerErrorException httpServerErrorException) {
            System.out.println("Received HTTP server error exception while fetching the details. Error Message: " + httpServerErrorException.getMessage());
            throw httpServerErrorException;
        } catch (HttpClientErrorException httpClientErrorException) {
            System.out.println("Received HTTP client error exception while fetching the details. Error Message: " + httpClientErrorException.getMessage());
            throw httpClientErrorException;
        } catch (ResourceAccessException resourceAccessException) {
            System.out.println("Received Resource Access exception while fetching the details.");
            throw resourceAccessException;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Observed(name = "http.hello.world.service.retrieve.data")
    public String hello() throws Exception {

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImV4cCI6MTkxODE0OTE5M30.TAbp_WbtpYfWnPhBEhqHGwxqz2oeyYQlN4tN75w-i_M";


        String employeeMono;
        employeeMono = webClient.get()
                .uri("http://localhost:8081/rest/hello")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()

                .onStatus(HttpStatusCode::is4xxClientError,
                    (e) -> getIs4xxClientError(e))

                .onStatus(HttpStatusCode::is5xxServerError,
                    (e) -> getIs5xxServerError(e, "is5xxServerError"))

                .onStatus(HttpStatusCode::isError,
                    (e) -> getIsError(e, "isError"))

                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(1000))

                .onErrorMap(WebClientRequestException.class, ex -> {
                    System.out.println("WebClientRequestException ::  "+ex.getMessage());
                    return new ApplicationException(HttpStatusCode.valueOf(500),"WebClientRequestException");
                })


                .onErrorMap(TimeoutException.class, ex -> {
                    System.out.println("TimeoutException ::  "+ex.getMessage());
                    return new ApplicationException(HttpStatusCode.valueOf(500),"TimeoutException");
                })

                .onErrorMap(WebClientException.class, ex -> {
                    System.out.println("WebClientException ::  "+ex.getMessage());
                    return new ApplicationException(HttpStatusCode.valueOf(500),"WebClientException");
                })
                .block();
        return employeeMono;




    }

    private  Mono<Throwable> getIsError(ClientResponse e, String isError) {
        return e.bodyToMono(String.class)
                .handle(
                        (body, handler) -> {
                            handler.error(new ApplicationException(e.statusCode(), isError));
                        });
    }

    private  Mono<Throwable> getIs5xxServerError(ClientResponse e, String is5xxServerError) {
        return getIsError(e, is5xxServerError);
    }

    private  Mono<Throwable> getIs4xxClientError(ClientResponse e) {
        return e.bodyToMono(String.class)
                .handle(
                        (body, handler) -> {
                            System.out.println("body : " + body + ">>>" + e.statusCode().value());
                            handler.error(new ApplicationException(e.statusCode(), "is4xxClientError"));
                        });
    }

}