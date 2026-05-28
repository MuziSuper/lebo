package cn.muzisheng.lebo.model;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseTest {

    @Test
    void putAuthorizationHeaderExposesItForCorsClients() {
        Response<Boolean> response = new Response<>();

        response.putHeader(HttpHeaders.AUTHORIZATION, "Bearer token-value");

        assertThat(response.value().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer token-value");
        assertThat(response.value().getHeaders().getFirst(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS))
                .isEqualTo(HttpHeaders.AUTHORIZATION);
    }
}
