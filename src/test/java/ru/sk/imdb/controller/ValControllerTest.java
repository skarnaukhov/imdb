package ru.sk.imdb.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.sk.imdb.domain.Pair;
import ru.sk.imdb.domain.SuccessResponse;
import ru.sk.imdb.service.InMemoryStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @Before
    public void setUp() {
        this.inMemoryStorage.putValue("value", "value string");
        this.inMemoryStorage.putValue("keyToDelete", "valueToDelete");
    }

    @Test
    public void testGetValueByKeyIsSuccessful() {
        final String key = "value";
        final String result = webTestClient.get().uri("val/{key}", key)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();
        System.out.println(result);
        assertThat("{\""+key+"\": \"value string\"}").isEqualTo(result);
    }

    @Test
    public void testGetValueByKeyReturns404() {
        final String key = "value1";
        final String result = webTestClient.get().uri("val/{key}", key)
                .exchange().expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+"\"}").isEqualTo(result);
    }

    @Test
    public void upsertStringValueByKey() {
        final String key = "value";
        final String result = webTestClient.put().uri("val/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, "value string").toString())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\""+key+"\": \"value string\"}").isEqualTo(result);
    }

    @Test
    public void updateStringValueByKeySuccess() {
        final String key = "value";
        final String result = webTestClient.post().uri("val/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, "string").toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\""+key+"\": \"string\"}").isEqualTo(result);
    }

    @Test
    public void updateStringValueByKeyNotFound() {
        final String key = "keyNotFound";
        final String result = webTestClient.post().uri("val/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, "string").toString())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+"\"}").isEqualTo(result);
    }

    @Test
    public void deleteStringValueByKeySuccess() {
        final String key = "keyToDelete";
        final SuccessResponse result = webTestClient.method(HttpMethod.DELETE).uri("val/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, "valueToDelete").toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(SuccessResponse.class).returnResult().getResponseBody();
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void deleteStringValueByKeyReturns404() {
        final String key = "keyToDelete1";
        final String result = webTestClient.method(HttpMethod.DELETE).uri("val/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, "valueToDelete").toString())
                .exchange().expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+"\"}").isEqualTo(result);
    }

}
