<template>
    <el-card>
        <template #header>
            <span>管理员管理</span>
            <el-button type="success" style="margin-left: 20px;" @click="openAddDialog()">添加管理员</el-button>
        </template>
        <el-table :data="adminList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="userId" label="用户ID" />
            <el-table-column prop="type" label="管理员类型" />
            <el-table-column prop="createTime" label="创建时间">
                <template #default="scope">
                    {{ formatFollowTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" @click="edit(scope.row)">修改</el-button>
                    <el-button type="danger" @click="deleteMessage(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-card>
    <!-- 修改用户模态框 -->
    <el-dialog v-model="showEditUserModal" title="修改管理员类型" width="30%">
        <el-form :model="editForm" :rules="rules" label-width="100px">
            <el-form-item label="管理员类型" prop="type">
                <el-select v-model="editForm.type" placeholder="请选择管理员类型" clearable>
                    <el-option v-for="item in adminTypes" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showEditUserModal = false">取消</el-button>
            <el-button type="primary" @click="handleEditUser">保存</el-button>
        </template>
    </el-dialog>
    <!-- 新增管理员模态框 -->
    <el-dialog v-model="showAddUserModal" title="新增管理员" width="30%">
        <el-form :model="addForm" :rules="rules" label-width="100px">
            <el-form-item label="用户ID" prop="userId">
                <el-input v-model="addForm.userId" placeholder="请输入用户ID" />
            </el-form-item>
            <el-form-item label="管理员类型" prop="type">
                <el-select v-model="addForm.type" placeholder="请选择管理员类型" clearable>
                    <el-option v-for="item in adminTypes" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="showAddUserModal = false">取消</el-button>
            <el-button type="primary" @click="handleAddUser">保存</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { getAdminListService, updateAdminService, deleteAdminService, addAdminService } from '@/api/user';
import dayjs from 'dayjs';

const adminList = ref([]);

const getadminList = async () => {
    let result = await getAdminListService();
    adminList.value = result.data;
};

const deleteMessage = async (id) => {
    try {
        await deleteAdminService(id);
        ElMessage.success('删除成功');
        getadminList();
    } catch (error) {
        ElMessage.error('删除失败');
    }
};

const formatFollowTime = (time) => {
    return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

getadminList();
const showEditUserModal = ref(false);
const showAddUserModal = ref(false);

// 编辑表单数据
const editForm = reactive({
    id: null,
    type: null,
    userId: null
});

// 新增表单数据
const addForm = reactive({
    userId: null,
    type: null
});

// 管理员类型选项
const adminTypes = ref([
    { value: '超级管理员', label: '超级管理员' },
    { value: '普通管理员', label: '普通管理员' }
]);

// 表单验证规则
const rules = reactive({
    type: [
        { required: true, message: '请选择管理员类型', trigger: 'change' }
    ],
    userId: [
        { required: true, message: '请输入用户ID', trigger: 'blur' }
    ]
});
// 修改用户信息
const edit = (row) => {
    editForm.id = row.id;
    editForm.type = row.type;
    editForm.userId = row.userId;
    showEditUserModal.value = true;
};
// 保存修改
const handleEditUser = async () => {
    const result = await updateAdminService(editForm);
    showEditUserModal.value = false;
    ElMessage.success('修改成功');
    getadminList();
};

// 打开新增管理员对话框
const openAddDialog = () => {
    showAddUserModal.value = true;
};

// 保存新增管理员
const handleAddUser = async () => {
    try {
        await addAdminService(addForm);
        ElMessage.success('新增管理员成功');
        showAddUserModal.value = false;
        getadminList();
    } catch (error) {
        ElMessage.error('新增管理员失败');
    }
};
</script>

<style scoped></style>