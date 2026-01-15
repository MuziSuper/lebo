package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.mapper.ProductMapper;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.pear.model.Result;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService  {

    @Override
    public ResponseEntity<Result<List<Product>>> list(Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Product>> detail(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Long>> add(Product product) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Boolean>> update(Product product) {
        return null;
    }

    @Override
    public ResponseEntity<Result<Boolean>> delete(Long id) {
        return null;
    }
}
