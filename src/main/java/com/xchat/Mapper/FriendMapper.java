package com.xchat.Mapper;

import com.xchat.Entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FriendMapper {
    // 添加好友关系
    @Insert("INSERT INTO friendship(user_id, friend_id) VALUES(#{userId}, #{friendId})")
    void addFriend(Long userId, Long friendId);

    // 检查是否已经是好友
    @Select("SELECT count(*) FROM friendship WHERE user_id = #{userId} AND friend_id = #{friendId}")
    int checkFriendship(Long userId, Long friendId);

    // 获取我的好友列表 (关联查询 User 表)
    @Select("SELECT u.* FROM user u JOIN friendship f ON u.id = f.friend_id WHERE f.user_id = #{userId}")
    List<User> findFriendsByUserId(Long userId);
}