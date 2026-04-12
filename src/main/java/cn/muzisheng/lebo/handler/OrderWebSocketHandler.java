package cn.muzisheng.lebo.handler;

import cn.muzisheng.lebo.model.WebSocketEventType;
import cn.muzisheng.lebo.model.WebSocketMessage;
import cn.muzisheng.lebo.service.UserService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单 WebSocket 处理器
 * 
 * <p>负责处理客户端（商户/客户）与服务器之间的 WebSocket 双工通信。
 * 支持心跳检测、订单状态变更通知等功能。</p>
 * 
 * <h3>通信流程：</h3>
 * <ol>
 *   <li>客户支付订单 → 服务端通知商户有新订单（ORDER_PAID）</li>
 *   <li>商户确认接单 → 服务端通知客户订单已确认（ORDER_ACCEPT）</li>
 *   <li>商户拒绝接单 → 服务端通知客户订单已拒绝（ORDER_REJECT）</li>
 * </ol>
 * 
 * <h3>客户端类型：</h3>
 * <ul>
 *   <li>customer - 普通客户，接收自己订单的状态变更通知</li>
 *   <li>merchant - 商户，接收所有订单的变更通知</li>
 * </ul>
 * 
 * <h3>支持的事件类型：</h3>
 * <ul>
 *   <li>PING/PONG - 心跳检测，保持连接活跃</li>
 *   <li>ORDER_PAID - 客户已支付订单（通知商户）</li>
 *   <li>ORDER_ACCEPT - 商家确认接单（通知客户）</li>
 *   <li>ORDER_REJECT - 商家拒绝接单（通知客户）</li>
 * </ul>
 * 
 * <h3>连接示例：</h3>
 * <pre>
 * // 商户连接
 * ws://localhost:8080/ws/order?type=merchant&token=Bearer xxx
 * 
 * // 客户连接
 * ws://localhost:8080/ws/order?type=customer&token=Bearer xxx
 * </pre>
 * 
 * @author muzisheng
 * @see WebSocketEventType
 * @see WebSocketMessage
 */
