<template>
    <el-card>
        <template #header>
            <span>频道信息管理</span>
            <el-button type="success" style="margin-left: 20px;" @click="openAddDialog()">新增频道</el-button>
        </template>
        <el-table :data="channelList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="channelName" label="频道名称" />
            <el-table-column prop="administratorId" label="管理者ID" />
            <el-table-column prop="name" label="管理者昵称" />
            <el-table-column prop="status" label="频道状态">
                <template #default="scope">
                    <el-switch v-model="scope.row.channalStatus" class="ml-2" @change="changeStatus(scope.row)"
                        style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949;margin-left: 10px;" />
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button class="btn-change" type="primary" @click="openManageDialog(scope.row)">修改</el-button>
                    <el-button type="danger" @click="deleteChannel(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
    <el-dialog class="eldialog" width="400px" v-model="editDialogVisible" title="频道管理">
        <div>
            <span>频道名称：</span>
            <el-input style="width: 200px;" v-model="currentChannel.channelName"
                placeholder="{{ currentChannel.channelName }}"></el-input>
            <el-button style="margin-left: 20px;" type="primary"
                @click="saveChannelChanges(currentChannel)">修改</el-button>
        </div>
    </el-dialog>
    <el-dialog v-model="addDialogVisible" title="新增频道">
        <el-form :model="newChannel" ref="addFormRef" label-width="80px">
            <el-form-item label="频道名称">
                <el-input v-model="newChannel.channelName" placeholder="请输入频道名称"></el-input>
            </el-form-item>
            <el-form-item label="管理者ID">
                <el-input v-model="newChannel.administratorId" placeholder="请输入频道管理者ID"></el-input>
            </el-form-item>
            <div style="margin-left: 10px;">频道状态：
                <el-switch v-model="newChannelStatus" @change="changeNewStatus(addForm)"
                    style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949;margin-left: 10px;" />
            </div>
        </el-form>
        <template #footer>
            <el-button @click="addDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="addChannel">确定</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getChannelListService, disableChannel, enableChannel, deleteChannelService, updateChannel, createChannelByUserIdService } from '@/api/message';
import { getUserNameByIdService, getUserByIdService } from '@/api/user';
import dayjs from 'dayjs';

const channelList = ref([]);
const getchannelList = async () => {
    let result = await getChannelListService();
    // console.log(result.data);
    for (let i = 0; i < result.data.length; i++) {
        let user = await getUserNameByIdService(result.data[i].administratorId);
        result.data[i].name = user.data;
        if (result.data[i].status === "已启用") {
            result.data[i].channalStatus = true;
        } else {
            result.data[i].channalStatus = false;
        }
    }
    // console.log(result.data)
    channelList.value = result.data;
};

const changeStatus = async (channel) => {
    try {
        if (!channel.channalStatus) {
            await disableChannel(channel.id);
            ElMessage.success('已禁用');
        } else {
            await enableChannel(channel.id);
            ElMessage.success('已启用');
        }
        getchannelList();



    } catch (error) {
        ElMessage.error('操作失败');
    }
};

const deleteChannel = async (id) => {
    try {
        await deleteChannelService(id);
        ElMessage.success('删除成功');
        getchannelList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getchannelList();

// 打开管理对话框
const editDialogVisible = ref(false);
const currentChannel = ref({});
const openManageDialog = (channel) => {
    editDialogVisible.value = true;
    currentChannel.value = { ...channel };
}
//修改频道信息
const saveChannelChanges = async (channal) => {
    await updateChannel(channal);
    ElMessage({
        message: '修改成功',
        type: 'success',
    })
    getchannelList();
    editDialogVisible.value = false;
}
//新增频道
const addDialogVisible = ref(false);
const newChannel = ref({});
const newChannelStatus = ref(true);
const openAddDialog = () => {
    addDialogVisible.value = true;
    newChannelStatus.value = true; // 重置状态
    newChannel.value.status = "已启用"; // 重置状态
}
const changeNewStatus = () => {
    newChannelStatus.value = newChannelStatus.value ? true : false;
    if (newChannelStatus.value) {
        newChannel.value.status = "已启用";
    } else {
        newChannel.value.status = "已禁用";
    }
}
const addChannel = async () => {
    if (!newChannel.value.channelName || !newChannel.value.administratorId) {
        ElMessage.error('请填写完整的频道信息');
        return;
    }
    const check = ref(true)
    await getUserByIdService(newChannel.value.administratorId).then((res) => {
        if (res.data === null) {
            check.value = false;
            ElMessage.error('该用户不存在');
            return;
        }
    })
    if (check.value === true) {
        try {
            await createChannelByUserIdService(newChannel.value);
            ElMessage.success('新增频道成功');
            addDialogVisible.value = false;
            getchannelList();
        }
        catch (error) {
            ElMessage.error('新增频道失败');
        }
    }
}
</script>

<style scoped>
.comment-content {
    word-wrap: break-word;
    word-break: break-all;
}

.eldialog {
    display: flex;
}
</style>