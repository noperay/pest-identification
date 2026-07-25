<template>
  <div class="chat-container">
    <!-- 左侧 -->
    <div class="chat-sidebar z-card">
      <el-aside width="200px">
        <div class="el-aside__logo"></div>
        <el-menu :default-active="activeMenu">
          <!-- 我的订阅子菜单 -->
          <el-sub-menu index="1">
            <template #title>
              <el-icon>
                <icons.Menu />
              </el-icon>
              <span>我的订阅</span>
            </template>
            <el-menu-item v-for="channel in mySubscribeChannels" :key="channel.id" :index="`channel-${channel.id}`">
              <div :style="{ position: 'relative' }">
                <div v-if="channel.status === '已启用'" @click="goToChannel(channel)">
                  <el-icon>
                    <icons.Message />
                  </el-icon>
                  <span>{{ channel.channelName }}</span>
                </div>
                <div v-else class="disabled-overlay">
                  {{ channel.channelName }}（禁用）
                </div>
              </div>
            </el-menu-item>
          </el-sub-menu>

          <!-- 我的私信子菜单 -->
          <el-sub-menu index="2">
            <template #title>
              <el-icon>
                <icons.Menu />
              </el-icon>
              <span>我的私信</span>
            </template>
            <el-menu-item v-for="chat in myChats" :key="chat.id" :index="`chat-${chat.id}`" @click="goToUserChat(chat)">
              <el-icon>
                <icons.Message />
              </el-icon>
              <span>{{ chat.nickname }}</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
    </div>
    <!-- 聊天Main -->
    <div class="chat-main">
      <el-card class="box-card">
        <template #header>
          {{ chatBoxTitle }}
        </template>
        <div class="chat-content">
          <el-scrollbar ref="scrollbarRef" class="message-list" height="530px">
            <div v-if="sendType === 'channel'" v-for="message in messageList" :key="message.id" class="message-item"
              :class="{ 'self-message': message.userId == userInfo.info.id }">
              <template v-if="message.userId != userInfo.info.id">
                <el-popover placement="right-start" width="20" trigger="click" popper-class="custom-popover">
                  <div class="popover-content">
                    <div class="menu-item" @click="toUserProfile(message.userId)"> 个人主页</div>
                    <div class="menu-item" @click="addChat(message)"> 发送私信</div>
                  </div>
                  <template #reference>
                    <el-card shadow="hover" class="message-card">
                      <div slot="header" class="username flex">
                        {{ message.username }}
                      </div>
                      <div class="message-content">{{ message.message }}</div>
                      <div class="message-time">{{ formatTime(message.sendTime) }}</div>
                    </el-card>
                  </template>
                </el-popover>
              </template>
              <template v-else>
                <el-card shadow="hover" class="message-card">
                  <div slot="header" class="username flex">
                    {{ message.username }}
                  </div>
                  <div class="message-content">{{ message.message }}</div>
                  <div class="message-time">{{ formatTime(message.sendTime) }}</div>
                </el-card>
              </template>
            </div>
            <div v-if="sendType === 'userchat'" v-for="message in userChatMessageList" :key="message.id"
              class="message-item" :class="{ 'self-message': message.senderId == userInfo.info.id }">
              <template v-if="message.senderId != userInfo.info.id">
                <el-popover placement="right-start" width="20" trigger="click" popper-class="custom-popover">
                  <div class="popover-content">
                    <div class="menu-item" @click="toUserProfile(message.senderId)"> 个人主页</div>
                  </div>
                  <template #reference>
                    <el-card shadow="hover" class="message-card">
                      <div slot="header" class="username flex">
                        {{ message.senderName }}
                      </div>
                      <div class="message-content">{{ message.message }}</div>
                      <div class="message-time">{{ formatTime(message.createTime) }}</div>
                    </el-card>
                  </template>
                </el-popover>
              </template>
              <template v-else>
                <el-card shadow="hover" class="message-card">
                  <div slot="header" class="username flex">
                    {{ message.senderName }}
                  </div>
                  <div class="message-content">{{ message.message }}</div>
                  <div class="message-time">{{ formatTime(message.createTime) }}</div>
                </el-card>
              </template>
            </div>
          </el-scrollbar>
          <div class="input-container">
            <div class="input-row">
              <el-input v-model="messageText" type="textarea" :rows="3" placeholder="请输入消息内容"
                @keyup.enter.native="sendMessage"></el-input>
            </div>
            <el-button type="primary" :disabled="!messageText" @click="sendMessage()">发送消息</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import * as icons from '@element-plus/icons-vue';
