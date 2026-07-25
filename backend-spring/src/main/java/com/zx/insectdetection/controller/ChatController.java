package com.zx.insectdetection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.insectdetection.entity.chat.Chat;
import com.zx.insectdetection.entity.chat.ChatMessage;
import com.zx.insectdetection.entity.others.Result;
import com.zx.insectdetection.entity.websoket.FullMessage;
import com.zx.insectdetection.entity.websoket.Message;
import com.zx.insectdetection.entity.websoket.UsersMessage;
import com.zx.insectdetection.mapper.ChatMapper;
import com.zx.insectdetection.service.ChatService;
import com.zx.insectdetection.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMapper chatMapper;
    @MessageMapping("/sendmessage/general")
    public void sendMessage(Message message) {
        Integer channelId = message.getChannelId();
        boolean result = messageService.saveMessages(message, channelId);
        if (result) {
            String destination = "/channel/" + channelId;
            try {
                messagingTemplate.convertAndSend(destination, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 发送私信
    @MessageMapping("/sendmessage/user")
    public void sendUsersMessages(ChatMessage chatMessage) {
        chatService.addUsersMessage(chatMessage);
        Integer chatId = chatMessage.getChatId();
        String destination = "/userchat/" + chatId;
        try {
            messagingTemplate.convertAndSend(destination, chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @MessageMapping("/getChats")
    @SendTo("/chat/getChats")
    public Chat getChats(Chat chat) {
//        Chat newChat =  chatMapper.getChat(chat);
        System.out.println(chat);
        return chat;
    }

    //查找所有信息
    @GetMapping("/getmessages")
    public Result<List<Message>> getMessages() {
        List<Message> messages = messageService.findAllMessages();
        return Result.success(messages);
    }
    //根据频道id查找信息
    @GetMapping("/findMessageByChannelId")
    public Result<List<Message>> getMessage(@RequestParam Integer channelId) {
        List<Message> message = messageService.findMessageByChannelId(channelId);
        return Result.success(message);
    }

    //增加私信列表
    @PostMapping("/addChat")
    public Result addChat(@RequestBody Chat chat) {
        System.out.println(chat);
        chatService.addChat(chat);
        return Result.success("添加成功");
    }
    //删除私信列表
    @DeleteMapping("/deleteChat")
    public Result deleteChat(@RequestParam Integer chatId) {
        chatService.deleteChat(chatId);
        return Result.success("删除成功");
    }
    //根据userId查找私信列表
    @GetMapping("/getUserChats")
    public Result<List<Chat>> getUserChats(@RequestParam Integer userId) {
        List<Chat> chats = chatService.getUserChats(userId);
        return Result.success(chats);
    }
    //插入用户私信
    @PostMapping("/addUsersMessage")
    public Result addUsersMessage(@RequestBody ChatMessage chatMessage) {
        chatService.addUsersMessage(chatMessage);
        return Result.success();
    }
    //删除用户私信
    @DeleteMapping("/deleteUsersMessage")
    public Result deleteUsersMessage(@RequestParam Integer chatId) {
        chatService.deleteUsersMessage(chatId);
        return Result.success();
    }
    //根据chatId查找用户私信
    @GetMapping("/getUsersMessages/{chatId}")
    public Result<List<ChatMessage>> getUsersMessages(@PathVariable Integer chatId) {
        List<ChatMessage> chatMessages = chatService.getUsersMessages(chatId);
        return Result.success(chatMessages);
    }
    //获取ChatMessage全部数据
    @GetMapping("/getAllChatMessage")
    public Result<List<ChatMessage>> getAllChatMessage() {
        List<ChatMessage> chatMessages = chatMapper.getAllChatMessage();
        return Result.success(chatMessages);
    }
    //删除频道信息
    @DeleteMapping("/deleteMessageById/{messageId}")
    public Result deleteMessageById(@PathVariable Integer messageId) {
        chatMapper.deleteMessageById(messageId);
        return Result.success();
    }
}