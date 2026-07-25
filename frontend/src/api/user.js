// 导入 request.js 请求工具
import { apiInstance, flaskInstance } from '@/utils/apirequest.js';
// 将对象转换为 URLSearchParams 的公共函数
const toURLSearchParams = (data) => {
    const params = new URLSearchParams();
    for (let key in data) {
        params.append(key, data[key]);
    }
    return params;
};

// 提供调用注册接口的函数
export const userRegisterService = (registerData) => {
    const params = toURLSearchParams(registerData);
    return apiInstance.post('/user/register', params);
};

// 提供调用登录接口的函数
export const userLoginService = (loginData) => {
    const params = toURLSearchParams(loginData);
    return apiInstance.post('/user/login', params);
};
// 根据id获取用户信息
export const getUserByIdService = async (userId) => {
    try {
        return await apiInstance.get(`/user/findUserById/${userId}`);
    } catch (error) {
        console.error('获取用户信息失败:', error);
        throw error;
    }
};
//根据用户名获取用户信息
export const getUserByUsernameService = async (username) => {
    try {
        return await apiInstance.get('/user/findUserByUsername', { params: { username } });
    } catch (error) {
        console.error('获取用户信息失败:', error);
        throw error;
    }
};
// 获取所有用户信息
export const getUsersService = async () => {
    try {
        return await apiInstance.get('/user/getUsers');
    } catch (error) {
        console.error('获取用户列表失败:', error);
        throw error;
    }
};

// 获取用户详细信息
export const userInfoService = async () => {
    try {
        return await apiInstance.get('/user/userInfo');
    } catch (error) {
        console.error('获取用户详细信息失败:', error);
        throw error;
    }
};

// 修改个人信息
export const userInfoUpdateService = async (userInfoData) => {
    try {
        return await apiInstance.put('/user/updateUser', userInfoData);
    } catch (error) {
        console.error('修改个人信息失败:', error);
        throw error;
    }
};

// 修改头像
export const userAvatarUpdateService = async (avatarUrl) => {
    const params = toURLSearchParams({ avatarUrl });
    try {
        return await apiInstance.patch('/user/updateAvatar', params);
    } catch (error) {
        console.error('修改头像失败:', error);
        throw error;
    }
};

// 修改密码
export const passwordUpdateService = async (userInfoData) => {
    try {
        return await apiInstance.patch('/user/updatePwd', userInfoData);
    } catch (error) {
        console.error('修改密码失败:', error);
        throw error;
    }
};

// 增加用户
export const addUserService = async (userData) => {
    const params = toURLSearchParams(userData);
    return apiInstance.post('/user/addUser', params);
};
// 删除用户
export const deleteUserService = async (userId) => {
    try {
        return await apiInstance.delete(`/user/deleteUser/${userId}`);
    } catch (error) {
        console.error('删除用户失败:', error);
        throw error;
    }
};
// 获取所有关注列表
export const getFollowListService = async () => {
    try {
        return await apiInstance.get('/user/getFollows');
    } catch (error) {
        console.error('获取关注列表失败:', error);
        throw error;
    }
};
// 获取我的关注列表
export const getMyFollowListService = async (userId) => {
    return await apiInstance.get(`/user/getMyFollows/${userId}`);
};
//获取创建的频道
export const getCreateChannelService = async (userId) => {
    try {
        return await apiInstance.get(`/user/getChannelByUserId/${userId}`);
    } catch (error) {
        console.error('获取创建频道列表失败:', error);
        throw error;
    }
};
//关注用户
export const followUserService = async (followee) => {
    const formData = new FormData();
    formData.append('followee', followee);
    return apiInstance.post('/user/followUser', formData);
}
//取消关注
export const cancelFollowUserService = async (followee) => {
    const formData = new FormData();
    formData.append('followee', followee);
    return apiInstance.post('/user/cancelFollow', formData);
}
//获取发布文章数量
export const getArticleCountsService = async (userId) => {
    const response = await apiInstance.get(`/user/getArticleCounts/${userId}`);
    return response.data;
}
//增加访客
export const addUserVisitService = async (userId) => {
    await apiInstance.get(`/user/addVisitorCount/${userId}`);
}
//根据用户id获取用户名
export const getUserNameByIdService = async (userId) => {
    const response = await apiInstance.get(`/user/getUserNameById/${userId}`);
    return response;
}
//根据表id删除关注表信息
export const deleteFollowService = async (followId) => {
    const response = await apiInstance.delete(`/user/deleteFollow/${followId}`);
    return response;
}
//获取所有用户私聊记录
export const getUserChatListService = async () => {
    const response = await apiInstance.get(`/chat/getAllChatMessage`);
    return response;
}
//获取管理员列表
export const getAdminListService = async () => {
    const response = await apiInstance.get(`/user/findAdmin`);
    return response;
}
//修改管理员
export const updateAdminService = async (data) => {
    const response = await apiInstance.put('/user/updateAdmin', data);
    return response;
}
//删除管理员
export const deleteAdminService = async (id) => {
    const response = await apiInstance.delete(`/user/deleteAdmin/${id}`);
    return response;
}
//增加管理员
export const addAdminService = async (data) => {
    const response = await apiInstance.post('/user/addAdmin', data);
    return response;
}
//根据userID查询管理员类型
export const getAdminTypeService = async (userId) => {
    const response = await apiInstance.get(`/user/getAdminType/${userId}`);
    return response;
}
export const deleteMessageService = async (messageId) => {
    const response = await apiInstance.delete(`/chat/deleteUsersMessage`, {
        params: {
            chatId: messageId
        }
    });
    return response;
}