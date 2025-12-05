package com.xchat.Entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private String bio;
}