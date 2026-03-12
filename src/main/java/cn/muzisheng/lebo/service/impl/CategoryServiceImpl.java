package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.Category;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.CategoryException;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.mapper.CategoryMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.CategoryService;
import cn.muzisheng.lebo.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Log4j2
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    private final ProductService productService;
    public CategoryServiceImpl(ProductService productService) {
        this.productService = productService;
    }
    @Override
    public ResponseEntity<Result<Boolean>> create(String categoryName) {
        // 参数校验
        if (!StringUtils.hasText(categoryName)) {
            log.error("创建分类失败，分类名称不能为空，categoryName: {}", categoryName);
            throw new CategoryException("分类名称不能为空");
        }

        // 创建新分类
        Category category = new Category();
        category.setName(categoryName.trim());
        if (!this.save(category)) {
            log.error("创建分类失败，categoryName: {}", categoryName);
            throw new CategoryException("创建分类失败");
        }
        log.info("创建分类成功，categoryName: {}", categoryName);
        Response<Boolean> response = new Response<>();
        response.setData(true);
        return response.value();
    }

    @Override
    public ResponseEntity<Result<Boolean>> update(Category category) {
        // 参数校验
        if (category == null || category.getId() == null) {
            log.error("更新分类失败，分类 ID 不能为空");
            throw new CategoryException("分类 ID 不能为空");
        }
        
        if (!StringUtils.hasText(category.getName())) {
            log.error("更新分类失败，分类名称不能为空，categoryId: {}", category.getId());
            throw new CategoryException("分类名称不能为空");
        }

        // 检查分类是否存在
        Category existingCategory = this.getById(category.getId());
        if (existingCategory == null) {
            log.error("更新分类失败，分类不存在，categoryId: {}", category.getId());
            throw new CategoryException("分类不存在：" + category.getId());
        }

        // 检查新名称是否与其他分类重复（排除自己）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, category.getName().trim())
                    .ne(Category::getId, category.getId());
        Category duplicateCategory = this.getOne(queryWrapper);
        
        if (duplicateCategory != null) {
            log.error("更新分类失败，分类名称已存在，categoryId: {}, newName: {}", 
                     category.getId(), category.getName());
            throw new CategoryException("分类名称已存在：" + category.getName());
        }

        // 更新分类
        existingCategory.setName(category.getName().trim());
        boolean success = this.updateById(existingCategory);
        
        if (!success) {
            log.error("更新分类失败，categoryId: {}", category.getId());
            throw new CategoryException("更新分类失败, categoryName: " + category.getName());
        }
        
        log.info("更新分类成功，categoryId: {}, newName: {}", category.getId(), category.getName());
        Response<Boolean> result = new Response<>();
        result.setData(true);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<Boolean>> delete(Long categoryId) {
        // 参数校验
        if (categoryId == null) {
            log.error("删除分类失败，分类 ID 不能为空");
            throw new CategoryException("分类 ID 不能为空");
        }

        // 检查分类是否存在
        Category existingCategory = this.getById(categoryId);
        if (existingCategory == null) {
            log.error("删除分类失败，分类不存在，categoryId: {}", categoryId);
            throw new CategoryException("分类不存在：" + categoryId);
        }

        // TODO: 检查分类下是否有商品（需要 ProductMapper 支持）
        // 暂时不实现，后续根据需求添加
        if(productService.existByCategoryId(categoryId)){
            log.error("删除分类失败，分类下存在商品，无法删除，categoryId: {}", categoryId);
            throw new CategoryException("分类下存在商品，无法删除：" + categoryId);
         }

        // 删除分类
        boolean success = this.removeById(categoryId);
        
        if (!success) {
            log.error("删除分类失败，categoryId: {}", categoryId);
            throw new CategoryException("删除分类失败");
        }
        
        log.info("删除分类成功，categoryId: {}", categoryId);
        Response<Boolean> result = new Response<>();
        result.setData(true);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<List<Category>>> categoryList() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getName);
        
        List<Category> categories = this.list(queryWrapper);
        
        log.info("获取分类列表成功，size: {}", categories.size());
        Response<List<Category>> result = new Response<>();
        result.setData(categories);
        return result.value();
    }
}