@Log4j2
@Component
public class OrderWebSocketHandler extends TextWebSocketHandler {

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        OrderWebSocketHandler.userService = userService;
    }

    /**
     * 客户连接映射表
     * Key: 用户的 openId
     * Value: WebSocket 会话对象
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private static final Map<String, WebSocketSession> customerMap = new ConcurrentHashMap<>();

    /**
     * 商户连接映射表
     * Key: 用户的 openId
     * Value: WebSocket 会话对象
     * 使用 ConcurrentHashMap 保证线程安全
     */
    private static final Map<String, WebSocketSession> merchantMap = new ConcurrentHashMap<>();

    /** 客户端类型：普通客户 */
    private static final String CLIENT_TYPE_CUSTOMER = "customer";

    /** 客户端类型：商户 */
    private static final String CLIENT_TYPE_MERCHANT = "merchant";

    /**
     * WebSocket 连接建立后的回调方法
     * 
     * <p>当客户端成功建立 WebSocket 连接时触发。
     * 会根据 URL 参数中的 type 将连接分类存储到对应的映射表中。</p>
     * 
     * <h3>处理流程：</h3>
     * <ol>
     *   <li>从 URL 参数获取客户端类型（type）</li>
     *   <li>从会话属性获取用户 openId（由拦截器注入）</li>
     *   <li>验证 openId 是否有效</li>
     *   <li>根据客户端类型存储到对应的映射表</li>
     * </ol>
     * 
     * @param session WebSocket 会话对象，包含连接信息和用户属性
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String clientType = getParam(session, "type");
        String openId = (String) session.getAttributes().get("openId");

        log.info("WebSocket 连接请求，sessionId: {}, clientType: {}, openId: {}", 
                session.getId(), clientType, openId);

        if (openId == null || openId.isEmpty()) {
            log.warn("WebSocket 连接失败：openId 为空，sessionId: {}", session.getId());
            closeSession(session);
            return;
        }

        if (CLIENT_TYPE_CUSTOMER.equals(clientType)) {
            customerMap.put(openId, session);
            log.info("【客户连接成功】openId: {}, sessionId: {}, 当前在线客户数: {}, 当前在线商户数: {}", 
                    openId, session.getId(), customerMap.size(), merchantMap.size());
        } else if (CLIENT_TYPE_MERCHANT.equals(clientType)) {
            if (!userService.isMerchant(openId)) {
                log.warn("WebSocket 连接失败：用户不是商户，openId: {}, sessionId: {}", openId, session.getId());
                closeSession(session);
                return;
            }
            merchantMap.put(openId, session);
            log.info("【商户连接成功】openId: {}, sessionId: {}, 当前在线商户数: {}, 当前在线客户数: {}", 
                    openId, session.getId(), merchantMap.size(), customerMap.size());
        } else {
            log.warn("WebSocket 连接失败：无效的客户端类型，type: {}, sessionId: {}", clientType, session.getId());
            closeSession(session);
        }
    }

    /**
     * 处理客户端发送的文本消息
     * 
     * <p>接收客户端发送的 JSON 格式消息，根据消息类型分发到对应的处理方法。</p>
     * 
     * @param session WebSocket 会话对象
     * @param message 文本消息对象
     * @throws Exception 消息处理异常
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String openId = (String) session.getAttributes().get("openId");

        log.info("【收到消息】openId: {}, sessionId: {}, payload: {}", openId, session.getId(), payload);

        try {
            JSONObject json = JSON.parseObject(payload);
            String type = json.getString("type");

            if (!WebSocketEventType.contains(type)) {
                log.warn("未知的 WebSocket 事件类型: {}, openId: {}", type, openId);
                return;
            }

            WebSocketEventType eventType = WebSocketEventType.fromCode(type);
            log.info("【事件触发】openId: {}, eventType: {}", openId, eventType);

            if (eventType == WebSocketEventType.PING) {
                handlePing(session);
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 消息异常，openId: {}, error: {}", openId, e.getMessage(), e);
        }
    }

    /**
     * WebSocket 连接关闭后的回调方法
     * 
     * @param session WebSocket 会话对象
     * @param status 关闭状态码和原因
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String openId = (String) session.getAttributes().get("openId");
        if (openId != null) {
            boolean wasCustomer = customerMap.remove(openId) != null;
            boolean wasMerchant = merchantMap.remove(openId) != null;
            String clientType = wasCustomer ? "客户" : (wasMerchant ? "商户" : "未知");
            log.info("【连接关闭】openId: {}, sessionId: {}, clientType: {}, status: {}, 当前在线客户数: {}, 当前在线商户数: {}", 
                    openId, session.getId(), clientType, status, customerMap.size(), merchantMap.size());
        } else {
            log.info("【连接关闭】sessionId: {}, openId 为空, status: {}", session.getId(), status);
        }
    }

    /**
     * WebSocket 传输错误回调方法
     * 
     * @param session WebSocket 会话对象
     * @param exception 传输异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String openId = (String) session.getAttributes().get("openId");
        log.error("【传输错误】openId: {}, sessionId: {}, error: {}", openId, session.getId(), exception.getMessage());
        closeSession(session);
    }

    /**
     * 处理心跳请求
     * 
     * @param session WebSocket 会话对象
     * @throws Exception 发送消息异常
     */
    private void handlePing(WebSocketSession session) throws Exception {
        String openId = (String) session.getAttributes().get("openId");
        log.info("【心跳请求】openId: {}, sessionId: {}", openId, session.getId());
        WebSocketMessage<Void> pongMessage = WebSocketMessage.pong();
        sendMessage(session, pongMessage);
        log.info("【心跳响应】openId: {}, sessionId: {}", openId, session.getId());
    }

    // ==================== 业务通知方法 ====================

    /**
     * 通知商户有新的已支付订单
     * 
     * <p>当客户支付订单成功后，调用此方法广播给所有在线商户。
     * 商户收到通知后，应调用订单列表接口刷新页面。</p>
     * 
     * <h3>使用场景：</h3>
     * <pre>
     * // 在 OrderServiceImpl.submit() 方法中调用
     * OrderWebSocketHandler.notifyMerchantNewOrder();
     * </pre>
     */
    public static void notifyMerchantNewOrder() {
        log.info("【业务通知】开始通知商户有新订单，当前在线商户数: {}", merchantMap.size());
        WebSocketMessage<Void> message = WebSocketMessage.of(WebSocketEventType.ORDER_PAID);
        broadcastToMerchants(message);
        log.info("【业务通知】已通知商户有新订单，事件类型: ORDER_PAID");
    }

    /**
     * 通知客户订单已被商家确认
     * 
     * <p>当商家确认接单后，调用此方法通知对应客户。
     * 客户收到通知后，应调用订单详情接口刷新页面。</p>
     * 
     * @param customerOpenId 客户的 openId
     */
    public static void notifyCustomerOrderAccepted(String customerOpenId) {
        log.info("【业务通知】开始通知客户订单已确认，customerOpenId: {}", customerOpenId);
        WebSocketMessage<Void> message = WebSocketMessage.of(WebSocketEventType.ORDER_ACCEPT);
        sendToCustomer(customerOpenId, message);
        log.info("【业务通知】已通知客户订单已确认，customerOpenId: {}, 事件类型: ORDER_ACCEPT", customerOpenId);
    }

    /**
     * 通知客户订单已被商家拒绝
     * 
     * <p>当商家拒绝接单后，调用此方法通知对应客户。
     * 客户收到通知后，应调用订单详情接口刷新页面。</p>
     * 
     * @param customerOpenId 客户的 openId
     */
    public static void notifyCustomerOrderRejected(String customerOpenId) {
        log.info("【业务通知】开始通知客户订单已拒绝，customerOpenId: {}", customerOpenId);
        WebSocketMessage<Void> message = WebSocketMessage.of(WebSocketEventType.ORDER_REJECT);
        sendToCustomer(customerOpenId, message);
        log.info("【业务通知】已通知客户订单已拒绝，customerOpenId: {}, 事件类型: ORDER_REJECT", customerOpenId);
    }

    // ==================== 基础发送方法 ====================

    /**
     * 向指定商户发送消息
     * 
     * @param openId 商户的openId
     * @param message 要发送的消息对象
     */
    public static void sendToMerchant(String openId, WebSocketMessage<?> message) {
        WebSocketSession session = merchantMap.get(openId);
        if (session != null && session.isOpen()) {
            try {
                sendMessage(session, message);
                log.info("【发送成功】商户 openId: {}, sessionId: {}, eventType: {}", openId, session.getId(), message.getType());
            } catch (Exception e) {
                log.error("【发送失败】商户 openId: {}, sessionId: {}, error: {}", openId, session.getId(), e.getMessage());
            }
        } else {
            log.warn("【发送失败】商户 WebSocket 会话不存在或已关闭，openId: {}", openId);
        }
    }

    /**
     * 向指定客户发送消息
     * 
     * @param openId 客户的用户 openId
     * @param message 要发送的消息对象
     */
    public static void sendToCustomer(String openId, WebSocketMessage<?> message) {
        WebSocketSession session = customerMap.get(openId);
        if (session != null && session.isOpen()) {
            try {
                sendMessage(session, message);
                log.info("【发送成功】客户 openId: {}, sessionId: {}, eventType: {}", openId, session.getId(), message.getType());
            } catch (Exception e) {
                log.error("【发送失败】客户 openId: {}, sessionId: {}, error: {}", openId, session.getId(), e.getMessage());
            }
        } else {
            log.warn("【发送失败】客户 WebSocket 会话不存在或已关闭，openId: {}", openId);
        }
    }

    /**
     * 向所有在线商户广播消息
     * 
     * @param message 要广播的消息对象
     */
    public static void broadcastToMerchants(WebSocketMessage<?> message) {
        log.info("【广播开始】目标: 所有商户, eventType: {}, 当前在线商户数: {}", message.getType(), merchantMap.size());
        int successCount = 0;
        int failCount = 0;
        for (Map.Entry<String, WebSocketSession> entry : merchantMap.entrySet()) {
            String openId = entry.getKey();
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    sendMessage(session, message);
                    successCount++;
                } catch (Exception e) {
                    log.error("【广播失败】商户 openId: {}, sessionId: {}, error: {}", openId, session.getId(), e.getMessage());
                    failCount++;
                }
            }
        }
        log.info("【广播完成】目标: 商户, 成功: {}, 失败: {}", successCount, failCount);
    }

    /**
     * 向所有在线客户广播消息
     * 
     * @param message 要广播的消息对象
     */
    public static void broadcastToCustomers(WebSocketMessage<?> message) {
        log.info("【广播开始】目标: 所有客户, eventType: {}, 当前在线客户数: {}", message.getType(), customerMap.size());
        int successCount = 0;
        int failCount = 0;
        for (Map.Entry<String, WebSocketSession> entry : customerMap.entrySet()) {
            String openId = entry.getKey();
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    sendMessage(session, message);
                    successCount++;
                } catch (Exception e) {
                    log.error("【广播失败】客户 openId: {}, sessionId: {}, error: {}", openId, session.getId(), e.getMessage());
                    failCount++;
                }
            }
        }
        log.info("【广播完成】目标: 客户, 成功: {}, 失败: {}", successCount, failCount);
    }

    // ==================== 工具方法 ====================

    private static void sendMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String json = JSON.toJSONString(message);
        session.sendMessage(new TextMessage(json));
    }

    private void closeSession(WebSocketSession session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            log.error("关闭 WebSocket 会话失败: {}", e.getMessage());
        }
    }

    private String getParam(WebSocketSession session, String key) {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        return Arrays.stream(query.split("&"))
                .map(s -> s.split("="))
                .filter(arr -> arr.length == 2 && arr[0].equals(key))
                .findFirst()
                .map(arr -> arr[1])
                .orElse(null);
    }

    public static int getMerchantCount() {
        return merchantMap.size();
    }

    public static int getCustomerCount() {
        return customerMap.size();
    }
}
