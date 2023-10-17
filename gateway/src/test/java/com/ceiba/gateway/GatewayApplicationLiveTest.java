package com.ceiba.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class GatewayApplicationLiveTest {

    private MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    private TestRestTemplate testRestTemplate = new TestRestTemplate();
    private String TEST_URL = "http://localhost:8085";


    @Test
    public void prueba() {
        ResponseEntity<String> response = testRestTemplate
                .getForEntity(TEST_URL + "/home/index.html", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Location").get(0)).isEqualTo("http://localhost:8085/login");
    }

    @Test
    public void testAccess() {


        form.add("username", "user");
        form.add("password", "password");

        // Inicia sesion con rol de USER
        ResponseEntity<String> response = testRestTemplate
                .postForEntity(TEST_URL + "/login", form, String.class);

        String sessionCookie = response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        System.out.println("Valor de la sesion con rol de USER: " + sessionCookie);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // Verifica que tiene acceso a books con la sesion de rol USER
        response = testRestTemplate.exchange(TEST_URL + "/book-service/books/1",
                HttpMethod.GET, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verifica que no tiene acceso a recursos con rol de ADMIN
        response = testRestTemplate.exchange(TEST_URL + "/rating-service/ratings",
                HttpMethod.GET, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Inicia sesion con rol de ADMIN
        form.clear();
        form.add("username", "admin");
        form.add("password", "admin");
        response = testRestTemplate
                .postForEntity(TEST_URL + "/login", form, String.class);

        sessionCookie = response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
        System.out.println("Valor de la sesion con rol de ADMIN: " + sessionCookie);

        headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        httpEntity = new HttpEntity<>(headers);

        // Verifica que ahora si tiene acceso a recurso con rol de ADMIN
        response = testRestTemplate.exchange(TEST_URL + "/rating-service/ratings",
                HttpMethod.GET, httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // Verifica el acceso al servicio de eureka
        response = testRestTemplate.exchange(TEST_URL + "/discovery",
                HttpMethod.GET, httpEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
