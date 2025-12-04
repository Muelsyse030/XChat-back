package WebSocketConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 存储在线用户的 Session: Map<UserId, Session>
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从 URL 参数获取 token (例如 ws://localhost:8080/ws/chat?token=user-token-1)
        String query = session.getUri().getQuery();
        String userId = getUserIdFromToken(query);

        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("用户上线: " + userId);
        } else {
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 1. 收到前端消息 {"receiverId": 2, "content": "你好"}
        Map<String, Object> msgMap = objectMapper.readValue(message.getPayload(), Map.class);

        String receiverId = String.valueOf(msgMap.get("receiverId"));
        String content = (String) msgMap.get("content");

        // 2. 找到接收者的 Session
        WebSocketSession receiverSession = userSessions.get(receiverId);

        // 3. 转发消息
        if (receiverSession != null && receiverSession.isOpen()) {
            // 构造发给接收者的消息结构
            Map<String, Object> response = Map.of(
                    "content", content,
                    "senderId", getUserIdFromSession(session)
            );
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }

        // TODO: 这里可以调用 Mapper 将消息保存到数据库
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        userSessions.values().remove(session);
    }

    // 简单的从 token 提取 ID 的逻辑 (对应 AuthController 生成的格式)
    private String getUserIdFromToken(String query) {
        if (query != null && query.contains("token=user-token-")) {
            return query.split("token=user-token-")[1];
        }
        return null;
    }

    private String getUserIdFromSession(WebSocketSession session) {
        return getUserIdFromToken(session.getUri().getQuery());
    }
}