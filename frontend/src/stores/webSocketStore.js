import { defineStore } from 'pinia';
import { ref } from 'vue';
import { Client } from '@stomp/stompjs';
import { findChannelByUserIdService, findChannelByIdService, findMessageByChannelId } from '@/api/message.js';
import { useUserInfoStore } from '@/stores/userInfo.js';
// 定义 WebSocket 存储
export const useWebSocketStore = defineStore('webSocket', () => {
    const isConnected = ref(false);
    const client = ref(null);
    const stompClient = ref(null);
    const receivedMessagesByChannel = ref({}); // 用于存储不同频道的消息
    const newMessage = ref('');
    const newMessageChat = ref('');
    const chatList = ref('');
    // 新增：用于记录已订阅的频道
    const subscribedChannels = ref({});
    const subscribedChats = ref({});
    const subScribedChatList = ref({});
    const userInfo = useUserInfoStore();
    const mySubscribeChannels = ref([]); //我订阅的频道

    // 连接 WebSocket 服务器
    const connectWebSocket = async () => {
        client.value = new Client({
            // brokerURL: 'ws://106.52.231.216:8082/ws',
            brokerURL: 'ws://localhost:8080/ws',
            onConnect: async () => {
                console.log('WebSocket 连接成功');
                isConnected.value = true;
                stompClient.value = client.value;
                // 等待 findChannelByUserId 执行完毕
                await findChannelByUserId();
                // console.log(mySubscribeChannels.value);
                mySubscribeChannels.value.forEach((channel) => {
                    try {
                        // console.log(channel.id);
                        // 订阅频道
                        subscribeToChannel(channel.id);
                    } catch (error) {
                        console.error(`订阅频道 ${channel.id} 时出错:`, error);
                    }
                });
            },
            onDisconnect: async () => {
                console.log('WebSocket 连接断开，尝试重新连接...');
                isConnected.value = false;
                // 连接断开时清空已订阅频道记录
                subscribedChannels.value = {};
                // 尝试重新连接
                await reconnect();
            },
            onStompError: (frame) => {
                console.error('STOMP 协议错误:', frame.headers['message']);
                isConnected.value = false;
                // 出现错误时清空已订阅频道记录
                subscribedChannels.value = {};
            },
            onWebSocketError: (error) => {
                console.error('WebSocket 连接出错:', error);
                isConnected.value = false;
                // 出现错误时清空已订阅频道记录
                subscribedChannels.value = {};
            }
        });
        await client.value.activate();
    };
    // 重连方法
    const reconnect = async () => {
        let retryCount = 0;
        const maxRetries = 5;
        const retryInterval = 5000; // 5 秒重试一次

        while (retryCount < maxRetries) {
            try {
                await client.value.activate();
                console.log('重新连接成功');
                break;
            } catch (error) {
                retryCount++;
                console.log(`重新连接失败，第 ${retryCount} 次重试，将在 ${retryInterval / 1000} 秒后再次尝试...`);
                await new Promise(resolve => setTimeout(resolve, retryInterval));
            }
        }

        if (retryCount === maxRetries) {
            console.error('达到最大重试次数，无法重新连接');
        }
    };
    // 查询我订阅的频道
    const findChannelByUserId = async () => {
        try {
            let result = await findChannelByUserIdService(userInfo.info.id);
            const promises = result.data.map((channel) => {
                return findChannelByIdService(channel.channelId);
            });
            const responses = await Promise.all(promises);
            // 展开嵌套数组
            responses.forEach((res) => {
                if (Array.isArray(res.data)) {
                    mySubscribeChannels.value.push(...res.data);
                } else {
                    mySubscribeChannels.value.push(res.data);
                }
            });
        } catch (error) {
            console.error('Error finding channels:', error);
        }
    };
    // 订阅频道
    const subscribeToChannel = (channelId) => {
        if (stompClient.value && isConnected.value) {
            const channelPath = `/channel/${channelId}`;
            // console.log(channelPath)
            // 检查该频道是否已经订阅
            if (subscribedChannels.value[channelId]) {
                // console.log(`已经订阅了频道 ${channelId}，无需重复订阅`);
                return;
            }
            const subscription = stompClient.value.subscribe(channelPath, (message) => {
                try {
                    const parsedMessage = JSON.parse(message.body);
                    newMessage.value = parsedMessage;
                    // console.log(newMessage.value)
                } catch (error) {
                    console.error('解析消息出错:', error);
                }
            });
            // 记录该频道已订阅
            subscribedChannels.value[channelId] = subscription;
        } else {
            console.error('WebSocket 未连接，无法订阅频道');
        }
    };
    //订阅私信
    const subscribeToChat = (chatId) => {
        if (stompClient.value && isConnected.value) {
            const chatPath = `/userchat/${chatId}`;
            // console.log(chatPath)
            // 检查该私信是否已经订阅
            if (subscribedChats.value[chatId]) {
                return;
            }
            const subscription = stompClient.value.subscribe(chatPath, (message) => {
                try {
                    const parsedMessage = JSON.parse(message.body);
                    newMessageChat.value = parsedMessage;
                    // console.log(newMessageChat.value)
                } catch (error) {
                    console.error('解析消息出错:', error);
                }
            });
            // 记录该私信已订阅
            subscribedChats.value[chatId] = subscription;
        } else {
            console.error('WebSocket 未连接，无法订阅频道');
        }
    };
    //订阅私信列表
    const subChats = (userId) => {
        if (stompClient.value && isConnected.value) {
            const chatPath = `/chat/getChats`;
            // 检查该私信是否已经订阅
            if (subScribedChatList.value[userId]) {
                // console.log(`已经订阅了频道 ${chatId}，无需重复订阅`);
                return;
            }
            const subscription = stompClient.value.subscribe(chatPath, (message) => {
                try {
                    const newChatList = JSON.parse(message.body);
                    chatList.value = newChatList;
                    // console.log(chatList.value)
                } catch (error) {
                    console.error('解析消息出错:', error);
                }
            });
            // 记录该私信列表已订阅
            subScribedChatList.value[userId] = subscription;
        } else {
            console.error('WebSocket 未连接，无法订阅频道');
        }
    };

    // 取消订阅频道
    const unsubscribeFromChannel = (channelId) => {
        const subscription = subscribedChannels.value[channelId];
        if (subscription) {
            subscription.unsubscribe();
            // 从已订阅频道记录中移除
            delete subscribedChannels.value[channelId];
            console.log(`已取消订阅频道 ${channelId}`);
        } else {
            console.log(`未订阅频道 ${channelId}，无需取消订阅`);
        }
    };

    // 发送频道消息
    const sendMessage = (message, channelId) => {
        const fullMessage = {
            ...message,
            channelId
        };
        if (stompClient.value && isConnected.value) {
            stompClient.value.publish({
                destination: '/app/sendmessage/general',
                body: JSON.stringify(fullMessage)
            });
        } else {
            console.error('WebSocket 未连接，无法发送消息');
        }
    };
    // 用户发送私信
    const sendMessageToUser = (message, chatId) => {
        const fullMessage = {
            ...message,
            chatId
        };
        if (stompClient.value && isConnected.value) {
            stompClient.value.publish({
                destination: '/app/sendmessage/user',
                body: JSON.stringify(fullMessage)
            });
        } else {
            console.error('WebSocket 未连接，无法发送消息');
        }
    };
    //更新私信列表
    const updateChats = (chat) => {
        if (stompClient.value && isConnected.value) {
            stompClient.value.publish({
                destination: '/app/getChats',
                body: JSON.stringify(chat)
            });
        } else {
            console.error('WebSocket 未连接，无法更新私信列表');
        }

    };
    return {
        isConnected,
        connectWebSocket,
        subscribeToChannel,
        subscribeToChat,
        unsubscribeFromChannel, // 新增：取消订阅方法
        sendMessage,
        receivedMessagesByChannel,
        newMessage,
        newMessageChat,
        findChannelByUserId,
        sendMessageToUser,
        chatList,
        subChats,
        updateChats
    };
}, {
    // 使用持久化插件
    persist: true
});