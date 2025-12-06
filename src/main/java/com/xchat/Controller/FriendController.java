package com.xchat.Controller;

import com.xchat.Entity.User;
import com.xchat.Mapper.FriendMapper;
import com.xchat.Mapper.UserMapper;
import com.xchat.WebSocketConfig.ChatWebSocketHandler; // 引入 WebSocket 处理器用于判断在线
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/friends")
@CrossOrigin
public class FriendController {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;

    // 1. 获取好友列表 (替代原来的 getContacts)
    @GetMapping
    public Map<String, Object> getMyFriends(@RequestHeader("Authorization") String token) {
        Long currentUserId = getUserIdFromToken(token);

        List<User> friends = friendMapper.findFriendsByUserId(currentUserId);

        // 包装在线状态
        List<Map<String, Object>> friendsWithStatus = friends.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("email", user.getEmail());
            map.put("nickname", user.getNickname());
            map.put("avatar", user.getAvatar());
            map.put("bio", user.getBio());
            // 判断在线状态
            map.put("online", ChatWebSocketHandler.isUserOnline(String.valueOf(user.getId())));
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", friendsWithStatus);
        return result;
    }

    // 2. 添加好友接口
    @PostMapping("/add")
    public Map<String, Object> addFriend(@RequestBody Map<String, String> payload, @RequestHeader("Authorization") String token) {
        Map<String, Object> result = new HashMap<>();
        String targetEmail = payload.get("email");
        Long currentUserId = getUserIdFromToken(token);

        // 1. 找人
        User targetUser = userMapper.findByEmail(targetEmail);
        if (targetUser == null) {
            result.put("code", 404);
            result.put("msg", "用户不存在");
            return result;
        }

        if (targetUser.getId().equals(currentUserId)) {
            result.put("code", 400);
            result.put("msg", "不能添加自己");
            return result;
        }

        // 2. 检查是否已经是好友
        if (friendMapper.checkFriendship(currentUserId, targetUser.getId()) > 0) {
            result.put("code", 400);
            result.put("msg", "你们已经是好友了");
            return result;
        }

        // 3. 双向添加 (A->B, B->A)
        try {
            friendMapper.addFriend(currentUserId, targetUser.getId());
            friendMapper.addFriend(targetUser.getId(), currentUserId);
            result.put("code", 200);
            result.put("msg", "添加成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "添加失败: " + e.getMessage());
        }

        return result;
    }

    // 简单解析 Token
    private Long getUserIdFromToken(String token) {
        if (token.startsWith("user-token-")) {
            return Long.parseLong(token.split("-")[2]);
        }
        return 0L;
    }
}