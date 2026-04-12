package cn.muzisheng.lebo.model;

/**
 * WebSocket 事件类型枚举
 * 
 * <p>定义了客户端与服务端之间 WebSocket 通信的所有事件类型。</p>
 * 
 * <h3>事件分类：</h3>
 * <ul>
 *   <li>心跳事件：PING/PONG，用于保持连接活跃</li>
 *   <li>商户通知事件：ORDER_PAID，通知商户有新订单</li>
 *   <li>客户通知事件：ORDER_ACCEPT/ORDER_REJECT，通知客户订单状态变更</li>
 * </ul>
 */
public enum WebSocketEventType {
    /** 心跳请求 - 客户端发送，服务端响应 PONG */
    PING("PING", "心跳请求"),
    
    /** 心跳响应 - 服务端响应客户端的 PING */
    PONG("PONG", "心跳响应"),
    
    /** 商家确认接单 - 服务端通知客户，订单已被商家确认 */
    ORDER_ACCEPT("ORDER_ACCEPT", "商家确认接单"),
    
    /** 商家拒绝接单 - 服务端通知客户，订单已被商家拒绝 */
    ORDER_REJECT("ORDER_REJECT", "商家拒绝接单"),
    
    /** 客户已支付订单 - 服务端通知商户，有新的已支付订单 */
    ORDER_PAID("ORDER_PAID", "客户已支付订单");

    private final String code;
    private final String description;

    WebSocketEventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static WebSocketEventType fromCode(String code) {
        for (WebSocketEventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static boolean contains(String code) {
        return fromCode(code) != null;
    }
}
