<template>
    <el-card>
        <template #header>
            <span>文章分类管理</span>
            <el-button type="success" style="margin-left: 20px;" @click="openAddDialog()">新增分类</el-button>
        </template>
        <el-table :data="categoryList" stripe>
            <el-table-column prop="id" label="ID" />
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="description" label="分类描述" />
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" @click="openEditDialog(scope.row)">修改</el-button>
                    <el-button type="danger" @click="deleteCategory(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

    </el-card>
    <!-- 新增分类对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增分类">
        <el-form :model="addForm" ref="addFormRef" label-width="80px">
            <el-form-item label="分类名称">
                <el-input v-model="addForm.name" placeholder="请输入分类名称"></el-input>
            </el-form-item>
            <el-form-item label="分类描述">
                <el-input v-model="addForm.description" placeholder="请输入分类描述"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="addDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="addCategory">确定</el-button>
        </template>
    </el-dialog>
    <!-- 编辑分类对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑分类">
        <el-form :model="editForm" ref="editFormRef" label-width="80px">
            <el-form-item label="分类名称">
                <el-input v-model="editForm.name" placeholder="请输入分类名称"></el-input>
            </el-form-item>
            <el-form-item label="分类描述">
                <el-input v-model="editForm.description" placeholder="请输入分类描述"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="editDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="editCategory">确定</el-button>
        </template>
    </el-dialog>

</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getCategoriesInfoService, deleteCategoryService, addCategoryService, editCategoryService } from '@/api/category';
import 'element-plus/dist/index.css'
const categoryList = ref([]);
const addDialogVisible = ref(false);
const editDialogVisible = ref(false);
const addForm = ref({ name: '', description: '' });
const editForm = ref({ id: null, name: '', description: '' });
const addFormRef = ref(null);
const editFormRef = ref(null);

const getcategoryList = async () => {
    let result = await getCategoriesInfoService();
    categoryList.value = result.data;
};

const openAddDialog = () => {
    addDialogVisible.value = true;
    addForm.value = { name: '', description: '' };
};

const openEditDialog = (row) => {
    editDialogVisible.value = true;
    editForm.value = { ...row };
};

const addCategory = async () => {
    try {
        await addCategoryService(addForm.value);
        ElMessage.success('新增分类成功');
        addDialogVisible.value = false;
        getcategoryList();
    } catch (error) {
        ElMessage.error('新增分类失败');
    }
};

const editCategory = async () => {
    try {
        await editCategoryService(editForm.value);
        ElMessage.success('编辑分类成功');
        editDialogVisible.value = false;
        getcategoryList();
    } catch (error) {
        ElMessage.error('编辑分类失败');
    }
};

const deleteCategory = async (id) => {
    try {
        await deleteCategoryService(id);
        ElMessage.success('删除分类成功');
        getcategoryList();
    } catch (error) {
        ElMessage.error('删除分类失败');
    }
};

getcategoryList();
</script>

<style scoped></style>