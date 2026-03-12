package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.Category;
import org.springframework.http.ResponseEntity;
import cn.muzisheng.lebo.model.Result;

import java.util.List;

public interface CategoryService {
    /**
     * 创建商品类目
     * @param categoryName 商品类目名称
     * @return 是否创建成功
     */
    ResponseEntity<Result<Boolean>> create(String categoryName);

    /**
     * 更新商品类目
     * @param category 商品类目（包含 id 和 name）
     * @return 是否更新成功
     */
    ResponseEntity<Result<Boolean>> update(Category category);

    /**
     * 删除商品类目
     * @param categoryId 商品类目 ID
     * @return 是否删除成功
     */
    ResponseEntity<Result<Boolean>> delete(Long categoryId);

    /**
     * 获取商品类目列表
     * @return 商品类目列表
     */
    ResponseEntity<Result<List<Category>>> categoryList();
}
