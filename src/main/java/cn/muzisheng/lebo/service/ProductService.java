package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.pear.model.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService extends IService<Product> {
    /**
     * 获取商品列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 商品列表
     */
    ResponseEntity<Result<List<Product>>> list(Integer pageNum, Integer pageSize);

    /**
     * 获取商品详情
     * @param id 商品id
     * @return 商品详情
     */
    ResponseEntity<Result<Product>> detail(Long id);
    /**
     * 添加商品
     * @param product 商品信息
     * @return 添加结果
     */
    ResponseEntity<Result<Long>> add(Product product);

    /**
     * 修改商品
     * @param product 待修改的商品信息
     * @return 修改结果
     */
    ResponseEntity<Result<Boolean>> update(Product product);
    /**
     * 删除商品
     * @param id 商品id
     * @return 删除结果
     */
    ResponseEntity<Result<Boolean>> delete(Long id);

}
