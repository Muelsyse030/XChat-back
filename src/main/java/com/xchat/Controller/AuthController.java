package com.xchat.Controller;

import com.xchat.Entity.User;       // 修正导入路径
import com.xchat.Mapper.UserMapper; // 修正导入路径
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List; // 记得导入 List

@RestController
@RequestMapping("/api")
@CrossOrigin // 允许前端跨域
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.findByEmail(loginUser.getEmail());

        if (user != null && user.getPassword().equals(loginUser.getPassword())) {
            result.put("code", 200);
            result.put("msg", "登录成功");
            // 简单模拟 Token：直接返回用户ID，实际项目中请使用 JWT
            result.put("token", "user-token-" + user.getId());
            result.put("userInfo", user);
        } else {
            result.put("code", 401);
            result.put("msg", "账号或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> HZregister(@RequestBody User user) {
        userMapper.insert(user);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "注册成功");
        return result;
    }

    @GetMapping("/contacts")
    public Map<String, Object> getContacts() {
        List<User> users = userMapper.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", users);
        return result;
    }
}
