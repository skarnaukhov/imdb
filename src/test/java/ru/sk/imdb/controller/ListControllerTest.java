package ru.sk.imdb.controller;

import org.assertj.core.util.Lists;
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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InMemoryStorage inMemoryStorage;

    @Before
    public void setUp() {
        this.inMemoryStorage.putList("value", Lists.list("1", "bbb", "sss", "ccc"));
        this.inMemoryStorage.putList("value2", Lists.list("1", "bbb", "sss", "ccc"));
        this.inMemoryStorage.putList("keyToDelete", Lists.list("1", "bbb", "sss", "ccc"));
        this.inMemoryStorage.putList("keyToAppend", Lists.list("1", "bbb", "ccc", "3"));
        this.inMemoryStorage.putList("keyToAppend1", Lists.list("1", "bbb", "ccc", "3"));
    }

    @Test
    public void testGetListByKeyIsSuccessful() {
        final String key = "value";
        final String result = webTestClient.get().uri("list/{key}", key)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();
        System.out.println(result);
        assertThat("{\""+key+"\": [\"1\",\"bbb\",\"sss\",\"ccc\"]}").isEqualTo(result);
    }

    @Test
    public void testGetListByKeyReturns404() {
        final String key = "value1";
        final String result = webTestClient.get().uri("list/{key}", key)
                .exchange().expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+"\"}").isEqualTo(result);
    }

    @Test
    public void upsertListByKey() {
        final String key = "value";
        final String result = webTestClient.put().uri("list/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, Arrays.asList("1", "bbb", "sss", "ccc")).toString())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\""+key+"\": [\"1\",\"bbb\",\"sss\",\"ccc\"]}").isEqualTo(result);
    }

    @Test
    public void appendListToListByKey() {
        final String key = "value2";
        final String result = webTestClient.post().uri("list/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, Arrays.asList("3")).toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\""+key+"\": [\"1\",\"bbb\",\"sss\",\"ccc\",\"3\"]}").isEqualTo(result);
    }

    @Test
    public void appendListToListByKeyReturn404() {
        final String key = "value3";
        final String result = webTestClient.post().uri("list/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, Arrays.asList("3")).toString())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+"\"}").isEqualTo(result);
    }

    @Test
    public void deleteStringValueByKeySuccess() {
        final String key = "keyToDelete";
        final SuccessResponse result = webTestClient.method(HttpMethod.DELETE).uri("list/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SuccessResponse.class).returnResult().getResponseBody();
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    public void deleteStringValueByKeyReturns404() {
        final String key = "keyToDelete1";
        final String result = webTestClient.method(HttpMethod.DELETE).uri("list/{key}", key)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .exchange().expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+"\"}").isEqualTo(result);
    }

    @Test
    public void appendValueToListByIndexSuccess() {
        final String key = "keyToAppend";
        final Integer index = 2;
        final String result = webTestClient.method(HttpMethod.PATCH).uri("list/{key}/{index}", key, index)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, Arrays.asList("222")).toString())
                .exchange().expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\""+key+"\": [\"1\",\"bbb\",\"222\",\"ccc\",\"3\"]}").isEqualTo(result);
    }

    @Test
    public void appendValueToListByTheLastIndexSuccess() {
        final String key = "keyToAppend1";
        final Integer index = 4;
        final String result = webTestClient.method(HttpMethod.PATCH).uri("list/{key}/{index}", key, index)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, Arrays.asList("222")).toString())
                .exchange().expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\""+key+"\": [\"1\",\"bbb\",\"ccc\",\"3\",\"222\"]}").isEqualTo(result);
    }

    @Test
    public void appendValueToListByIndexReturns404WhenIndexIsOutOfBounds() {
        final String key = "keyToAppend";
        final Integer index = 5;
        final String result = webTestClient.method(HttpMethod.PATCH).uri("list/{key}/{index}", key, index)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(new Pair<>(key, Arrays.asList("222")).toString())
                .exchange().expectStatus().isNotFound()
                .expectBody(String.class).returnResult().getResponseBody();
        assertThat("{\"error\":\"cannot find value by "+key+" and "+index+"\"}").isEqualTo(result);
    }

}