import { ref, onMounted, watch, nextTick, computed, watchEffect } from 'vue';
import { ElNotification, ElPopover } from 'element-plus';
import { useUserInfoStore } from '@/stores/userInfo.js';
import { findChannelByUserIdService, findChannelByIdService, findMessageByChannelId, getUserChats, getUsersMessage, addChatList } from '@/api/message.js';
import { getUserByIdService } from '@/api/user.js';
import { useWebSocketStore } from '@/stores/webSocketStore.js';
import { notification } from 'ant-design-vue';
const webSocketStore = useWebSocketStore();
const userInfo = useUserInfoStore();
const messageText = ref('');
const messageList = ref([]);
const userChatMessageList = ref([]);
const scrollbarRef = ref(null);
const mySubscribeChannels = ref([]); // 我订阅的频道
const myChats = ref([]); // 我的私信列表
const activeChannelId = ref();
const activeChatId = ref();
const chatBoxTitle = ref('公共频道');
const sendType = ref('channel'); // 发送类型，默认为频道
const activeReceiverId = ref();
const activeReceiverName = ref();
const activeMenu = ref('');
// 显示可点击的通知
const showPrivateMessageNotification = (message) => {
  ElNotification({
    title: `${message.senderName}  发来新消息`,
    message: '点击查看',
    type: 'info',
    // duration: 2500,
    onClick: () => {
      handleElNotificationClick(message);
    }
  });
};
const handleElNotificationClick = (message) => {
  for (const chat of myChats.value) {
    if (chat.id == message.chatId) {
      goToUserChat(chat);
      return;
    }
  }
}

onMounted(async () => { // 初始化
  await connectWebSocket();
  await findChannelByUserId();
  // 初始化时订阅第一个频道
  if (mySubscribeChannels.value.length > 0) {
    activeChannelId.value = mySubscribeChannels.value[0].id;
    chatBoxTitle.value = mySubscribeChannels.value[0].channelName;
    activeMenu.value = `channel-${activeChannelId.value}`;
    await subscribeChannel();
    await getMessageByChannelId(activeChannelId.value);

  }
  await getUserChatsByUserId();
  await subscribeChatList();
  await subscribeChat();
});

// 连接 websocket 服务器 
const connectWebSocket = async () => {
  await webSocketStore.connectWebSocket();
};
// 订阅私信列表
const subscribeChatList = async () => {
  await webSocketStore.subChats(userInfo.info.id);
}
//获取我的私信列表
const getUserChatsByUserId = async () => {
  try {
    const result = await getUserChats(userInfo.info.id);
    // console.log(result.data);
    // 使用 Promise.all 并行处理所有异步请求
    const newChats = await Promise.all(result.data.map(async (chat) => {
      const userId = chat.senderId == userInfo.info.id ? chat.receiverId : chat.senderId;
      const result1 = await getUserByIdService(userId);
      const nickname = result1.data.nickname
      // 合并原有的 chat 对象和新的 username 属性
      return {
        ...chat,
        nickname
      };
    }));
    myChats.value = newChats;
  } catch (error) {
    console.error('Error finding channels:', error);
  }
}
getUserChatsByUserId();
//订阅私信
const subscribeChat = async () => {
  // console.log(myChats.value);
  for (const chat of myChats.value) {
    try {
      await webSocketStore.subscribeToChat(chat.id);
    } catch (error) {
      console.error('Error subscribing to chat:', error);
    }
  }
}
subscribeChat();
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
    // 检查 mySubscribeChannels 是否有数据
    if (mySubscribeChannels.value.length > 0) {
      // 设置 activeChannelId 为第一个频道的 id
      activeChannelId.value = mySubscribeChannels.value[0].id;
      chatBoxTitle.value = mySubscribeChannels.value[0].channelName;
    }
  } catch (error) {
    console.error('Error finding channels:', error);
  }
};

//订阅频道
const subscribeChannel = async () => {
  try {
    await webSocketStore.subscribeToChannel(activeChannelId.value);
  } catch (error) {
    console.error('Error subscribing to channel:', error);
  }
}

