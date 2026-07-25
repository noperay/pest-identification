package com.zx.insectdetection.service;

import com.zx.insectdetection.entity.chat.ChatMessage;
import com.zx.insectdetection.entity.websoket.Message;
import com.zx.insectdetection.entity.websoket.UsersMessage;

import java.util.List;

public interface MessageService {
    Boolean saveMessages(Message message,Integer channelId);
    List<Message> findAllMessages();
    List<UsersMessage> getUsersMessages(Integer userId);
    Boolean sendUsersMessages(UsersMessage usersMessage);

    List<Message> findMessageByChannelId(Integer channelId);

}
