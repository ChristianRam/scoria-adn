package com.ceiba.book.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.ceiba.book.model.Book;
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
class BookControllerTest {

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
    void givenAnonimousUser_whenFindAllBooks_shouldReturnBookSuccesfully() {

        given().when()
                .get("/books")
                .then()
                .statusCode(200);
    }

    @Test
    void givenAuthenticatedUser_whenFindBook_shouldReturnBookSuccesfully() {

        given().header("Authorization", bearerTokenUser)
                .when()
                .get("/books/{bookId}", 1)
                .then()
                .statusCode(200)
                .body("title", equalTo("Harry Potter"))
                .body("author", equalTo("J. K. Rowling"));
    }

    @Test
    void givenAuthenticatedAdmin_whenFindBook_shouldReturnForbiddenError() {

        given().header("Authorization", bearerTokenAdmin)
                .when()
                .get("/books/{bookId}", 1)
                .then()
                .statusCode(403);
    }

    @Test
    void givenAuthenticatedAdmin_whenCreateBook_shouldCreateBookSuccesfully() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Book(6L, "100 años de soledad", "Gabriel Garcia Marquez"))
                .when()
                .post("/books")
                .then()
                .statusCode(201);
    }

    @Test
    void givenBookIdAlreadyExist_whenCreateBook_shouldReturnConflict() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Book(1L, "100 años de soledad", "Gabriel Garcia Marquez"))
                .when()
                .post("/books")
                .then()
                .statusCode(409);
    }

    @Test
    void givenAuthenticatedAdmin_whenUpdateBook_shouldUpdateBookSuccesfully() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Book(1L, "100 años de soledad", "Gabriel Garcia Marquez"))
                .when()
                .put("/books/{bookId}", 1)
                .then()
                .statusCode(200);
    }

    @Test
    void givenNonExistentBook_whenUpdateBook_shouldReturnNotFound() {

        given().header("Authorization", bearerTokenAdmin)
                .contentType("application/json")
                .body(new Book(20L, "100 años de soledad", "Gabriel Garcia Marquez"))
                .when()
                .put("/books/{bookId}", 20)
                .then()
                .statusCode(404);
    }

    @Test
    void givenAuthenticatedAdmin_whenDeleteBook_shouldDeleteBookSuccesfully() {

        given().header("Authorization", bearerTokenAdmin)
                .when()
                .delete("/books/{bookId}", 2)
                .then()
                .statusCode(204);
    }

    @Test
    void givenNonExistentBook_whenDeleteBook_shouldReturnNotFound() {

        given().header("Authorization", bearerTokenAdmin)
                .when()
                .delete("/books/{bookId}", 20)
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