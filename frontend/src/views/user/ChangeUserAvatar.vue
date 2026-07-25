<script setup>
import { Plus, Upload } from '@element-plus/icons-vue'
import { ref } from 'vue'
import avatar from '@/assets/default.png'
const uploadRef = ref()
import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore();

import useUserInfoStore from '@/stores/userInfo.js'
const userInfoStore = useUserInfoStore();

//用户头像地址
const imgUrl = ref(userInfoStore.info.avatarUrl)

//图片上传成功的回调函数
const uploadSuccess = (result) => {
    imgUrl.value = result.data;
}

import { userAvatarUpdateService } from '@/api/user.js'
import { ElMessage } from 'element-plus'
//头像修改
const updateAvatar = async () => {
    //调用接口
    let result = await userAvatarUpdateService(imgUrl.value);

    ElMessage.success(result.message ? result.message : '修改成功')

    //修改pinia中的数据
    userInfoStore.info.avatarUrl = imgUrl.value
}
</script>

<template>
    <el-card class="page-container z-card">
        <template #header>
            <div class="header">
                <span>更换头像</span>
            </div>
        </template>
        <div class="flex">
            <el-upload ref="uploadRef" class="avatar-uploader" :show-file-list="false" :auto-upload="true"
                action="/api/file/upload" name="file" :headers="{ 'Authorization': tokenStore.token }"
                :on-success="uploadSuccess">
                <img :src="imgUrl ? imgUrl : avatar" class="avatar" width="250" />
            </el-upload>
            <div class="flex col">
                <el-button class="choosepic" type="primary" :icon="Plus" size="large"
                    @click="uploadRef.$el.querySelector('input').click()">
                    选择图片
                </el-button>
                <el-button class="uploadpic" type="success" :icon="Upload" size="large" @click="updateAvatar">
                    上传头像
                </el-button>
            </div>
        </div>
    </el-card>
</template>

<style lang="scss" scoped>
.page-container {
    margin-left: 10px;
    height: 100%;
}

.avatar-uploader {
    :deep() {
        .avatar {
            width: 200px;
            height: 200px;
            display: block;
        }

        .el-upload {
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
        }

        .el-upload:hover {
            border-color: var(--el-color-primary);
        }

        .el-icon.avatar-uploader-icon {
            font-size: 28px;
            color: #8c939d;
            width: 278px;
            height: 278px;
            text-align: center;
        }
    }
}

.choosepic {
    margin-top: 20px;
    margin-left: 30px;
    width: 100px;
}

.uploadpic {
    margin-top: 30px;
    margin-left: 30px;
    width: 100px;
}
</style>