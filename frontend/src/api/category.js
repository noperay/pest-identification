//导入request.js请求工具
import { apiInstance } from '@/utils/apirequest.js';
//获取分类详细信息
export const getCategoriesInfoService = () => {
    return apiInstance.get('/category/getCategories')
}
//根据id获取分类详细信息
export const getCategoryInfoByIdService = (id) => {
    return apiInstance.get(`/category/getCategory/${id}`)
}
//删除分类
export const deleteCategoryService = (id) => {
    return apiInstance.delete(`/category/deleteCategory/${id}`)
}
//增加分类
export const addCategoryService = (data) => {
    return apiInstance.post('/category/addCategory', data)
}
//修改分类
export const editCategoryService = (data) => {
    return apiInstance.put('/category/updateCategory', data)
}