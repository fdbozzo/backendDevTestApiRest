package com.sales.prices.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.prices.domain.ProductDetail;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SimilarProductRestClient {

  private final WebClient webClient;

  public SimilarProductRestClient(WebClient webClient) {
    this.webClient = webClient;
  }

  @Value("${restClient.productUrl}")
  private String productUrl;

  public Flux<Integer> getSimilarProductIdsByProductId(String productId) {
    var response = webClient
        .get()
        .uri(productUrl + "/{productId}/similarids", productId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(String.class)
        .flatMap(idList -> {
          System.out.println(idList);
          var arr = Arrays.stream(idList
                  .replace("[", "")
                  .replace ("]", "")
                  .split (","))
              .map(id -> {
                System.out.println("id: " + id);
                //return id;
                return Integer.valueOf(id);
              });
          var flx = Flux.fromStream(arr);

          return flx;
        })
        .log();

    return response;
  }

  public Mono<ProductDetail> getProductById(String productId) {
    var response = webClient
        .get()
        .uri(productUrl + "/{productId}", productId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToMono(ProductDetail.class)
        .log();

    return response;
  }

  public Flux<ProductDetail> getSimilarProductsByProductId(String productId) {
    var idsFlux = this.getSimilarProductIdsByProductId((productId));

    var response = idsFlux
        .flatMap(id -> this.getProductById(id.toString()));

    return response;
  }

}
