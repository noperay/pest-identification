//导入request.js请求工具
import { apiInstance } from '@/utils/apirequest.js';
// 将对象转换为 URLSearchParams 的公共函数
const toURLSearchParams = (data) => {
    const params = new URLSearchParams();
    for (let key in data) {
        params.append(key, data[key]);
    }
    return params;
};

//获取世界频道聊天记录
export const getAllMessagesHistory = async () => {
    return apiInstance.get('/chat/getmessages')
}
//根据用户id查询订阅频道
export const findChannelByUserIdService = async (userId) => {
    return apiInstance.get(`/channel/findChannelByUserId`, { params: { userId } });
}
//根据频道id查询频道
export const findChannelByIdService = async (channelId) => {
    return apiInstance.get(`/channel/findChannelById`, { params: { channelId } });
}
//根据频道id查询频道消息
export const findMessageByChannelId = async (channelId) => {
    return apiInstance.get(`/chat/findMessageByChannelId`, { params: { channelId } });
}
//禁用频道
export const disableChannel = async (channelId) => {
    return apiInstance.get(`/channel/disableChannel`, { params: { channelId } });
}
//启用频道
export const enableChannel = async (channelId) => {
    return apiInstance.get(`/channel/enableChannel`, { params: { channelId } });
}
//修改频道
export const updateChannel = async (channel) => {
    return apiInstance.put(`/channel/updateChannel`, channel);
}
//根据用户id查询私信列表
export const getUserChats = async (userId) => {
    return apiInstance.get(`/chat/getUserChats`, { params: { userId } });
}
//根据私信id获取信息
export const getUsersMessage = async (chatId) => {
    return apiInstance.get(`/chat/getUsersMessages/${chatId}`);
}
//增加私信列表
export const addChatList = async (chat) => {
    return apiInstance.post(`/chat/addChat`, chat);
}
//根据列表id删除私信列表
export const deleteChat = async (chatId) => {
    return apiInstance.delete(`/chat/deleteChat`, { params: { chatId } });
}
//增加私信
export const addUsersMessage = async (chatMessage) => {
    const params = toURLSearchParams(chatMessage);
    return apiInstance.post(`/chat/addUsersMessage`, params);
}
//删除私信
export const deleteUsersMessage = async (chatMessageId) => {
    return apiInstance.delete(`/chat/deleteUsersMessage`, { params: { chatMessageId } });
}
//订阅频道
export const subscribeChannel = async (channelId) => {
    const formData = new FormData();
    formData.append('channelId', channelId);
    return apiInstance.post('/channel/subscribeChannel', formData);
}
//查询是否订阅频道
export const findIfChannelSubscribe = async (channelId) => {
    return apiInstance.get(`/channel/findIfChannelSubscribe/${channelId}`);
}
//取消订阅频道
export const unsubscribeChannel = async (channelId) => {
    const formData = new FormData();
    formData.append('channelId', channelId);
    return apiInstance.post('/channel/unsubscribeChannel', formData);
}
//创建频道
export const createChannelService = async (channel) => {
    return apiInstance.post('/channel/createChannel', channel);
}
//删除频道
export const deleteChannelService = async (channelId) => {
    const formData = new FormData();
    formData.append('channelId', channelId);
    return apiInstance.post('/channel/deleteChannel', formData);
}
//获取频道列表
export const getChannelListService = async () => {
    return apiInstance.get('/channel/getChannelList');
}
//根据自定义用户id创建频道
export const createChannelByUserIdService = async (channel) => {
    return apiInstance.post('/channel/createChannelByUserId/', channel);
}
//根据信息id删除频道信息
export const deleteMessageByIdService = async (messageId) => {
    return apiInstance.delete(`/chat/deleteMessageById/${messageId}`);
}