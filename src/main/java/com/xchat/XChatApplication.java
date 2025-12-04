package com.xchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

// 确保这个类所在的包能扫描到 Controller, Mapper, WebSocketConfig 等包
// 如果你采用了默认包结构 (直接在 src/main/java 下)，则不需要 package 声明
// 如果采用了 com.xchat 结构，请加上 package com.xchat;

@SpringBootApplication
@MapperScan("com.xchat.Mapper") // 扫描 MyBatis Mapper 接口所在的包名
public class XChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(XChatApplication.class, args);
    }
}