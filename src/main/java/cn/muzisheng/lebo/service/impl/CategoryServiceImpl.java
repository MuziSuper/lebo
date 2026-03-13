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
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Log4j2
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 商品服务
     */
    private final ProductService productService;

    /**
     * 构造函数注入商品服务
     */
    public CategoryServiceImpl(ProductService productService) {
        this.productService = productService;
    }
    /**
     * 创建商品类目
     * @param categoryName 商品类目名称
     * @return 是否创建成功
     */
    @Transactional(rollbackOn = CategoryException.class)
    @Override
    public ResponseEntity<Result<Boolean>> create(String categoryName) {
        // 参数校验
        // 参数校验：分类名称不能为空
        if (!StringUtils.hasText(categoryName)) {
            log.error("创建分类失败，分类名称不能为空，categoryName: {}", categoryName);
            throw new CategoryException("分类名称不能为空");
        }

        // 创建新分类
        // 创建新分类对象
        Category category = new Category();
        // 去除前后空格
        String trimCategoryName = categoryName.trim();
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, trimCategoryName);
        Category existingCategory = this.getOne(queryWrapper);
        if (existingCategory != null) {
            log.error("创建分类失败，分类名称已存在，categoryName: {}", categoryName);
            throw new CategoryException("分类名称已存在：" + categoryName);
        }

        category.setName(trimCategoryName);
        // 保存分类到数据库
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
    @Transactional(rollbackOn = CategoryException.class)
    public ResponseEntity<Result<Boolean>> update(Category category) {
        // 参数校验
        // 参数校验：分类对象和ID不能为空
        if (category == null || category.getId() == null) {
            log.error("更新分类失败，分类 ID 不能为空");
            throw new CategoryException("更新分类失败，分类 ID 不能为空");
        }

        if (!StringUtils.hasText(category.getName())) {
            log.error("更新分类失败，分类名称不能为空，categoryId: {}", category.getId());
            throw new CategoryException("更新分类失败，分类名称不能为空");
        }

        // 检查分类是否存在
        Category existingCategory = this.getById(category.getId());
        if (existingCategory == null) {
            log.error("更新分类失败，分类不存在，categoryId: {}", category.getId());
            throw new CategoryException("更新分类失败，分类不存在：" + category.getId());
        }

        // 检查新名称是否与其他分类重复（排除自己）
        // 创建查询条件：按分类名称升序排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 名称相等
        queryWrapper.eq(Category::getName, category.getName().trim())
                .ne(Category::getId, category.getId());
        Category duplicateCategory = this.getOne(queryWrapper);

        // 如果存在重复名称，抛出异常
        if (duplicateCategory != null) {
            log.error("更新分类失败，分类名称已存在，categoryId: {}, newName: {}",
                    category.getId(), category.getName());
            throw new CategoryException("更新分类失败，分类名称已存在：" + category.getName());
        }

        // 更新分类
        // 更新分类名称
        existingCategory.setName(category.getName().trim());
        boolean success = this.updateById(existingCategory);

        // 删除失败处理
        if (!success) {
            log.error("更新分类失败，categoryId: {}", category.getId());
            throw new CategoryException("更新分类失败, categoryName: " + category.getName());
        }

        log.info("更新分类成功，categoryId: {}, newName: {}", category.getId(), category.getName());
        Response<Boolean> result = new Response<>();
        result.setData(true);
        return result.value();
    }

    @Transactional(rollbackOn = CategoryException.class)
    @Override
    public ResponseEntity<Result<Boolean>> delete(Long categoryId) {
        // 参数校验
        // 参数校验：分类ID不能为空
        if (categoryId == null) {
            log.error("删除分类失败，分类 ID 不能为空");
            throw new CategoryException("删除分类失败，分类 ID 不能为空");
        }

        // 检查分类是否存在
        Category existingCategory = this.getById(categoryId);
        if (existingCategory == null) {
            log.error("删除分类失败，分类不存在，categoryId: {}", categoryId);
            throw new CategoryException("删除分类失败，分类不存在：" + categoryId);
        }

        // 检查分类下是否有商品（业务规则：有商品不能删除）
        if (productService.existByCategoryId(categoryId)) {
            log.error("删除分类失败，分类下存在商品，无法删除，categoryId: {}", categoryId);
            throw new CategoryException("删除分类失败，分类下存在商品，无法删除：" + categoryId);
        }

        // 删除分类
        // 执行删除操作
        boolean success = this.removeById(categoryId);

        // 删除失败处理
        if (!success) {
            log.error("删除分类失败，categoryId: {}", categoryId);
            throw new CategoryException("删除分类失败");
        }

        log.warn("删除分类成功，categoryId: {}", categoryId);
        Response<Boolean> result = new Response<>();
        result.setData(true);
        return result.value();
    }

    @Override
    public ResponseEntity<Result<List<Category>>> categoryList() {
        // 创建查询条件：按分类名称升序排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getName);

        // 查询所有分类
        List<Category> categories = this.list(queryWrapper);

        log.info("获取分类列表成功，size: {}", categories.size());
        Response<List<Category>> result = new Response<>();
        result.setData(categories);
        return result.value();
    }
}