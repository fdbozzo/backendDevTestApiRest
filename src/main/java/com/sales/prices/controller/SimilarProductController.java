package com.sales.prices.controller;

import com.sales.prices.client.SimilarProductRestClient;
import com.sales.prices.domain.ProductDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
@Slf4j
public class SimilarProductController {

  @Autowired
  SimilarProductRestClient similarProductRestClient;

  @GetMapping("/{productId}/similar")
  public Flux<ProductDetail> getSimilarProductsByProductId(@PathVariable("productId") String productId) {
    return similarProductRestClient
        .getSimilarProductsByProductId(productId);
  }

}
