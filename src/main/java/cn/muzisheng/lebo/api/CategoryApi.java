package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.entity.Category;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryApi {
    private final CategoryService categoryService;
    public CategoryApi(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    /**
     * 创建商品类目
     * @param categoryName 商品类目名称
     * @return 是否创建成功
     */
    @RequestMapping("/create")
    public ResponseEntity<Result<Boolean>> create(@RequestParam String categoryName) {
        return categoryService.create(categoryName);
    }
    /**
     * 更新商品类目，根据类目 ID 修改类目名称
     * @param category 商品类目
     * @return 是否更新成功
     */
    @RequestMapping("/update")
    public ResponseEntity<Result<Boolean>> update(@RequestBody Category category) {
        return categoryService.update(category);
    }
    /**
     * 删除商品类目，需要检查类目下是否有商品，有商品则不能删除，抛出异常
     * @param categoryId 商品类目 ID
     * @return 是否删除成功
     */
    @RequestMapping("/delete")
    public ResponseEntity<Result<Boolean>> delete(@RequestParam Long categoryId) {
        return categoryService.delete(categoryId);
    }
    /**
     * 获取商品类目列表
     * @return 商品类目列表
     */
    @RequestMapping("/list")
    public ResponseEntity<Result<List<Category>>> list() {
        return categoryService.categoryList();
    }


}
