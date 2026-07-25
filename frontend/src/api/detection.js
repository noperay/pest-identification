//导入request.js请求工具
import { apiInstance, flaskInstance } from '@/utils/apirequest.js';
//提供调用上传文件接口的函数
export const startDetectService = (urlList) => {
  const results = urlList.value.map(url => {
    return flaskInstance.post('/detect', JSON.stringify({ url }), {
      headers: {
        'Content-Type': 'application/json',
      },
    });
  });
  return Promise.all(results)
};
//储存检测
export const insertDetectImgUrlService = (result) => {
  const dataToSend = result.map(item => item.data);
  return apiInstance.post('/detect/insertDetectInfo', dataToSend);
};
//获取用户检测记录
export const getDetectHistoryService = () => {
  return apiInstance.get('/detect/history');
}

export const getUserDetectService = () => {
  const url = `/detect/getUserDetectList`;
  return apiInstance.get(url);
};
//删除检测记录
export const deleteDetectRecordService = (id) => {
  const url = `/detect/deleteDetectRecord/${id}`;
  return apiInstance.delete(url);
}
//获取所有检测记录
export const getAllDetectService = () => {
  const url = `/detect/getAllDetectList`;
  return apiInstance.get(url);
}