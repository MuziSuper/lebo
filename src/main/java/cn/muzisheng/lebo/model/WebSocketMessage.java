package cn.muzisheng.lebo.model;

import cn.muzisheng.lebo.utils.IdUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage<T> {
    private String type;
    private Long timestamp;
    private String requestId;
    private T data;

    public static <T> WebSocketMessage<T> of(WebSocketEventType eventType, T data) {
        return WebSocketMessage.<T>builder()
                .type(eventType.getCode())
                .timestamp(System.currentTimeMillis())
                .requestId(IdUtil.generateId())
                .data(data)
                .build();
    }

    public static <T> WebSocketMessage<T> of(WebSocketEventType eventType) {
        return of(eventType, null);
    }

    public static WebSocketMessage<Void> pong() {
        return of(WebSocketEventType.PONG);
    }

    public static WebSocketMessage<Void> ping() {
        return of(WebSocketEventType.PING);
    }
}
