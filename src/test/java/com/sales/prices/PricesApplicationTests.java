package com.sales.prices;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.sales.prices.domain.ProductDetail;
import com.sales.prices.domain.SimilarProducts;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084) // Automaticamente levanta un server http en puerto 8084
@TestPropertySource(
		properties = {
				"restClient.productUrl=http://localhost:8084/product"
		}
)
class PricesApplicationTests {

	@Autowired
	WebTestClient webTestClient;

	@Test
	void getSimilarProductsByProductId_1() {
		//given
		var productId = "1";

		stubFor(get(urlEqualTo("/product" + "/" + productId + "/similarids"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("ids-234.json")
				));

//		stubFor(get(urlEqualTo("/v1/movieinfos" + "/" + productId))
//				.willReturn(aResponse()
//						.withHeader("Content-Type", "application/json")
//						.withBodyFile("movieinfo.json")
//				));

//		stubFor(get(urlPathEqualTo("/v1/reviews"))
//				.willReturn(aResponse()
//						.withHeader("Content-Type", "application/json")
//						.withBodyFile("reviews.json")
//				));

		//when
		webTestClient
				.get()
				.uri("/product/{productId}/similar", productId)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ProductDetail.class)
				.consumeWith(stringEntityExchangeResult -> {
					var product = stringEntityExchangeResult.getResponseBody();
					//assert Objects.requireNonNull(movie).getReviewList().size() == 2;
					//assertEquals("Batman Begins", movie.getMovieInfo().getName());
					System.out.println("product: " + product);
				});

		//then
		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product" + "/" + productId + "/similarids")));

	}

}
