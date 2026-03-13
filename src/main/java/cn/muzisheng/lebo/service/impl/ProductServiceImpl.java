package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.ProductAddDTO;
import cn.muzisheng.lebo.dto.ProductInOutDTO;
import cn.muzisheng.lebo.dto.ProductListDTO;
import cn.muzisheng.lebo.dto.ProductShowDTO;
import cn.muzisheng.lebo.entity.InOutProductRecord;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.ProductException;
import cn.muzisheng.lebo.mapper.ProductMapper;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.param.ProductConsumeParam;
import cn.muzisheng.lebo.service.CategoryService;
import cn.muzisheng.lebo.service.InOutProductRecordService;
import cn.muzisheng.lebo.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService  {
    private final CategoryService categoryService;
    private final InOutProductRecordService inOutProductRecordService;
    
    public ProductServiceImpl(CategoryService categoryService, InOutProductRecordService inOutProductRecordService) {
        this.categoryService = categoryService;
        this.inOutProductRecordService = inOutProductRecordService;
    }

    @Override
    public ResponseEntity<Result<IPage<ProductShowDTO>>> list(ProductListDTO productListDTO) {
        int pageNum = Optional.ofNullable(productListDTO).map(ProductListDTO::getPageNum).orElse(1);
        int pageSize = Optional.ofNullable(productListDTO).map(ProductListDTO::getPageSize).orElse(10);
        String keyword = Optional.ofNullable(productListDTO).map(ProductListDTO::getKeyword).orElse(null);
        Long categoryId = Optional.ofNullable(productListDTO).map(ProductListDTO::getCategoryId).orElse(null);
        Integer statusCode = Optional.ofNullable(productListDTO).map(ProductListDTO::getStatus).orElse(null);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if(categoryId!=null) {
            queryWrapper.eq( "category_id",categoryId.toString());
        }
        if (keyword!=null&&!keyword.trim().isEmpty()){
            queryWrapper.like("name", keyword);
        }
        if(statusCode!=null) {
            if (ProductStatusEnum.contains(statusCode)) {
                queryWrapper.eq("status", statusCode);
            } else {
                log.error("商品状态不存在, status: {}", statusCode);
                throw new ProductException("商品状态不存在, status: " + statusCode);
            }
        }
        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> productPage = this.page(page, queryWrapper);
        // 将 Product 转换为 ProductShowDTO
        List<ProductShowDTO> dtoList = productPage.getRecords().stream()
                .map(ProductShowDTO::fromProduct)
                .toList();
        IPage<ProductShowDTO> dtoPage = new Page<>(pageNum, pageSize, productPage.getTotal());
        dtoPage.setRecords(dtoList);
        Response<IPage<ProductShowDTO>> response = new Response<>();
        response.setData(dtoPage);
        return response.value();

    }


    @Override
    public ResponseEntity<Result<Boolean>> add(ProductAddDTO productAddDTO) {
        Response<Boolean> response = new Response<>();
        Product product = productAddDTO.toProduct();
        if(!this.save(product)){
            log.error("添加商品失败, product_name: "+product.getName());
            throw new ProductException("添加商品失败, product_name: "+product.getName());
        }
        response.setData(true);
        return response.value();
    }
    @Transactional(rollbackFor = ProductException.class,isolation = Isolation.REPEATABLE_READ)
    @Override
    public ResponseEntity<Result<Boolean>> update(ProductAddDTO productAddDTO) {
        Response<Boolean> response = new Response<>();
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        setUpdateWrapper(updateWrapper,productAddDTO);
        if(!this.update(updateWrapper)){
            log.error("更新商品失败, product_id: "+productAddDTO.getId());
            throw new ProductException("更新商品失败, product_id: "+productAddDTO.getId());
        }
        response.setData(true);
        return response.value();
    }
    /**
     * 获取更新wrapper
     * @param updateWrapper 更新wrapper
     * @param productAddDTO 商品信息
     * @throws ProductException 商品异常
     */
    private void setUpdateWrapper(UpdateWrapper<Product> updateWrapper,ProductAddDTO productAddDTO) {

        if(productAddDTO==null){
            log.error("商品信息不能为空");
            throw new ProductException("商品信息不能为空");
        }
        if(productAddDTO.getId()==null){
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        updateWrapper.eq("id", productAddDTO.getId());
        if(productAddDTO.getStatus()!=null) {
            if (!ProductStatusEnum.contains(productAddDTO.getStatus())) {
                log.error("商品状态不存在, status: {}", productAddDTO.getStatus());
                throw new ProductException("商品状态不存在, status: " + productAddDTO.getStatus());
            }
            updateWrapper.set("status", productAddDTO.getStatus());
        }
        if(productAddDTO.getName()!=null&&!productAddDTO.getName().trim().isEmpty()) {
            updateWrapper.set("name", productAddDTO.getName());
        }
        if(productAddDTO.getDescription()!=null&&!productAddDTO.getDescription().trim().isEmpty()) {
            updateWrapper.set("description", productAddDTO.getDescription());
        }
        if(productAddDTO.getImage()!=null&&!productAddDTO.getImage().trim().isEmpty()) {
            updateWrapper.set("image", productAddDTO.getImage());
        }
        if(productAddDTO.getTags()!=null&&!productAddDTO.getTags().trim().isEmpty()) {
            updateWrapper.set("tags", productAddDTO.getTags());
        }
        if(productAddDTO.getUnit()!=null&&!productAddDTO.getUnit().trim().isEmpty()) {
            updateWrapper.set("unit", productAddDTO.getUnit());
        }
        if(productAddDTO.getCategoryId()!=null&&productAddDTO.getCategoryId()>0) {
            updateWrapper.set("category_id", productAddDTO.getCategoryId());
        }
        if(productAddDTO.getSalePrice()!=null&&productAddDTO.getSalePrice()>0) {
            updateWrapper.set("sale_price", productAddDTO.getSalePrice());
        }
        if(productAddDTO.getVipPrice()!=null&&productAddDTO.getVipPrice()>0) {
            updateWrapper.set("vip_price", productAddDTO.getVipPrice());
        }
        if(productAddDTO.getStorage()!=null&&productAddDTO.getStorage()>0) {
            updateWrapper.set("storage", productAddDTO.getStorage());
        }
        if(productAddDTO.getCostPrice()!=null&&productAddDTO.getCostPrice()>0) {
            updateWrapper.set("cost_price", productAddDTO.getCostPrice());
        }
        if(productAddDTO.getPoint()!=null&&productAddDTO.getPoint()>0) {
            updateWrapper.set("point", productAddDTO.getPoint());
        }
        if(productAddDTO.isPointConvert()!=null) {
            updateWrapper.set("is_point_convert", productAddDTO.isPointConvert());
        }
        if (productAddDTO.getImage()!=null&&!productAddDTO.getImage().trim().isEmpty()){
            updateWrapper.set("image", productAddDTO.getImage());
        }

    }
    /**
     * 商品出库入库,内部检测库存与判空等信息，主要用于商户操作单商品的出库入库
     * @param productInOutDTO 商品出库入库信息
     * @return 操作结果
     * @throws ProductException 商品异常
     */
    @Override
    @Transactional(rollbackFor = ProductException.class,isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> inOut(ProductInOutDTO productInOutDTO){
        Response<Boolean> response = new Response<>();

        String productId=Optional.ofNullable(productInOutDTO).map(ProductInOutDTO::getProductId).orElse( null);
        if(productId==null){
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        long inOutnumber =Optional.of(productInOutDTO).map(ProductInOutDTO::getNumber).orElse(0L);
        if (inOutnumber == 0) {
            log.error("商品数量无增减");
            throw new ProductException("商品数量无增减");
        }
        QueryWrapper<Product> queryWrapper=new QueryWrapper<Product>().eq("id", productId).last("FOR UPDATE");
        Product product = this.getOne(queryWrapper);
        if (product == null) {
            log.error("商品不存在, product_id: {}", productInOutDTO.getProductId());
            throw new ProductException("商品不存在, product_id: " + productInOutDTO.getProductId());
        }
        long currentStorage = Optional.ofNullable(product.getStorage()).orElse(0L);
        long newNumber=currentStorage+ inOutnumber;
        if (newNumber < 0) {
            log.error("商品数量不足, product_id: {}, inOutnumber: {}", productId, inOutnumber);
            throw new ProductException("商品数量不足, product_id: " + productId + ", inOutnumber: " + inOutnumber);
        }
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", productId);
        updateWrapper.set("storage", newNumber);
        if (!this.update(updateWrapper)) {
            log.error("商品入库出库失败, product_id: {}", productId);
            throw new ProductException("商品入库出库失败, product_id: " + productId);
        }
        
        // 创建出入库记录
        InOutProductRecord record = InOutProductRecord.builder()
                .productId(productId)
                .productName(product.getName())
                .number(Math.abs(inOutnumber))  // 记录为正数
                .remainNumber(newNumber)
                .type(inOutnumber > 0 ? 1 : 2)  // 1:入库, 2:出库
                .time(java.time.LocalDateTime.now())
                .build();
        inOutProductRecordService.addInOutRecordCount(record);
        
        response.setData(true);
        return response.value();
    }
    /**
     * 商品批量出库,内部检测库存与判空等信息，主要用于客户支付订单候商品的批量出库
     * @param productInOutDTOList 商品出库入库信息列表
     * @throws ProductException 商品异常
     */
    @Override
    @Transactional(rollbackFor = ProductException.class,isolation = Isolation.REPEATABLE_READ)
    public List<Product> inOutBatch(List<ProductInOutDTO> productInOutDTOList){
        // 参数判空
        if (productInOutDTOList == null || productInOutDTOList.isEmpty()) {
            log.error("商品出库入库列表不能为空");
            throw new ProductException("商品出库入库列表不能为空");
        }
        
        // 提取所有商品ID并校验
        List<String> productIds = productInOutDTOList.stream()
                .map(ProductInOutDTO::getProductId)
                .toList();
        
        // 检查是否有空商品ID
        if (productIds.stream().anyMatch(id -> id == null || id.trim().isEmpty())) {
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        
        // 批量查询商品并加锁，防止并发问题
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>()
                .in("id", productIds)
                .last("FOR UPDATE");
        List<Product> products = this.list(queryWrapper);
        
        // 检查商品是否存在
        if (products == null ){
            log.error("商品不存在, product_ids: {}", productIds);
            throw new ProductException("商品不存在, product_ids: " + productIds);
        }
        if(products.size() != productIds.size()) {
            List<String> existIds = products.stream().map(Product::getId).toList();
            List<String> notExistIds = productIds.stream()
                    .filter(id -> !existIds.contains(id))
                    .toList();
            log.error("部分商品不存在, product_ids: {}", notExistIds);
            throw new ProductException("部分商品不存在, product_ids: " + notExistIds);
        }
        
        // 构建商品ID到商品对象的映射
        Map<String, Product> productMap = products.stream()
                .collect(java.util.stream.Collectors.toMap(Product::getId, p -> p));
        
        // 验证库存、商品状态并计算新库存
        java.util.Map<String, Long> newStorageMap = new java.util.HashMap<>();
        for (ProductInOutDTO dto : productInOutDTOList) {
            String productId = dto.getProductId();
            long number = Optional.ofNullable(dto.getNumber()).orElse(0L);
            Product product = productMap.get(productId);

            // 商品状态校验：仅在出库时强制要求商品为在售状态
            if (product.getStatus() != ProductStatusEnum.SELL) {
                log.error("商品不在售, product_id: {}, productName: {}", productId, product.getName());
                throw new ProductException("商品不在售, productName: " + product.getName());
            }

            if (number == 0) {
                log.error("商品数量无增减, product_id: {}", productId);
                throw new ProductException("商品数量无增减, product_id: " + productId);
            }


            long currentStorage = Optional.ofNullable(product.getStorage()).orElse(0L);
            long newNumber = currentStorage + number;
            
            if (newNumber < 0) {
                log.error("商品数量不足, product_id: {}, inOutnumber: {}", productId, number);
                throw new ProductException("商品数量不足, product_id: " + productId + ", inOutnumber: " + number);
            }
            
            newStorageMap.put(productId, newNumber);
        }
        
        // 批量更新库存
        // 准备需要更新的商品列表
        List<Product> updateProducts = new ArrayList<>();
        for (ProductInOutDTO dto : productInOutDTOList) {
            Product updateProduct = new Product();
            updateProduct.setId(dto.getProductId());
            updateProduct.setStorage(newStorageMap.get(dto.getProductId()));
            updateProducts.add(updateProduct);
        }
        
        // 批量更新
        if (!this.updateBatchById(updateProducts)) {
            log.error("商品批量入库出库失败");
            throw new ProductException("商品批量入库出库失败");
        }
        
        // 批量创建出入库记录
        List<InOutProductRecord> records = new ArrayList<>();
        for (ProductInOutDTO dto : productInOutDTOList) {
            Product product = productMap.get(dto.getProductId());
            InOutProductRecord record = InOutProductRecord.builder()
                    .productId(dto.getProductId())
                    .productName(product.getName())
                    .number(Math.abs(dto.getNumber()))  // 记录为正数
                    .remainNumber(newStorageMap.get(dto.getProductId()))
                    .type(dto.getNumber() > 0 ? 1 : 2)  // 1:入库, 2:出库
                    .time(java.time.LocalDateTime.now())
                    .build();
            records.add(record);
        }
        inOutProductRecordService.addInOutRecordCountBatch(records);
        
        return products;
    }
//    /**
//     * 获取商品详情
//     * @param id 商品id
//     * @return 商品详情
//     */
//    @Override
//    public ResponseEntity<Result<ProductShowDTO>> show(Long id) {
//        Response<ProductShowDTO> response = new Response<>();
//
//        // 参数校验
//        if (id == null) {
//            log.error("商品ID不能为空");
//            throw new ProductException("商品ID不能为空");
//        }
//
//        // 查询商品
//        Product product = this.getById(id);
//        if (product == null) {
//            log.error("商品不存在, product_id: {}", id);
//            throw new ProductException("商品不存在, product_id: " + id);
//        }
//
//        // 转换为DTO
//        ProductShowDTO productShowDTO = ProductShowDTO.fromProduct(product);
//
//        response.setData(productShowDTO);
//        return response.value();
//    }
    /**
     * 删除商品
     * @param id 商品id
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = ProductException.class,isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> delete(Long id) {
        Response<Boolean> response = new Response<>();
        Product existProduct = this.getById(id);
        if (existProduct == null) {
            log.error("删除商品失败，商品不存在，product_id: {}", id);
            throw new ProductException("删除商品失败，商品不存在，product_id: " + id);
        }

        if(!this.removeById(id)){
            log.error("删除商品失败, product_id: "+id);
            throw new ProductException("删除商品失败, product_id: "+id);
        }
        response.setData(true);
        return response.value();
    }

    /**
     * 订单创建后的商品消费,该方法在订单创建后的商品消费方法中调用，使用事务机制，并且传入的productConsumeParam参数已经确认可以消费
     * @param productConsumeParam 商品消费信息，包含商品ID和新的商品数量
     * @throws ProductException 商品异常
     */
    public void consumeNotCheck(ProductConsumeParam productConsumeParam) throws ProductException{
        String productId=productConsumeParam.getProductId();
        long newNumber =productConsumeParam.getNewNumber();
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", productId);
        updateWrapper.set("storage", newNumber);
        if (!this.update(updateWrapper)) {
            log.error("商品入库出库失败, product_id: {}, newNumber: {}", productId, newNumber);
            throw new ProductException("商品入库出库失败, product_id: " + productId + ",newNumber: " + newNumber);
        }
    }
    /**
     * 根据类目ID查看是否存在商品
     * @param categoryId 类目id
     * @return 商品详情
     */
    public boolean existByCategoryId(Long categoryId){
        if (categoryId == null) {
            log.error("类目ID不能为空");
            throw new ProductException("类目ID不能为空");
        }
        return this.count(new QueryWrapper<Product>().eq("category_id", categoryId))>0;
    }
}
