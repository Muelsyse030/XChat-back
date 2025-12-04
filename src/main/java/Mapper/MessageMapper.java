package Mapper;

import Entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {
    @Insert("INSERT INTO message(sender_id, receiver_id, content) VALUES(#{senderId}, #{receiverId}, #{content})")
    void insert(Message message);
}
