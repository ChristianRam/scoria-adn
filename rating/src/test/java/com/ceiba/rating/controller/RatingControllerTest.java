package com.ceiba.rating.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.ceiba.rating.model.Rating;
import com.ceiba.rating.model.Stars;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;


@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class RatingControllerTest {
    private static String bearerTokenUser;

    private static String bearerTokenAdmin;
    @LocalServerPort
    private int port;

    @Container
    static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("keycloak/oauth-realm.json");

    @PostConstruct
    public void init() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "realms/oauth");
    }

    @BeforeAll
    static void setup() {
        bearerTokenUser = getBearerToken("user", "user");
        bearerTokenAdmin = getBearerToken("admin", "admin");
    }

    @Test
    void givenAuthenticatedAdminWithoutBookIdParam_whenFindRatingsByBookId_shouldReturnAllRatingsSuccesfully() {

        given().header("Authorization", bearerTokenAdmin).when()
                .get("/ratings")
                .then()
                .statusCode(200);
    }

    @Test
    void givenAuthenticatedUserWithBookIdParam_whenFindRatingsByBookId_shouldReturnRatingSuccesfully() {

        given().header("Authorization", bearerTokenUser)
                .when()
                .get("/ratings?bookId={bookId}", 1)
                .then()
                .statusCode(200);
    }


    @Test
    void givenAuthenticatedAdmin_whenCreateRating_shouldCreateRatingSuccesfully() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Rating(6L, 1L, Stars.FIVE))
                .when()
                .post("/ratings")
                .then()
                .statusCode(201);
    }

    @Test
    void givenRatingIdAlreadyExist_whenCreateRating_shouldReturnConflict() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Rating(1L, 1L, Stars.FIVE))
                .when()
                .post("/ratings")
                .then()
                .statusCode(409);
    }

    @Test
    void givenAuthenticatedAdmin_whenUpdateRating_shouldUpdateRatingSuccesfully() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Rating(1L, 1L, Stars.FOUR))
                .when()
                .put("/ratings/{ratingId}", 1)
                .then()
                .statusCode(200);
    }

    @Test
    void givenNonExistentRating_whenUpdateRating_shouldReturnNotFound() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Rating(20L, 1L, Stars.FOUR))
                .when()
                .put("/ratings/{ratingId}", 20)
                .then()
                .statusCode(404);
    }

    @Test
    void givenAuthenticatedAdmin_whenDeleteRating_shouldDeleteRatingSuccesfully() {

        given().header("Authorization", bearerTokenAdmin)
                .when()
                .delete("/ratings/{ratingId}", 2)
                .then()
                .statusCode(204);
    }

    @Test
    void givenNonExistentRating_whenDeleteRating_shouldReturnNotFound() {

        given().header("Authorization", bearerTokenAdmin)
                .when()
                .delete("/ratings/{ratingId}", 20)
                .then()
                .statusCode(404);
    }

    private static String getBearerToken(String username, String password) {
        try {
            URI authorizationURI = new URIBuilder(keycloak.getAuthServerUrl()
                    + "realms/oauth/protocol/openid-connect/token").build();
            WebClient webclient = WebClient.builder().build();
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.put("client_id", Collections.singletonList("oauth-rest-api"));
            formData.put("client_secret", Collections.singletonList("b4inLFwyXprbVApBfXNVzQKqPje1Shpr"));
            formData.put("username", Collections.singletonList(username));
            formData.put("password", Collections.singletonList(password));
            formData.put("grant_type", Collections.singletonList("password"));

            String result = webclient.post()
                    .uri(authorizationURI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JacksonJsonParser jsonParser = new JacksonJsonParser();
            return "Bearer " + jsonParser.parseMap(result).get("access_token").toString();
        } catch (URISyntaxException e) {
            log.error("Can't obtain an access token from Keycloak!", e);
        }
        return null;
    }

}