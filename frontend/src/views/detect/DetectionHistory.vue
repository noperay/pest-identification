<template>
  <div class="detectContainer z-card flex col">
    <div class="header">
      <span><strong>识别记录</strong></span>
    </div>
    <div class="detection-content flex">
      <div class="table-container">
        <el-table :data="displayedData" border height="calc(100% - 40px)">
          <!-- <el-table-column prop="createTime" label="检测日期" /> -->
          <el-table-column prop="createTime" label="识别日期">
            <template #default="{ row }">
              {{ formatFollowTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="识别结果图" width="">
            <template #default="{ row }">
              <img :src="row.imgUrl" alt="识别结果" v-if="row.imgUrl" :width="120" :height="120" />
            </template>
          </el-table-column>
          <el-table-column label="害虫种类">
            <template #default="{ row }">
              <a :href="getBaiduLink(row.sort)" target="_blank">{{ row.sort }}</a>
            </template>
          </el-table-column>
          <el-table-column prop="confidenceLevel" label="置信度" width="" />
          <el-table-column label="操作" width="">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <div class="empty-state">
              <span v-if="error">数据加载失败，请稍后重试</span>
              <span v-else>暂无数据</span>
            </div>
          </template>
        </el-table>
      </div>
    </div>
    <!-- 分页组件，固定在底部 -->
    <div class="pagination-container flex">
      <el-pagination v-model:current-page="pagination.currentPage" :page-size="pagination.pageSize"
        :total="pagination.total" layout="prev, pager, next, jumper, 总共 {{ pagination.total }} 条"
        @current-change="handlePageChange" @size-change="handleSizeChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { getUserDetectService, deleteDetectRecordService } from "@/api/detection.js";
import { ElMessage } from 'element-plus';
import dayjs from 'dayjs'; // 新增导入 
// 存储所有检测记录
const userDetectList = ref([]);
// 加载状态
const loading = ref(true);
// 错误信息
const error = ref(null);

// 分页配置
const pagination = ref({
  currentPage: 1, // 当前页码
  pageSize: 5,    // 每页显示的记录数
  total: 0        // 总记录数
});

// 获取检测记录的异步函数
const getDetectHistory = async () => {
  try {
    loading.value = true;
    error.value = null;
    // 调用接口获取检测记录
    let result = await getUserDetectService();
    if (Array.isArray(result.data)) {
      // 处理返回的数据
      userDetectList.value = result.data.map(item => ({
        sort: item.sort,
        imgUrl: item.imgUrl,
        confidenceLevel: item.confidenceLevel,
        createTime: item.createTime,
        id: item.id // 假设数据中有 id 字段用于删除操作
      }));
    }
    // 更新总记录数
    pagination.value.total = userDetectList.value.length;
  } catch (err) {
    // 捕获错误并记录
    error.value = err;
    console.error('获取检测记录失败:', err);
  } finally {
    // 无论成功与否，结束加载状态
    loading.value = false;
  }
};

// 计算当前页要显示的数据
const displayedData = computed(() => {
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize;
  const end = start + pagination.value.pageSize;
  return userDetectList.value.slice(start, end);
});

// 页码变化时的处理函数
const handlePageChange = (page) => {
  pagination.value.currentPage = page;
};

// 每页显示数量变化时的处理函数
const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.currentPage = 1;
};

// 删除记录的异步函数
const handleDelete = async (row) => {
  try {
    // 调用接口删除记录
    await deleteDetectRecordService(row.id);
    // 从本地数据中移除该记录
    const index = userDetectList.value.findIndex(item => item.id === row.id);
    if (index > -1) {
      userDetectList.value.splice(index, 1);
    }
    // 更新总记录数
    pagination.value.total = userDetectList.value.length;
    // 处理页码超出范围的情况
    const totalPages = Math.ceil(pagination.value.total / pagination.value.pageSize);
    if (pagination.value.currentPage > totalPages && totalPages > 0) {
      pagination.value.currentPage = totalPages;
    }
    // 提示删除成功
    ElMessage.success('删除成功');
  } catch (error) {
    // 捕获错误并提示
    console.error('删除记录失败:', error);
    ElMessage.error('删除记录失败，请稍后重试');
  }
};

// 页面加载时获取检测记录
getDetectHistory();

// 生成百度百科链接的函数
function getBaiduLink(insectType) {
  return `https://baike.baidu.com/item/${encodeURIComponent(insectType)}`;
}
const formatFollowTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};

</script>

<style scoped>
.detectContainer {
  /* padding: 10px; */
  height: 100%;
  width: 100%;
}

.header {
  /* border: 1px solid red; */
  margin-top: 10px;
  margin-left: 10px;
  padding: 10px;
  font-weight: bold;
  z-index: 1;
}

.detection-content {
  /* border: 1px solid red; */
  margin-left: 20px;
  margin-right: 20px;
  overflow-y: auto;
  /* overflow: hidden; */
}

.table-container {
  height: 100%;
  width: 100%;
  overflow-y: auto;
}

.el-table {
  min-height: 100%;
}

.el-table-column {
  width: auto;
  /* 或者设置具体的像素值 */
}

.empty-state {
  text-align: center;
  padding: 20px;
  color: #888;
}

.pagination-container {
  margin-top: 10px;
  margin-bottom: 10px;
  margin-right: 20px;
  justify-content: flex-end;
  background-color: white;
  z-index: 1;
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