package Mapper;

import Entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    @Insert("INSERT INTO user(email, password, nickname, avatar) VALUES(#{email}, #{password}, #{nickname}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
}