// 发送消息
import axios from 'axios';
import { ElMessage } from 'element-plus';
import sensitiveWords from '@/assets/js/sensitiveWords.js';
const sendMessage = async () => {
  const message = messageText.value;

  // 检查是否包含敏感词
  for (const word of sensitiveWords) {
    if (message.includes(word)) {
      ElMessage.error("您的发言包含敏感词：" + word);
      return;
    }
  }

  // 没有敏感词，继续发送消息
  if (sendType.value === 'channel') {
    await sendMessageToChannel();
  } else if (sendType.value === 'userchat') {
    await sendMessageToChat();
  }
}
// const sendMessage = async () => {
//   const params = {
//     msg: messageText.value,
//   };
//   // const result = await axios.get('/chatapi/api/Forbidden', { params });
//   // const result = await axios.get('https://suapi.net/api/text/badword', { params });
//   const result = await axios.get('https://v.api.aa1.cn/api/api-mgc/index.php', { params });
//   // 提取JSON部分
//   const jsonRegex = /\{("[^"]+":(?:\d+|"[^"]*"),?)+\}/;
//   const dataFormats = result.data;
//   const match = dataFormats.match(jsonRegex);
//   if (match) {
//     const jsonStr = match[0];
//     const jsonObj = JSON.parse(jsonStr);
//     const code = jsonObj.code;
//     const num = jsonObj.num;
//     const desc = jsonObj.desc;
//     const ci = jsonObj.ci;
//     if (desc == '存在敏感词') {
//       ElMessage.error("您的发言包含敏感词：" + ci);
//       return;
//     }
//     if (sendType.value === 'channel') {
//       await sendMessageToChannel();
//     } else if (sendType.value === 'userchat') {
//       await sendMessageToChat();
//     }
//   } else {
//     console.error('未找到有效的 JSON 数据');
//   }
// }

// 发送频道消息 
const sendMessageToChannel = async () => {
  try {
    const message = {
      userId: userInfo.info.id,
      username: userInfo.info.nickname,
      message: messageText.value,
      sendTime: new Date().getTime()
    };
    webSocketStore.sendMessage(message, activeChannelId.value);
    messageText.value = '';
  } catch (error) {
    console.error('   发送消息失败:', error);
    ElNotification({
      title: '消息发送失败',
      type: 'error',
      duration: 2000
    });
  }
};

//发送私信
const sendMessageToChat = async () => {
  try {
    const message = {
      senderId: userInfo.info.id,
      senderName: userInfo.info.nickname,
      receiverId: activeReceiverId.value,
      receiverName: activeReceiverName.value,
      message: messageText.value
    };
    webSocketStore.sendMessageToUser(message, activeChatId.value);
    messageText.value = '';
  } catch (error) {
    console.error('   发送消息失败:', error);
    ElNotification({
      title: '消息发送失败',
      type: 'error',
      duration: 2000
    });
  }
}

// 根据频道 id 获取消息
const getMessageByChannelId = async (channelId) => {
  messageList.value = [];
  try {
    const result = await findMessageByChannelId(channelId);
    messageList.value = result.data;
  } catch (error) {
    console.error('Error finding messages:', error);
  }
};

// 根据私信 id 获取消息
const getMessageByChatId = async (chatId) => {
  try {
    const result = await getUsersMessage(chatId);
    userChatMessageList.value = result.data;
  } catch (error) {
    console.error('Error finding messages:', error);
  }
};

// 切换频道
const goToChannel = async (channel) => {
  sendType.value = 'channel';
  activeChannelId.value = channel.id;
  chatBoxTitle.value = channel.channelName;
  activeMenu.value = `channel-${channel.id}`;
  await getMessageByChannelId(channel.id);
  messageText.value = '';
};

// 增加私信列表
const addChat = async (message) => {
  // console.log(myChats.value)
  for (const chat of myChats.value) {
    if ((chat.senderId === message.userId && chat.receiverId === userInfo.info.id) || (chat.receiverId === message.userId && chat.senderId === userInfo.info.id)) {
      console.log('已存在', chat);
      goToUserChat(chat);
      return;
    }
  }
  try {
    const chat = {
      senderId: userInfo.info.id,
      receiverId: message.userId,
    };
    await addChatList(chat);
    await getUserChatsByUserId();//更新我的私信列表
    await webSocketStore.updateChats(chat);//广播私信列表
    const newChat = myChats.value.find(chat => chat.receiverId === message.userId);
    goToUserChat(newChat);
  } catch {
    ElNotification({
      title: '进入私信失败',
      type: 'error',
      duration: 2000
    })
  }
};

