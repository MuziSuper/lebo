
package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.ProductAddDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.dto.ProductListDTO;
import cn.muzisheng.lebo.dto.ProductShowDTO;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.lebo.vo.InoutProductDashBoardVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 商品管理接口
 * 提供商品的增删改查功能
 */
@RestController
@RequestMapping("/product")
public class ProductApi {
    private final ProductService productService;

    /**
     * 构造函数注入 ProductService
     * @param productService 商品服务
     */
    public ProductApi(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 获取商品列表（分页）
     * @param productListDTO 查询参数（页码、每页数量、关键词、类目 ID、状态等）
     * @return 分页商品列表数据
     */
    @PostMapping("/list")
    public ResponseEntity<Result<IPage<ProductShowDTO>>> getProductList(@RequestBody(required = false) ProductListDTO productListDTO) {
        return productService.list(productListDTO);
    }

    /**
     * 添加商品
     * @param productAddDTO 商品信息（名称、描述、价格、库存等）
     * @return 添加结果（true/false）
     */
    @PostMapping("/add")
    public ResponseEntity<Result<Boolean>> addProduct(@RequestBody ProductAddDTO productAddDTO) {
        return productService.add(productAddDTO);
    }
    /**
     * 商品出库入库,内部检测库存与判空等信息，主要用于商户操作单商品的出库入库
     * @param productInOutDTO 商品入库出库信息（商品 ID、入库数量、出库数量等）
     * @return 操作结果（true/false）
     */
    @PostMapping("/inout")
    public ResponseEntity<Result<Boolean>> inoutProduct(@RequestBody ProductInOutDTO productInOutDTO) {
        return productService.inOut(productInOutDTO);
    }

    /**
     * 更新商品信息
     * @param productDTO 商品信息（必须包含商品 ID）
     * @return 更新结果（true/false）
     */
    @PostMapping("/update")
    public ResponseEntity<Result<Boolean>> updateProduct(@RequestBody ProductAddDTO productDTO) {
        return productService.update(productDTO);
    }

    /**
     * 删除商品
     * @param id 商品 ID
     * @return 删除结果（true/false）
     */
    @PostMapping("/delete")
    public ResponseEntity<Result<Boolean>> deleteProduct(@RequestParam("id") String id) {
        return productService.delete(id);
    }
    /**
     * 商户出入库大盘接口，获取昨日零点到今日零点的出入库数据量和当前库存总数量和金额
     * @return 入出库数据量和当前库存总数量和金额
     */
    @GetMapping("/dashboard")
    @ResponseBody
    public ResponseEntity<Result<InoutProductDashBoardVO>> getInoutDashboard() {
        return productService.getInoutDashboard();
    }
}