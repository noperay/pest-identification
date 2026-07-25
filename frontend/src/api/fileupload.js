import { apiInstance } from '@/utils/apirequest.js';
// 修改头像
export const userAvatarUpdateService = async (avatarUrl) => {
    const params = toURLSearchParams({ avatarUrl });
    try {
        return await apiInstance.patch('/file/upload', params);
    } catch (error) {
        console.error('图片上传失败:', error);
        throw error;
    }
};