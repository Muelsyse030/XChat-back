package com.xchat.Controller;

import com.xchat.Entity.User;
import com.xchat.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 图片存储的本地路径 (System.getProperty("user.dir") 获取项目根目录)
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // 1. 头像上传接口
    @PostMapping("/upload")
    public Map<String, Object> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "文件为空");
            return result;
        }

        try {
            // 确保目录存在
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + suffix;

            // 保存文件
            File dest = new File(UPLOAD_DIR + newFileName);
            file.transferTo(dest);

            // 返回可访问的 URL (假设配置了虚拟路径 /images/)
            String fileUrl = "http://localhost:8080/images/" + newFileName;

            result.put("code", 200);
            result.put("url", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "上传失败: " + e.getMessage());
        }
        return result;
    }

    // 2. 更新用户信息接口
    @PostMapping("/update")
    public Map<String, Object> updateInfo(@RequestBody User user) {
        // 这里应该做鉴权，确保只能修改自己的信息，这里简化处理
        userMapper.update(user);

        // 重新查询最新信息返回，用于更新前端 Store
        User updatedUser = userMapper.findById(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "更新成功");
        result.put("userInfo", updatedUser);
        return result;
    }
}