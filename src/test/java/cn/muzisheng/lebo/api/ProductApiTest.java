package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.ProductAddDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.dto.ProductListDTO;
import cn.muzisheng.lebo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

class ProductApiTest extends UnitTestSupport {

    @Mock
    private ProductService productService;

    @Test
    void delegatesAllProductEndpoints() {
        ProductApi api = new ProductApi(productService);
        ProductListDTO listDTO = new ProductListDTO();
        ProductAddDTO addDTO = new ProductAddDTO();
        ProductInOutDTO inOutDTO = ProductInOutDTO.builder().productId("p1").number(1L).build();
        api.getProductList(listDTO);
        api.addProduct(addDTO);
        api.inoutProduct(inOutDTO);
        api.updateProduct(addDTO);
        api.deleteProduct("p1");
        api.getInoutDashboard();

        verify(productService).list(listDTO);
        verify(productService).add(addDTO);
        verify(productService).inOut(inOutDTO);
        verify(productService).update(addDTO);
        verify(productService).delete("p1");
        verify(productService).getInoutDashboard();
    }
}
