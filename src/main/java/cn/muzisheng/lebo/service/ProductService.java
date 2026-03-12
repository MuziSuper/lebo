package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.ProductAddDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.dto.ProductListDTO;
import cn.muzisheng.lebo.dto.ProductShowDTO;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.model.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

public interface ProductService extends IService<Product> {
    /**
     * 获取商品列表
     * @param productListDTO 商品列表参数
     * @return 商品列表
     */
    ResponseEntity<Result<IPage<ProductShowDTO>>> list(ProductListDTO productListDTO);

    /**
     * 添加商品
     * @param productAddDTO 商品信息
     * @return 添加结果
     */
    ResponseEntity<Result<Boolean>> add(ProductAddDTO productAddDTO);

    /**
     * 修改商品
     * @param productAddDTO 待修改的商品信息
     * @return 修改结果
     */
    ResponseEntity<Result<Boolean>> update(ProductAddDTO productAddDTO);
    /**
     * 商品出库入库
     * @param productInOutDTO 商品出库入库信息
     * @return 操作结果
     */
    ResponseEntity<Result<Boolean>> inOut(ProductInOutDTO productInOutDTO);

    /**
     * 订单创建后的商品消费
     * @param productInOutDTO 商品消费信息
     */
    void consume(ProductInOutDTO productInOutDTO);

    /**
     * 删除商品
     * @param id 商品id
     * @return 删除结果
     */
    ResponseEntity<Result<Boolean>> delete(Long id);

    /**
     * 根据类目ID查看是否存在商品
     * @param categoryId 类目id
     * @return 商品详情
     */
    boolean existByCategoryId(Long categoryId);

}
