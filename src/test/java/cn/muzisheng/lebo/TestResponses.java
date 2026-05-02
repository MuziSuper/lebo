package cn.muzisheng.lebo;

import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import org.springframework.http.ResponseEntity;

/**
 * Shared helpers for tests that need the same response envelope used by
 * production services. Keeping this small avoids hand-building subtly different
 * response shapes in each test.
 */
public final class TestResponses {

    private TestResponses() {
    }

    public static <T> ResponseEntity<Result<T>> ok(T data) {
        Response<T> response = new Response<>();
        response.setData(data);
        return response.value();
    }
}