// 去用户聊天
const goToUserChat = async (chat) => {
  sendType.value = 'userchat';
  activeChatId.value = chat.id;
  if (chat.senderId == userInfo.info.id) {
    activeReceiverId.value = chat.receiverId;
  } else {
    activeReceiverId.value = chat.senderId;
  }
  chatBoxTitle.value = chat.nickname;
  activeReceiverName.value = chat.nickname;
  messageText.value = '';
  activeMenu.value = `chat-${chat.id}`;
  getMessageByChatId(chat.id);
  await subscribeChat();
}

// 处理时间 
const formatTime = (time) => {
  const date = new Date(time);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 处理点击个人主页 
const toUserProfile = (userId) => {
  const url = `/user/userprofile/${userId}`;
  window.open(url, '_blank');
};

// 滚动到底部 
const scrollToBottom = () => {
  if (scrollbarRef.value) {
    const scrollbarEl = scrollbarRef.value.$el.querySelector('.el-scrollbar__wrap');
    if (scrollbarEl) {
      scrollbarEl.scrollTop = scrollbarEl.scrollHeight;
    }
  }
};

// 监听消息列表
watch(messageList, () => {
  nextTick(() => {
    scrollToBottom();
  });
});
// 监听消息列表
watch(userChatMessageList, () => {
  nextTick(() => {
    scrollToBottom();
  });
});
// 监听 newMessage 的变化
watch(() => webSocketStore.newMessage, (newVal) => {
  if (newVal && newVal.channelId === activeChannelId.value) {
    messageList.value.push(newVal);
    nextTick(() => {
      scrollToBottom();
    });
  }
}, { deep: true });
// 监听 newMessageChat 的变化
watch(() => webSocketStore.newMessageChat, (newVal) => {
  getUserChatsByUserId();
  if (newVal && newVal.chatId) {
    // 找到对应的聊天对象
    const chat = myChats.value.find(chat => chat.id === newVal.chatId);
    if (chat) {
      userChatMessageList.value.push(newVal);
      nextTick(() => {
        scrollToBottom();
      });
      if (newVal.receiverId == userInfo.info.id) {
        console.log("测试测试")
        showPrivateMessageNotification(newVal); // 显示通知
      }
    }
  }
}, { deep: true });
const test = async () => {
  await getUserChatsByUserId();
  await subscribeChatList();
  await subscribeChat();

}
// 监听 chatList 的变化
watch(() => webSocketStore.chatList, (chat) => {
  test()
}, { deep: true });
</script>

<style scoped>
.chat-container {
  display: flex;
  width: 100%;
  height: 800px;
}

.chat-sidebar {
  height: 765px;
  /* border: 1px solid rgb(205, 204, 204); */
  background-color: white;
  margin-right: 20px;
  width: 200px;
  /* padding: 10px; */
}

.channelStyleActive {
  border-right: 4px solid #409eff;
  padding: 10px;
  background-color: #e8f5ff;
  cursor: pointer;
}

.channelStyleActive:hover {
  background-color: #f4f4f4;
}

.channelStyle {
  padding: 10px;
  cursor: pointer;
  /* border: 1px solid rebeccapurple; */
}

.channelStyle:hover {
  background-color: #f4f4f4;
}

::v-deep .chat-sidebar>.el-card__body {
  padding: 0;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.message-list {
  flex: 1;
  margin-bottom: 10px;
}

.message-item {
  margin-bottom: 10px;
}

.message-card {
  max-width: 50%;
  margin-right: auto;
  cursor: pointer;
}

.self-message .message-card {
  margin-left: auto;
  margin-right: 0;
  background-color: #e8f5ff;
  border-left: 4px solid #409eff;
}

.username {
  font-weight: bold;
  color: #409eff;
  /* display: inline-block; */
}

.message-time {
  font-size: 0.8em;
  color: #666;
  margin-top: 5px;
}

.input-container {
  display: flex;
  flex-direction: column;
}

.input-row {
  margin-bottom: 10px;
}

.el-button--primary {
  width: 100%;
  margin-top: 10px;
}

.menu-item {
  padding: 5px 0;
  cursor: pointer;
}

.menu-item:hover {
  background-color: #f4f4f4;
}

.custom-popover {
  margin-left: 5px;
}

.b {
  border: 1px solid;
}

.el-menu {
  background-color: #ffffff;
}

.disabled-overlay {
  font-size: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.disabled-overlay:hover {
  cursor: not-allowed;
}
</style>