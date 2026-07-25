<script setup>
import { ref } from 'vue'
//定义数据模型
const updatePasswordData = ref({
    old_pwd: '',
    new_pwd: '',
    re_pwd: ''
})
//校验密码的函数
const checkPassword = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次确认密码'))
    } else if (value !== updatePasswordData.value.new_pwd) {
        callback(new Error('请确保两次输入的密码一样'))
    } else {
        callback()
    }
}
const rules = {
    old_pwd: [
        { required: true, message: '请输入旧密码', trigger: 'blur' },
        {
            pattern: /^\S{5,16}$/,
            message: '旧密码必须是2-10位的非空字符串',
            trigger: 'blur'
        }
    ],
    new_pwd: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        {
            pattern: /^\S{5,16}$/,
            message: '新密码必须是2-10位的非空字符串',
            trigger: 'blur'
        }
    ],
    re_pwd: [
        { required: true, message: '密码不可为空', trigger: 'blur' },
        { validator: checkPassword, trigger: 'blur' }
    ]
}

//修改密码
import { passwordUpdateService } from '@/api/user.js'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router';
import useUserInfoStore from '@/stores/userInfo.js'
import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const router = useRouter();
const updatePassword = async () => {
    //调用接口
    let result = await passwordUpdateService(updatePasswordData.value);
    ElMessage.success(result.msg ? result.msg : '修改成功');
    //清除token并退出
    //清空pinia中存储的token以及个人信息
    tokenStore.removeToken()
    userInfoStore.removeInfo()
    //跳转到登录页面
    router.push('/login')
}
</script>
<template>
    <el-card class="page-container z-card">
        <template #header>
            <div class="header">
                <span>修改密码</span>
            </div>
        </template>
        <el-row>
            <el-col :span="12">
                <el-form :model="updatePasswordData" :rules="rules" label-width="100px" size="large">
                    <el-form-item label="旧密码" prop="old_pwd">
                        <el-input v-if="userInfoStore.info.id === 10077" v-model="updatePasswordData.old_pwd"
                            disabled></el-input>
                        <el-input v-else v-model="updatePasswordData.old_pwd"></el-input>
                    </el-form-item>
                    <el-form-item label="新密码" prop="new_pwd">
                        <el-input v-if="userInfoStore.info.id === 10077" v-model="updatePasswordData.new_pwd"
                            disabled></el-input>
                        <el-input v-else v-model="updatePasswordData.new_pwd"></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="re_pwd">
                        <el-input v-if="userInfoStore.info.id === 10077" v-model="updatePasswordData.re_pwd"
                            disabled></el-input>
                        <el-input v-else v-model="updatePasswordData.re_pwd"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button v-if="userInfoStore.info.id === 10077" type="primary" @click="updatePassword"
                            disabled>提交修改</el-button>
                        <el-button v-else type="primary" @click="updatePassword">提交修改</el-button>
                        <div v-if="userInfoStore.info.id === 10077" style="margin-left: 10px;">（测试账号禁止修改密码）</div>
                    </el-form-item>
                </el-form>
            </el-col>
        </el-row>
    </el-card>
</template>|
<style scoped>
.page-container {
    margin-top: 10px;
}
</style>