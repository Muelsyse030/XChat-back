package com.xchat.Controller;

import com.xchat.Entity.Message;
import com.xchat.Mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;

    @GetMapping("/messages")
    public Map<String, Object> getHistory(@RequestParam Long contactId, @RequestHeader("Authorization") String token) {
        // 简单解析 Token 获取当前用户 ID (token格式: "user-token-1")
        Long currentUserId = Long.parseLong(token.split("-")[2]);

        List<Message> messages = messageMapper.findChatHistory(currentUserId, contactId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", messages);
        return result;
    }
}