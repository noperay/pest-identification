package com.zx.insectdetection.service.Impl;

import com.zx.insectdetection.entity.websoket.Message;
import com.zx.insectdetection.entity.websoket.UsersMessage;
import com.zx.insectdetection.mapper.MessageMapper;
import com.zx.insectdetection.service.MessageService;
import com.zx.insectdetection.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Override
    public Boolean saveMessages(Message message,Integer channelId) {
        message.setSendTime(new Date());
        message.setChannelId(channelId);
        Boolean result = messageMapper.saveMessage(message);
        if(result){
            return true;
        }
        return false;
    }
    @Override
    public List<Message> findAllMessages() {
        return messageMapper.findAllMessages();
    }
    @Override
    public List<UsersMessage> getUsersMessages(Integer receiverId) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer senderId = (Integer) map.get("id");
        List<UsersMessage> usersMessages = messageMapper.getUsersMessages(senderId, receiverId);
        return usersMessages;
    }
    @Override
    public Boolean sendUsersMessages(UsersMessage usersMessage) {
        usersMessage.setSendTime(new Date());
        Boolean result = messageMapper.sendUsersMessages(usersMessage);
        if(result){
            return true;
        }
        return false;
    }

    @Override
    public List<Message> findMessageByChannelId(Integer channelId) {
        return messageMapper.findMessageByChannelId(channelId);
    }
}
