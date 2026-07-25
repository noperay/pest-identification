package com.zx.insectdetection.mapper;

import com.zx.insectdetection.entity.chat.Chat;
import com.zx.insectdetection.entity.chat.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMapper {
    @Select("select * from chat where sender_id = #{userId} or receiver_id = #{userId}")
    List<Chat> getUserChats(Integer userId);

    @Insert("insert into chat (sender_id,receiver_id,create_time) values (#{senderId}, #{receiverId},#{createTime})")
    void addChat(Chat chat);

    @Delete("delete from chat where id = #{chatId} ")
    void deleteChat(Integer chatId);
    //插入用户私信
    @Insert("insert into chat_message (chat_id,sender_id,sender_name,receiver_id,receiver_name, message, create_time) values (#{chatId},#{senderId},#{senderName},#{receiverId},#{receiverName},#{message}, #{createTime})")
    void addUsersMessage(ChatMessage chatMessage);

    //删除用户私信
    @Delete("delete from chat_message where id = #{chatId}")
    void deleteUsersMessage(Integer chatId);

    //根据chatId查找用户私信
    @Select("select * from chat_message where chat_id = #{chatId}")
    List<ChatMessage> getUsersMessages(Integer chatId);

    //判断是否已在私信列表
    @Select("select * from chat where (sender_id = #{senderId} or receiver_id = #{senderId}) or (sender_id = #{receiverId} or receiver_id = #{receiverId})")
    List<ChatMessage> isInChat(Integer senderId, Integer receiverId);

    //获取私信列表的私信
    @Select("select * from chat where (sender_id = #{senderId} and receiver_id = #{receiverId}) or (sender_id = #{receiverId} and receiver_id = #{senderId})")
    Chat getChat(Chat chat);

    //获取ChatMessage全部数据
    @Select("select * from chat_message")
    List<ChatMessage> getAllChatMessage();

    //根据messageId删除消息
    @Delete("delete from message where id = #{messageId}")
    void deleteMessageById(Integer messageId);
}
