package com.zx.insectdetection.mapper;

import com.zx.insectdetection.entity.websoket.Message;
import com.zx.insectdetection.entity.websoket.UsersMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {
    //查找所有信息
    @Select("select * from message")
    List<Message> findAllMessages();
    //保存信息
    @Insert("insert into message (user_id, username, message, send_time,channel_id) values (#{userId}, #{username}, #{message}, #{sendTime},#{channelId})")
    boolean saveMessage(Message message);
    //查找用户发送信息
    @Select("select * from users_message where sender_id = #{senderId} and receiver_id = #{receiverId}")
    List<UsersMessage> getUsersMessages(@Param("senderId") Integer senderId,@Param("receiverId") Integer receiverId);
    //发送用户信息
    @Insert("insert into users_message (sender_id, receiver_id, message, send_time) values (#{senderId}, #{receiverId}, #{message}, #{sendTime})")
    Boolean sendUsersMessages(UsersMessage usersMessage);
    //根据channelId查找信息
    @Select("select * from message where channel_id = #{channelId}")
    List<Message> findMessageByChannelId(Integer channelId);
    //修改message表的username
    @Update("update message set username = #{username} where user_id = #{userId}")
    Boolean updateMessageUsername(@Param("username") String username,@Param("userId") Integer userId);
}
