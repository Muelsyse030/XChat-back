package com.xchat.WebSocketConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xchat.Entity.Message;
import com.xchat.Mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MessageMapper messageMapper;

    // 存储在线用户的 Session: Map<UserId, Session>
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从 URL 参数获取 token
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
        // 1. 收到前端消息
        Map<String, Object> msgMap = objectMapper.readValue(message.getPayload(), Map.class);

        String receiverIdStr = String.valueOf(msgMap.get("receiverId"));
        String content = (String) msgMap.get("content");

        // 获取发送者ID
        String senderIdStr = getUserIdFromSession(session);

        // 2. 存入数据库
        try {
            Message dbMsg = new Message();
            dbMsg.setSenderId(Long.parseLong(senderIdStr));
            dbMsg.setReceiverId(Long.parseLong(receiverIdStr));
            dbMsg.setContent(content);

            messageMapper.insert(dbMsg); // 写入数据库
            System.out.println("消息已保存: " + content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("消息保存失败: " + e.getMessage());
        }

        // 3. 转发消息给接收者
        WebSocketSession receiverSession = userSessions.get(receiverIdStr);
        if (receiverSession != null && receiverSession.isOpen()) {
            Map<String, Object> response = Map.of(
                    "content", content,
                    "senderId", senderIdStr
            );
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        userSessions.values().remove(session);
    }

    // 简单的从 token 提取 ID 的逻辑
    private String getUserIdFromToken(String query) {
        if (query != null && query.contains("token=user-token-")) {
            return query.split("token=user-token-")[1];
        }
        return null;
    }

    private String getUserIdFromSession(WebSocketSession session) {
        return getUserIdFromToken(session.getUri().getQuery());
    }

    public static boolean isUserOnline(String userId) {
        return userSessions.containsKey(userId);
    }
}