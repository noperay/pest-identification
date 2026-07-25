package com.zx.insectdetection.controller;

import com.zx.insectdetection.entity.article.Category;
import com.zx.insectdetection.entity.others.Result;
import com.zx.insectdetection.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    // 获取所有文章分类
    @GetMapping("/getCategories")
    public Result<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return Result.success(categories);
    }

    // 添加新的分类
    @PostMapping("/addCategory")
    public Result<Category> addCategory(@RequestBody Category category) {
        try {
            Category savedCategory = categoryRepository.save(category);
            return Result.success(savedCategory);
        } catch (Exception e) {
            return Result.error("添加分类失败");
        }
    }

    // 更新现有分类
//    @PutMapping("/updateCategory")
//    public Result<Category> updateCategory(@PathVariable Long categoryId, @RequestBody Category categoryDetails) {
//        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
//        if (categoryOptional.isPresent())  {
//            Category category = categoryOptional.get();
//            category.setName(categoryDetails.getName());
//            category.setDescription(categoryDetails.getDescription());
//            Category updatedCategory = categoryRepository.save(category);
//            return Result.success(updatedCategory);
//        } else {
//            return Result.error("分类不存在，更新失败");
//        }
//    }
    @PutMapping("/updateCategory")
    public Result<String> updateCategory(@RequestBody Category category) {
        categoryRepository.save(category);
        return Result.success();
    }

    // 删除分类
    @DeleteMapping("/deleteCategory/{categoryId}")
    public Result<String> deleteCategory(@PathVariable Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent())  {
            categoryRepository.deleteById(categoryId);
            return Result.success("分类删除成功");
        } else {
            return Result.error("分类不存在，删除失败");
        }
    }

    //根据分类id查询分类
    @GetMapping("/getCategory/{categoryId}")
    public Result<Category> getCategory(@PathVariable Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();
        return Result.success(category);
    }
    //根据分类id更新分类
//    @PutMapping("/updateCategoryById/{categoryId}")
//    public Result<Category> updateCategoryById(@PathVariable Long categoryId, @RequestBody Category categoryDetails) {
//
//    }
}