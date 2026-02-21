package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shop")
public class ShopApi {
    private final ProductService productService;
    public ShopApi(ProductService productService) {
        this.productService = productService;
    }
    @RequestMapping("info")
    public ResponseEntity<Result<List<Product>>> getShopList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
        return productService.list(pageNum, pageSize);
    }
}
