package com.sales.prices;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.sales.prices.domain.ProductDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
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

	@Value("${restClient.productUrl}")
	private String productUrl;

	@Test
	void getProductById_1() {
		var productId = "1";

		stubFor(get(urlEqualTo("/product" + "/" + productId))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("product-1.json")
				));

		webTestClient
				.get()
				.uri("/product" + "/{productId}", productId)
				.exchange()
				.expectStatus().isOk()
				//.expectBody(ProductDetail.class)
				.expectBody(ProductDetail.class)
				.consumeWith(productDetailEntityExchangeResult -> {
					var product = productDetailEntityExchangeResult.getResponseBody();
					//assert Objects.requireNonNull(movie).getReviewList().size() == 2;
					//assertEquals("Batman Begins", movie.getMovieInfo().getName());
					System.out.println("product: " + product);
				});

		//then
		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product" + "/" + productId)));

	}

	@Test
	void getProductById_1_notFound() {
		var productId = "1";

		stubFor(get(urlEqualTo("/product" + "/" + productId))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
								.withStatus(404)
						//.withBodyFile("product-1.json")
				));

		webTestClient
				.get()
				.uri("/product" + "/{productId}", productId)
				.exchange()
				.expectStatus().isNotFound()
//				.expectBody(String.class)
//				.consumeWith(productDetailEntityExchangeResult -> {
//					var product = productDetailEntityExchangeResult.getResponseBody();
//					//assert Objects.requireNonNull(movie).getReviewList().size() == 2;
//					//assertEquals("Batman Begins", movie.getMovieInfo().getName());
//					System.out.println("product: " + product);
//				})
		;

		//then
		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product" + "/" + productId)));

	}

	@Test
	void getSimilarProductsIdsByProductId_1() {
		//given
		var productId = "1";

		stubFor(get(urlEqualTo("/product" + "/" + productId + "/similarids"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("ids-234.json")
				));

		//when
		webTestClient
				.get()
				.uri("/product/{productId}/similarids", productId)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Integer.class)
				.consumeWith(listEntityExchangeResult -> {
					var id = listEntityExchangeResult.getResponseBody();
					//assert Objects.requireNonNull(movie).getReviewList().size() == 2;
					//assertEquals("Batman Begins", movie.getMovieInfo().getName());
					System.out.println("(test) id: " + id);
				});

		//then
//		WireMock.verify(2,
//				getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));

	}

	@Test
	void getSimilarProductsByProductId_1() {
		//given
		var productId = "1";

		stubFor(get(urlEqualTo("/product/" + productId + "/similarids"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("ids-234.json")
				));

		stubFor(get(urlEqualTo("/product/2"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("product-2.json")
				));

		stubFor(get(urlEqualTo("/product/3"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBodyFile("product-3.json")
				));

		stubFor(get(urlEqualTo("/product/4"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
								.withStatus(404)
						//.withBodyFile("product-4.json")
				));


		//when
		webTestClient
				.get()
				.uri("/product/{productId}/similar", productId)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ProductDetail.class)
				.consumeWith(listEntityExchangeResult -> {
					var product = listEntityExchangeResult.getResponseBody();
					//assert Objects.requireNonNull(movie).getReviewList().size() == 2;
					//assertEquals("Batman Begins", movie.getMovieInfo().getName());
					System.out.println("product: " + product);
				});

		//then
		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product/" + productId + "/similarids")));

		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product/2")));

		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product/3")));

		WireMock.verify(1,
				getRequestedFor(urlEqualTo("/product/4")));

	}

}
