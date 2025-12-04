package com.xchat.Mapper;

import com.xchat.Entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    @Insert("INSERT INTO user(email, password, nickname, avatar) VALUES(#{email}, #{password}, #{nickname}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT id, email, nickname, avatar FROM user")
    List<User> findAll();
}
