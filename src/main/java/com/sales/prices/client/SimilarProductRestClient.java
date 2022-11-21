package com.sales.prices.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sales.prices.domain.ProductDetail;
import com.sales.prices.domain.SimilarProducts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

  public Flux<ProductDetail> getSimilarProductsByProductId(String productId) {
    ObjectMapper objectMapper = new ObjectMapper();
    return webClient
        .get()
        .uri(productUrl + "/{productId}/similarids", productId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(String.class)
//        .flatMap(similarProductIds -> {
//          log.info("similarProductIds: {}", similarProductIds);
//          List<String> list = null;
//          try {
//            list = objectMapper.readerForListOf(String.class).readValue(similarProductIds);
//
//          } catch (IOException e) {
//            log.error("Exception: {e}", e);
//            throw new RuntimeException(e);
//          }
//
//          List<ProductDetail> productDetailList = new ArrayList<>();
//
//          for (String id : list) {
//            productDetailList.add(new ProductDetail(id, "name", 10.11, true));
//          }
//
//          return Flux.fromIterable(productDetailList);
//        })
        .map(similarProductIds -> {
          log.info("similarProductIds: {}", similarProductIds);
          List<String> list;
          try {
            list = objectMapper.readerForListOf(String.class).readValue(similarProductIds);

          } catch (IOException e) {
            log.error("Exception: {e}", e);
            throw new RuntimeException(e);
          }

          return list;
        })
        .flatMap(idList -> Flux.fromStream(
                idList.stream()
                    .map(s -> {
                      System.out.println("s = " + s);
                      return new ProductDetail(s, "name", 10.11, true);
                    })
            )
        )

//          List<ProductDetail> productDetailList = new ArrayList<>();
//
//          for (String id : idList) {
//            productDetailList.add(new ProductDetail(id, "name", 10.11, true));
//          }
//
//          return Flux.fromIterable(productDetailList);
//        )
//        .flatMap(obj -> {
//          System.out.println(obj);
//          return new ProductDetail("1", "name", 10.11, true);
//        })
        ;
  }

}
