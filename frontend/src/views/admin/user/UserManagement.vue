<template>
    <div class="usermanagementContainer flex col z-card">
        <!-- 搜索框 -->
        <div class="search-container">
            <el-input v-model="searchQuery" placeholder="通过用户ID或用户名搜索" style="width: 300px; margin-right: 10px;" />
            <el-button type="primary" @click="searchUsers">搜索</el-button>
            <el-button type="success" @click="showAddUserModal = true">添加用户</el-button>
        </div>
        <!-- 用户列表 -->
        <el-table :data="displayedUsers" border stripe style="width: 100%; margin-top: 20px;">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column label="头像" width="100">
                <template #default="{ row }">
                    <el-image :src="row.avatarUrl" style="width: 50px; height: 50px;" />
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="updateTime" label="更新时间" />
            <el-table-column label="操作" width="150">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="editUser(row)">修改</el-button>
                    <el-button type="danger" size="small" @click="deleteUser(row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pagination-container">
            <el-pagination v-model:current-page="pagination.currentPage" :page-size="pagination.pageSize"
                :total="pagination.total" layout="prev, pager, next, jumper, total" @current-change="handlePageChange"
                prev-text="上一页" next-text="下一页">
                <template #total>
                    总共 {{ pagination.total }} 条
                </template>
            </el-pagination>
        </div>
        <!-- 添加用户模态框 -->
        <el-dialog v-model="showAddUserModal" title="添加用户" width="30%" @close="clearRegisterData">
            <el-form :model="newUser" label-width="80px">
                <el-form-item label="用户名">
                    <el-input v-model="newUser.username" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="newUser.password" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showAddUserModal = false">取消</el-button>
                <el-button type="primary" @click="handleAddUser">提交</el-button>
            </template>
        </el-dialog>

        <!-- 修改用户模态框 -->
        <el-dialog v-model="showEditUserModal" title="修改用户信息" width="30%">
            <el-form :model="editingUser" label-width="80px">
                <el-form-item label="用户名">
                    <el-input v-model="editingUser.username" />
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="editingUser.nickname" />
                </el-form-item>
                <el-form-item label="密码">
                    <el-input v-model="editingUser.password" />
                </el-form-item>
                <el-form-item label="邮箱">
                    <el-input v-model="editingUser.email" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showEditUserModal = false">取消</el-button>
                <el-button type="primary" @click="handleEditUser">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getUsersService, addUserService, userInfoUpdateService, deleteUserService } from '@/api/user';

// 定义响应式变量
const searchQuery = ref('');
const showAddUserModal = ref(false);
const newUser = ref({
    username: '',
    password: '',
    money: '',
});
const showEditUserModal = ref(false);
const editingUser = ref({
    id: '',
    username: '',
    nickname: '',
    password: '',
    email: '',
    money: '',
});

// 所有用户信息
const allUsers = ref([]);

// 搜索用户
const searchUsers = () => {
    pagination.value.currentPage = 1; // 搜索时重置到第一页
};

// 添加用户信息
const handleAddUser = async () => {
    try {
        const result = await addUserService(newUser.value);
        ElMessage.success(result.message ? result.message : '添加成功')
        showAddUserModal.value = false;
        // 添加成功后重新获取用户列表数据
        await fetchUsers();
    } catch (error) {
        console.error('添加用户失败:', error);
        alert('添加用户失败，请检查输入数据');
    }
};

// 清空数据模型
const clearRegisterData = () => {
    newUser.value = {
        username: '',
        password: '',
        money: 0
    }
};

// 修改用户信息
const editUser = (user) => {
    // 排除 password 字段
    const { password, ...userWithoutPassword } = user;
    editingUser.value = { ...userWithoutPassword };
    showEditUserModal.value = true;
};

const handleEditUser = async () => {
    const result = await userInfoUpdateService(editingUser.value);
    ElMessage.success(result.message ? result.message : '修改成功')
    showEditUserModal.value = false;
    // 修改成功后重新获取用户列表数据
    await fetchUsers();
};

// 删除用户信息
const deleteUser = async (id) => {
    try {
        await deleteUserService(id);
        // 删除成功后重新获取用户列表数据
        await fetchUsers();
    } catch (error) {
        console.error('删除用户信息失败:', error);
    }
};

// 添加分页状态
const pagination = ref({
    currentPage: 1,
    pageSize: 8,
    total: 0
});

// 修改显示用户数据计算属性
const displayedUsers = computed(() => {
    const filtered = Array.isArray(allUsers.value) ? allUsers.value.filter(user =>
        user.id.toString().includes(searchQuery.value) ||
        user.username.includes(searchQuery.value)
    ) : [];

    // 更新总条数
    pagination.value.total = filtered.length;

    // 分页处理
    const start = (pagination.value.currentPage - 1) * pagination.value.pageSize;
    const end = start + pagination.value.pageSize;
    return filtered.slice(start, end);
});

// 添加分页事件处理
const handlePageChange = (page) => {
    pagination.value.currentPage = page;
};

// 获取用户信息
const fetchUsers = async () => {
    try {
        const result = await getUsersService();
        if (Array.isArray(result.data)) {
            allUsers.value = result.data;
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
    }
};

onMounted(async () => {
    await fetchUsers();
});
</script>


<style scoped>
.usermanagementContainer {
    padding: 20px;
}

.search-container {
    margin-bottom: 20px;
}

.el-table {
    margin-top: 20px;
}

.el-image {
    border-radius: 4px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

/* 分页组件样式优化 */
.el-pagination {
    font-size: 14px;
    color: #333;
}

.el-pagination .btn-prev,
.el-pagination .btn-next,
.el-pagination .el-pager li {
    border: 1px solid #ccc;
    background-color: #fff;
    margin: 0 3px;
    border-radius: 4px;
    transition: background-color 0.3s ease;
}

.el-pagination .btn-prev:hover,
.el-pagination .btn-next:hover,
.el-pagination .el-pager li:hover {
    background-color: #f0f0f0;
}

.el-pagination .el-pager li.active {
    background-color: #409eff;
    color: #fff;
    border-color: #409eff;
}

.el-pagination__total {
    margin-left: 10px;
}
</style>