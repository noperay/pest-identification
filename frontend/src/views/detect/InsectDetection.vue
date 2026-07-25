<template>
  <div class="flex col">
    <el-card class="detectContainer z-card">
      <template #header>
        <div class="header">
          <span><strong>害虫识别</strong></span>
        </div>
      </template>
      <el-form>
        <el-form-item>
          <el-upload ref="uploadRef" :auto-upload="false" action="/api/file/upload" multiple :show-file-list="true"
            name="file" list-type="picture-card" :headers="{ 'Authorization': tokenStore.token }"
            :on-preview="handlePictureCardPreview" :on-remove="handleRemove" :on-success="uploadSuccess">
            <el-icon @click="">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-button type="primary" @click="submitUpload">上传</el-button>
        <el-button type="primary" @click="startDetect">开始识别</el-button>
      </el-form>
    </el-card>
    <el-card class="resultContainer z-card">
      <template #header>
        <span><strong>识别结果</strong></span>
      </template>
      <div v-if="userDetectList.length > 0" class="detect_box">
        <div class="detect_result" v-for="item in paginatedData" :key="item.id">
          <div class="imgbox" @click="handlePreview(item.imgUrl)">
            <img class="detect-image" :src="item.imgUrl"
              style="object-fit: contain; max-width: 100%; max-height: 100%;" />
          </div>
          <div class="info-box">
            <p>类别: {{ item.sort }}</p>
            <p>置信度: {{ (Number(item.confidenceLevel) || 0).toFixed(3) }}</p>
            <p>识别时间: {{ formatFollowTime(item.createTime) }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="当前没有检测结果" />
      <el-pagination v-if="userDetectList.length > 0" :total="userDetectList.length" :page-size="pageSize"
        v-model:current-page="currentPage" layout="prev, pager, next" background class="pagination" />
    </el-card>
  </div>
  <el-dialog v-model="dialogVisible" width="700">
    <img class="responsive-image" :src="dialogImageUrl" alt="Preview Image"
      style="width: 100%; height: auto; max-height: 80vh; object-fit: contain;" />
  </el-dialog>
</template>
<script setup>
import { ref, computed } from "vue";
import { Plus } from '@element-plus/icons-vue';
import { ElMessage, ElLoading } from 'element-plus'
import { startDetectService, insertDetectImgUrlService, getUserDetectService } from '@/api/detection.js'
import { useTokenStore } from '@/stores/token.js'
import { userInfoService } from '@/api/user.js'
import dayjs from 'dayjs';

const tokenStore = useTokenStore();
const dialogImageUrl = ref('');
const dialogVisible = ref(false);
const uploadRef = ref();
//原图片名
const fileNameList = ref([]);
//检测前图像列表
const urlList = ref([]);
//检测后图像列表
const detectImgUrlList = ref([]);
const userinfo = ref({});

const getUserInfo = async () => {
  //调用接口
  let result = await userInfoService();
  userinfo.value = result.data;
}
getUserInfo();

// 上传方法
const submitUpload = () => {
  //清空list
  urlList.value = []
  detectImgUrlList.value = []
  uploadRef.value.submit();
};

//图片上传成功的回调函数
const uploadSuccess = (response, file) => {
  fileNameList.value.push(file.name);
  urlList.value.push(response.data);
  // console.log(urlList.value)
};

//显示dialog
const handlePictureCardPreview = (file) => {
  dialogVisible.value = true;
  // 如果文件有 URL，则直接使用，否则创建一个临时 URL
  dialogImageUrl.value = file.url || URL.createObjectURL(file.raw);
};

//移除
const handleRemove = (file) => {
  // 使用 findIndex 查找与 file.name 匹配的项的索引
  const index = fileNameList.value.findIndex(name => name === file.name);
  // 如果找到匹配项，删除该项
  if (index !== -1) {
    fileNameList.value.splice(index, 1);
  }
};

//开始检测
const startDetect = async () => {
  if (!fileNameList.value || fileNameList.value.length === 0) {
    ElMessage.error("请上传图片");
  } else {
    const loading = ElLoading.service({
      lock: true,
      text: '正在检测中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    try {
      let result = await startDetectService(urlList);
      result.forEach(item => {
        detectImgUrlList.value.push(item.data.imgUrl)
      });
      insertDetectInfo(result);
    } catch (error) {
      ElMessage.error('检测失败，请稍后重试');
    } finally {
      loading.close();
    }
  }
};

//储存检测结果
const insertDetectInfo = async (detectResult) => {
  let result = await insertDetectImgUrlService(detectResult)
  ElMessage.success(result);
  getDetectHistory();
}

const userDetectList = ref([]);
const getDetectHistory = async () => {
  let result = await getUserDetectService();
  if (Array.isArray(result.data)) {
    userDetectList.value = result.data.map(item => ({
      id: item.imgId,
      uid: item.userId,
      sort: item.sort,
      imgUrl: item.imgUrl,
      confidenceLevel: item.confidenceLevel,
      createTime: item.createTime,
    }));
  }
  // console.log(userDetectList.value);
}
getDetectHistory();

const handlePreview = (imgUrl) => {
  dialogVisible.value = true;
  dialogImageUrl.value = imgUrl;
};

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);

// 分页数据计算
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return userDetectList.value.slice(start, end);
});

const formatFollowTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
};
</script>

<style>
.detectContainer {
  width: 100%;
  height: 100%;
  flex: 1;
}

.pagination {
  margin-top: 20px;
  justify-content: center;
}

.resultContainer {
  margin-top: 20px;
  flex: 1;
}

.detect_box {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.detect_result {
  width: 200px;
  margin-left: 0;
}

.imgbox {
  width: 100%;
  height: 200px;
  cursor: pointer;
  transition: transform 0.2s;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.imgbox:hover {
  transform: scale(1.05);
}

.detect-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.info-box {
  margin-top: 10px;
  padding: 8px;
  border: 1px solid #eee;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.info-box p {
  margin: 4px 0;
  font-size: 14px;
  color: #666;
}
</style>