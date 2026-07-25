<template>
    <div v-if="myCreateChannels.length === 0">
        用户暂未创建频道
    </div>
    <div v-else class="MyChannelContainer">
        <div v-for="channel in myCreateChannels" :key="channel.id">
            <div class="channelName">
                <span class="channelNameText">{{ channel.channelName }}</span>
                <div v-if="channel.administratorId !== userInfo.info.id">
                    <div v-if="channel.ifSubscribe">
                        <el-button class="button-cancel" type="info" plain
                            @click="CancelSubScribeChannel(channel.id)">取消订阅</el-button>
                    </div>
                    <el-button v-else class="button" type="info" plain
                        @click="subScribeChannel(channel.id)">订阅</el-button>
                </div>
                <el-button v-else class="button" type="info" plain @click="openManageDialog(channel)">管理</el-button>
            </div>
        </div>
        <el-dialog class="eldialog" width="370px" v-model="dialogVisible" title="频道管理">
            <div>
                <span>频道名称：</span>
                <el-input class="elinput" v-model="currentChannel.channelName"
                    placeholder="{{ currentChannel.channelName }}"></el-input>
                <el-button class="btn-change" type="primary" @click="saveChannelChanges(currentChannel)">修改</el-button>
            </div>
            <div style="margin-top: 20px;">
                <span>频道状态：</span>
                <el-switch v-model="channalStatus" class="ml-2" @click="changeStatus(currentChannel.id)"
                    style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949;margin-left: 10px;" />
                <el-button style="margin-left: 124px;height: 35px;" type="danger"
                    @click="deleteChannel(currentChannel.id)">删除频道</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, defineProps } from 'vue';
import { getCreateChannelService } from '@/api/user.js';
import { useUserListStore, useUserInfoStore, useFollowListStore } from '@/stores/userInfo.js';
import { disableChannel, enableChannel, updateChannel, subscribeChannel, findIfChannelSubscribe, unsubscribeChannel, deleteChannelService } from '@/api/message.js'
import { ElMessage } from 'element-plus';
const userInfo = useUserInfoStore();
const myCreateChannels = ref([]); // 我订阅的频道
const dialogVisible = ref(false);
const currentChannel = ref({});
const channalStatus = ref(true)
const props = defineProps({
    userId: {
        type: Number,
        required: true
    }
});
// 查询我创建的频道
const findCreateChannel = async () => {
    try {
        let result = await getCreateChannelService(props.userId);
        myCreateChannels.value = result.data;
        for (let i = 0; i < myCreateChannels.value.length; i++) {
            let channel = myCreateChannels.value[i];
            let isSubscribeData = await findIfChannelSubscribe(channel.id);
            if (isSubscribeData.data === "已订阅") {
                channel.ifSubscribe = true;
            } else {
                channel.ifSubscribe = false;
            }
        }
        // console.log(myCreateChannels.value);

    } catch (error) {
        console.error('Error finding channels:', error);
    }
}
// 修改频道状态
const changeStatus = async (channelId) => {
    try {
        if (channalStatus.value === false) {
            await disableChannel(channelId);
            ElMessage({
                message: '已禁用该频道',
                type: 'success',
            })
        } else {
            await enableChannel(channelId);
            ElMessage({
                message: '已启用该频道',
                type: 'success',
            })
        }
        findCreateChannel();
    } catch (error) {
        console.error('Error changing channel status:', error);
    }

}
// 打开管理对话框
const openManageDialog = (channel) => {
    dialogVisible.value = true;
    currentChannel.value = { ...channel };
    if (channel.status === "已启用") {
        channalStatus.value = true
    } else if (channel.status === "已禁用") {
        channalStatus.value = false
    }
}
//修改频道信息
const saveChannelChanges = async (channal) => {
    // 这里可以添加保存修改的逻辑，例如调用 API 更新频道信息
    // console.log(channal);
    await updateChannel(channal);
    ElMessage({
        message: '修改成功',
        type: 'success',
    })
    findCreateChannel();
    dialogVisible.value = false;
}
findCreateChannel();
//删除频道
const deleteChannel = async (channelId) => {
    try {
        await deleteChannelService(channelId);
        findCreateChannel();
        dialogVisible.value = false;
        ElMessage({
            message: '删除成功',
            type: 'success',
        })
    } catch (error) {
        console.error('Error deleting channel:', error);
    }
}
//订阅频道
const subScribeChannel = async (channelId) => {
    await subscribeChannel(channelId);
    findCreateChannel();
    ElMessage({
        message: '订阅成功',
        type: 'success',
    })
}
//取消订阅频道
const CancelSubScribeChannel = async (channelId) => {
    await unsubscribeChannel(channelId);
    findCreateChannel();
    ElMessage({
        message: '取消订阅成功',
        type: 'success',
    })
}
// 将方法暴露给父组件
defineExpose({
    findCreateChannel
});
</script>
<style scoped>
.MyChannelContainer {
    height: 100%;
    /* align-items: center; */
    font-size: 17px;
}

.channelName {
    height: 30px;
    padding: 5px;
    display: flex;
    /* flex-direction: column; */
}

.channelNameText {
    margin-left: 10px;
    flex: 8;
}

.button {
    flex: 1;
    height: 25px;
    width: 50px;
    margin-right: 10px;
}

.elinput {
    width: 200px;
}

.btn-change {
    width: 50px;
    margin-left: 10px;
}

.button-cancel {
    flex: 1;
    height: 25px;
    width: 70px;
    margin-right: 10px;
}
</style>