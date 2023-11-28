package com.ceiba.library.controller;

import com.ceiba.library.feign.IBookFeignClient;
import com.ceiba.library.feign.IRatingFeignClient;
import com.ceiba.library.model.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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
import java.util.ArrayList;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
class LibraryControllerTest {
    @Container
    static KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("keycloak/oauth-realm.json");
    private static String bearerTokenUser;
    @Mock
    private IBookFeignClient bookClient;
    @Mock
    private IRatingFeignClient ratingClient;
    @InjectMocks
    private LibraryController libraryController;
    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "realms/oauth");
    }

    @BeforeAll
    static void setup() {
        bearerTokenUser = getBearerToken("user", "user");
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

    @PostConstruct
    public void init() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void givenAuthenticatedUser_whenGetBookWithRatings_shouldReturnBookSuccesfully() {

        when(bookClient.findBook(any())).thenReturn(new Book());
        when(ratingClient.findRatingsByBookId(Mockito.any())).thenReturn(new ArrayList<>());

        given().header("Authorization", bearerTokenUser)
                .when()
                .get("/library/{bookId}", 1)
                .then()
                .statusCode(200);
    }

}