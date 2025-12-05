package com.xchat.Mapper;

import com.xchat.Entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface MessageMapper {
    @Insert("INSERT INTO message(sender_id, receiver_id, content) VALUES(#{senderId}, #{receiverId}, #{content})")
    void insert(Message message);

    @Select("SELECT * FROM message " +
            "WHERE (sender_id = #{userId} AND receiver_id = #{contactId}) " +
            "   OR (sender_id = #{contactId} AND receiver_id = #{userId}) " +
            "ORDER BY created_at ASC")
    List<Message> findChatHistory(Long userId, Long contactId);
}



