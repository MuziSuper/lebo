package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.dto.ProductAddDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.dto.ProductListDTO;
import cn.muzisheng.lebo.dto.ProductShowDTO;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.ProductException;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.vo.InoutProductDashBoardVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
     * @param productAddDTO 待修改的商品基础信息
     * @return 修改结果
     */
    ResponseEntity<Result<Boolean>> update(ProductAddDTO productAddDTO);
    /**
     * 商品出库入库,内部检测库存与判空等信息，主要用于商户操作单商品的出库入库
     * @param productInOutDTO 商品出库入库信息
     * @return 操作结果
     * @throws ProductException 商品异常
     */
    ResponseEntity<Result<Boolean>> inOut(ProductInOutDTO productInOutDTO);


    /**
     * 商品批量出库入库,内部检测库存与判空等信息，主要用于客户支付订单候商品的批量出库
     * @param productInOutDTOList 商品出库入库信息列表
     * @throws ProductException 商品异常
     */
    List<Product> inOutBatch(List<ProductInOutDTO> productInOutDTOList);

//    /**
//     * 获取商品详情
//     * @param id 商品id
//     * @return 商品详情
//     */
//    ResponseEntity<Result<ProductShowDTO>> show(Long id);
    /**
     * 删除商品
     * @param id 商品id
     * @return 删除结果
     */
    ResponseEntity<Result<Boolean>> delete(String id);

    /**
     * 根据类目ID查看是否存在商品
     * @param categoryId 类目id
     * @return 商品详情
     */
    boolean existByCategoryId(Long categoryId);
    /**
     * 商户出入库大盘接口，获取昨日零点到今日零点的出入库数据量和当前库存总数量和金额
     * @return 入出库数据量和当前库存总数量和金额
     */
    ResponseEntity<Result<InoutProductDashBoardVO>> getInoutDashboard();
}
