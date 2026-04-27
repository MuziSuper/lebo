package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.*;
import cn.muzisheng.lebo.entity.InOutProductRecord;
import cn.muzisheng.lebo.entity.Order;
import cn.muzisheng.lebo.entity.Product;
import cn.muzisheng.lebo.exception.ProductException;
import cn.muzisheng.lebo.exception.UserPointException;
import cn.muzisheng.lebo.mapper.ProductMapper;
import cn.muzisheng.lebo.model.OrderTypeEnum;
import cn.muzisheng.lebo.model.ProductStatusEnum;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.param.PointConversionParam;
import cn.muzisheng.lebo.param.ProductConsumeParam;
import cn.muzisheng.lebo.service.HistoryOperationService;
import cn.muzisheng.lebo.service.InOutProductRecordService;
import cn.muzisheng.lebo.service.OrderService;
import cn.muzisheng.lebo.service.ProductService;
import cn.muzisheng.lebo.service.UserService;
import cn.muzisheng.lebo.utils.IdUtil;
import cn.muzisheng.lebo.vo.InoutProductDashBoardVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    private final InOutProductRecordService inOutProductRecordService;
    private final HistoryOperationService historyOperationService;
    private final OrderService orderService;
    private final UserService userService;

    public ProductServiceImpl(InOutProductRecordService inOutProductRecordService,
                              HistoryOperationService historyOperationService,
                              @Lazy OrderService orderService,
                              @Lazy UserService userService) {
        this.inOutProductRecordService = inOutProductRecordService;
        this.historyOperationService = historyOperationService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Result<IPage<ProductShowDTO>>> list(ProductListDTO productListDTO) {
        Integer pageNum = Optional.ofNullable(productListDTO).map(ProductListDTO::getPageNum).orElse(null);
        Integer pageSize = Optional.ofNullable(productListDTO).map(ProductListDTO::getPageSize).orElse(null);
        String keyword = Optional.ofNullable(productListDTO).map(ProductListDTO::getKeyword).orElse(null);
        Long categoryId = Optional.ofNullable(productListDTO).map(ProductListDTO::getCategoryId).orElse(null);
        Integer statusCode = Optional.ofNullable(productListDTO).map(ProductListDTO::getStatus).orElse(null);
        Boolean isPointConvert = Optional.ofNullable(productListDTO).map(ProductListDTO::getIsPointConvert).orElse(null);

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId.toString());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like("name", keyword).or().like("description", keyword));
        }
        if (statusCode != null) {
            if (ProductStatusEnum.contains(statusCode)) {
                queryWrapper.eq("status", statusCode);
            } else {
                log.error("商品状态不存在, status: {}", statusCode);
                throw new ProductException("商品状态不存在, status: " + statusCode);
            }
        }
        if (isPointConvert != null) {
            queryWrapper.eq("is_point_convert", isPointConvert ? 1 : 0);
        }

        Response<IPage<ProductShowDTO>> response = new Response<>();

        if (pageNum != null && pageSize != null && pageNum > 0 && pageSize > 0) {
            Page<Product> page = new Page<>(pageNum, pageSize);
            Page<Product> productPage = this.page(page, queryWrapper);
            List<ProductShowDTO> dtoList = productPage.getRecords().stream()
                    .map(ProductShowDTO::fromProduct)
                    .toList();
            IPage<ProductShowDTO> dtoPage = new Page<>(pageNum, pageSize, productPage.getTotal());
            dtoPage.setRecords(dtoList);
            response.setData(dtoPage);
        } else {
            List<Product> products = this.list(queryWrapper);
            List<ProductShowDTO> dtoList = products.stream()
                    .map(ProductShowDTO::fromProduct)
                    .toList();
            Page<ProductShowDTO> resultPage = new Page<>(1, products.size(), products.size());
            resultPage.setRecords(dtoList);
            response.setData(resultPage);
        }

        return response.value();
    }


    @Override
    public ResponseEntity<Result<Boolean>> add(ProductAddDTO productAddDTO) {
        Response<Boolean> response = new Response<>();
        Product product = productAddDTO.toProduct();
        if (!this.save(product)) {
            log.error("添加商品失败, product_name: " + product.getName());
            throw new ProductException("添加商品失败, product_name: " + product.getName());
        }
        // 记录历史操作：商品添加 (type=0)
        historyOperationService.addHistoryOperation(0, "添加商品：" + product.getName());
        response.setData(true);
        return response.value();
    }

    @Transactional(rollbackFor = ProductException.class, isolation = Isolation.REPEATABLE_READ)
    @Override
    public ResponseEntity<Result<Boolean>> update(ProductAddDTO productAddDTO) {
        Response<Boolean> response = new Response<>();
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        setUpdateWrapper(updateWrapper, productAddDTO);

        // 检查是否有设置任何更新字段
        if (updateWrapper.getParamNameValuePairs().isEmpty()) {
            log.error("更新商品失败，没有可更新的字段，product_id: {}", productAddDTO.getId());
            throw new ProductException("更新商品失败，没有可更新的字段");
        }

        if (!this.update(updateWrapper)) {
            log.error("更新商品失败，product_id: " + productAddDTO.getId());
            throw new ProductException("更新商品失败，product_id: " + productAddDTO.getId());
        }
        // 记录历史操作：商品修改 (type=1)
        String productName = Optional.ofNullable(productAddDTO.getName())
                .filter(name -> !name.trim().isEmpty())
                .orElseGet(() -> {
                    Product product = this.getById(productAddDTO.getId());
                    return product != null ? product.getName() : productAddDTO.getId();
                });
        historyOperationService.addHistoryOperation(1, "修改商品：" + productName);
        response.setData(true);
        return response.value();
    }

    /**
     * 获取更新wrapper
     *
     * @param updateWrapper 更新wrapper
     * @param productAddDTO 商品信息
     * @throws ProductException 商品异常
     */
    private void setUpdateWrapper(UpdateWrapper<Product> updateWrapper, ProductAddDTO productAddDTO) {

        if (productAddDTO == null) {
            log.error("商品信息不能为空");
            throw new ProductException("商品信息不能为空");
        }
        if (productAddDTO.getId() == null) {
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        updateWrapper.eq("id", productAddDTO.getId());
        if (productAddDTO.getStatus() != null) {
            if (!ProductStatusEnum.contains(productAddDTO.getStatus())) {
                log.error("商品状态不存在, status: {}", productAddDTO.getStatus());
                throw new ProductException("商品状态不存在, status: " + productAddDTO.getStatus());
            }
            updateWrapper.set("status", productAddDTO.getStatus());
        }
        if (productAddDTO.getName() != null && !productAddDTO.getName().trim().isEmpty()) {
            updateWrapper.set("name", productAddDTO.getName());
        }
        if (productAddDTO.getDescription() != null && !productAddDTO.getDescription().trim().isEmpty()) {
            updateWrapper.set("description", productAddDTO.getDescription());
        }
        if (productAddDTO.getImage() != null && !productAddDTO.getImage().trim().isEmpty()) {
            updateWrapper.set("image", productAddDTO.getImage());
        }
        if (productAddDTO.getTags() != null && !productAddDTO.getTags().trim().isEmpty()) {
            updateWrapper.set("tags", productAddDTO.getTags());
        }
        if (productAddDTO.getUnit() != null && !productAddDTO.getUnit().trim().isEmpty()) {
            updateWrapper.set("unit", productAddDTO.getUnit());
        }
        if (productAddDTO.getCategoryId() != null && productAddDTO.getCategoryId() > 0) {
            updateWrapper.set("category_id", productAddDTO.getCategoryId());
        }
        if (productAddDTO.getSalePrice() != null && productAddDTO.getSalePrice() > 0) {
            updateWrapper.set("sale_price", productAddDTO.getSalePrice());
        }
        if (productAddDTO.getStorage() != null && productAddDTO.getStorage() > 0) {
            updateWrapper.set("storage", productAddDTO.getStorage());
        }
        if (productAddDTO.getCostPrice() != null && productAddDTO.getCostPrice() > 0) {
            updateWrapper.set("cost_price", productAddDTO.getCostPrice());
        }
        if (productAddDTO.getPoint() != null && productAddDTO.getPoint() > 0) {
            updateWrapper.set("point", productAddDTO.getPoint());
        }
        if (productAddDTO.isPointConvert() != null) {
            updateWrapper.set("is_point_convert", productAddDTO.isPointConvert());
        }
        if (productAddDTO.getImage() != null && !productAddDTO.getImage().trim().isEmpty()) {
            updateWrapper.set("image", productAddDTO.getImage());
        }

    }

    /**
     * 商品出库入库,内部检测库存与判空等信息，主要用于商户操作单商品的出库入库
     *
     * @param productInOutDTO 商品出库入库信息
     * @return 操作结果
     * @throws ProductException 商品异常
     */
    @Override
    @Transactional(rollbackFor = ProductException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> inOut(ProductInOutDTO productInOutDTO) {
        Response<Boolean> response = new Response<>();

        String productId = Optional.ofNullable(productInOutDTO).map(ProductInOutDTO::getProductId).orElse(null);
        if (productId == null) {
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        long inOutnumber = Optional.of(productInOutDTO).map(ProductInOutDTO::getNumber).orElse(0L);
        if (inOutnumber == 0) {
            log.error("商品数量无增减");
            throw new ProductException("商品数量无增减");
        }
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>().eq("id", productId).last("FOR UPDATE");
        Product product = this.getOne(queryWrapper);
        if (product == null) {
            log.error("商品不存在, product_id: {}", productInOutDTO.getProductId());
            throw new ProductException("商品不存在, product_id: " + productInOutDTO.getProductId());
        }
        long currentStorage = Optional.ofNullable(product.getStorage()).orElse(0L);
        long newNumber = currentStorage + inOutnumber;
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
                .description(productInOutDTO.getDescription())
                .type(inOutnumber > 0 ? 1 : 2)  // 1:入库, 2:出库
                .time(java.time.LocalDateTime.now())
                .build();
        inOutProductRecordService.addInOutRecordCount(record);

        response.setData(true);
        return response.value();
    }

    /**
     * 商品批量出库,内部检测库存与判空等信息，主要用于客户支付订单候商品的批量出库
     * 数据库内实现商品出库操作以及创建商品出入库记录
     * @param productInOutDTOList 商品出库入库信息列表
     * @throws ProductException 商品异常
     */
    @Override
    @Transactional(rollbackFor = ProductException.class, isolation = Isolation.REPEATABLE_READ)
    public void inOutBatch(List<ProductInOutDTO> productInOutDTOList) {
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
        if (products == null) {
            log.error("商品不存在, product_ids: {}", productIds);
            throw new ProductException("商品不存在, product_ids: " + productIds);
        }
        if (products.size() != productIds.size()) {
            List<String> existIds = products.stream().map(Product::getId).toList();
            List<String> notExistIds = productIds.stream()
                    .filter(id -> !existIds.contains(id))
                    .toList();
            log.error("部分商品不存在, product_ids: {}", notExistIds);
            throw new ProductException("部分商品不存在, product_ids: " + notExistIds);
        }

        // 构建商品ID到商品对象的映射
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        Map<String, Long> newStorageMap = new HashMap<>();

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
                log.warn("商品数量无增减, product_id: {}", productId);
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
    }

    /**
     * 商品批量出库,内部检测库存与判空等信息，还检测商品是否可积分兑换，主要用于客户积分兑换商品的批量出库
     * 数据库执行了更新商品库存操作、创建商品出入库记录
     * @param productInOutDTOList 商品出库入库信息列表
     * @param currentUserPoint    用户当前积分
     * @param openId              用户openid
     * @throws ProductException   商品异常
     */
    @Override
    @Transactional(rollbackFor = ProductException.class, isolation = Isolation.REPEATABLE_READ)
    public PointRecordAddDTO outBatchByPoints(List<ProductInOutDTO> productInOutDTOList, long currentUserPoint, String openId) {
        // 校验参数
        if (productInOutDTOList == null || productInOutDTOList.isEmpty()) {
            log.error("商品出库入库列表不能为空");
            throw new ProductException("商品出库入库列表不能为空");
        }
        // 校验用户openid是否为空
        if (openId == null || openId.trim().isEmpty()) {
            log.error("用户openid不能为空");
            throw new ProductException("用户openid不能为空");
        }
        // 获取商品ID列表
        List<String> productIds = productInOutDTOList.stream()
                .map(ProductInOutDTO::getProductId)
                .toList();

        if (productIds.stream().anyMatch(id -> id == null || id.trim().isEmpty())) {
            log.error("商品ID不能为空");
            throw new ProductException("商品ID不能为空");
        }
        // 获取商品列表
        QueryWrapper<Product> queryWrapper = new QueryWrapper<Product>()
                .in("id", productIds)
                .last("FOR UPDATE");
        List<Product> products = this.list(queryWrapper);
        // 校验商品列表是否为空
        if (products == null || products.isEmpty()) {
            log.error("商品不存在, product_ids: {}", productIds);
            throw new ProductException("商品不存在, product_ids: " + productIds);
        }
        // 校验商品是否全部存在
        if (products.size() != productIds.size()) {
            Set<String> existIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            List<String> notExistIds = productIds.stream()
                    .filter(id -> !existIds.contains(id))
                    .toList();
            log.error("部分商品不存在, product_ids: {}", notExistIds);
            throw new ProductException("部分商品不存在, product_ids: " + notExistIds);
        }
        // 商品列表转换为Map, key为商品ID, value为商品实体
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 计算出需要消耗的总积分以及商品 Id 对应新库存 newStorage 的映射 Map
        PointConversionParam context = validateAndCalculatePoints(productInOutDTOList, productMap, currentUserPoint);

        // 更新商品库存
        List<Product> updateProducts = productInOutDTOList.stream()
                .map(dto -> {
                    Product updateProduct = new Product();
                    updateProduct.setId(dto.getProductId());
                    updateProduct.setStorage(context.getNewStorageMap().get(dto.getProductId()));
                    return updateProduct;
                })
                .toList();

        if (!this.updateBatchById(updateProducts)) {
            log.error("商品批量积分兑换出库失败");
            throw new ProductException("商品批量积分兑换出库失败");
        }
        // 创建商品积分兑换出库记录
        createInOutRecords(productInOutDTOList, productMap, context.getNewStorageMap());
        // 组装积分兑换记录
        PointRecordAddDTO pointRecordAddDTO = new PointRecordAddDTO();
        pointRecordAddDTO.setOpenId(openId);
        pointRecordAddDTO.setDescription("积分兑换商品");
        pointRecordAddDTO.setChangeAmount(-context.getTotalRequiredPoints());
        pointRecordAddDTO.setBeforeAmount(currentUserPoint);
        pointRecordAddDTO.setAfterAmount(currentUserPoint - context.getTotalRequiredPoints());
        return pointRecordAddDTO;
    }

    /**
     * 校验商品是否在售中、是否可积分兑换、库存是否足够等
     * 校验商品数量是否为负数（出库）, 并计算总需要积分
     * 校验用户积分是否足够
     *
     * @param productInOutDTOList 商品出库入库信息列表
     * @param productMap          商品Map
     * @param currentUserPoint    用户当前积分
     * @return 计算出需要消耗的总积分以及商品Id对应新库存newStorage的映射Map
     * @throws ProductException   商品异常
     * @throws UserPointException 用户积分异常
     */
    private PointConversionParam validateAndCalculatePoints(List<ProductInOutDTO> productInOutDTOList,
                                                            Map<String, Product> productMap,
                                                            long currentUserPoint) {
        List<String> notSellingProducts = new ArrayList<>();
        List<String> notConvertibleProducts = new ArrayList<>();
        List<String> insufficientStockProducts = new ArrayList<>();
        // 新库存Map, key为商品ID, value为新库存
        Map<String, Long> newStorageMap = new HashMap<>();
        // 计算总需要积分
        long totalRequiredPoints = 0;
        // 遍历商品出库入库信息列表
        for (ProductInOutDTO dto : productInOutDTOList) {
            String productId = dto.getProductId();
            long number = Optional.ofNullable(dto.getNumber()).orElse(0L);
            Product product = productMap.get(productId);
            // 校验商品是否在售中
            if (product.getStatus() != ProductStatusEnum.SELL) {
                notSellingProducts.add(product.getName());
                continue;
            }
            // 校验商品是否可积分兑换
            if (!Boolean.TRUE.equals(product.getIsPointConvert())) {
                notConvertibleProducts.add(product.getName());
                continue;
            }
            // 校验商品数量是否为负数（出库）
            if (number >= 0) {
                log.error("积分兑换商品数量必须为负数（出库）, product_id: {}", productId);
                throw new ProductException("积分兑换商品数量必须为负数（出库）, productName: " + product.getName());
            }
            // 校验商品库存是否足够
            if (Math.abs(number) > product.getStorage()) {
                log.error("积分兑换商品库存不足, product_id: {}, number: {}", productId, number);
                throw new ProductException("积分兑换商品库存不足, productName: " + product.getName() + ", number: " + number);
            }
            // 获取当前库存
            long currentStorage = Optional.of(product.getStorage()).orElse(0L);
            long newStorage = currentStorage + number;
            // 校验新库存是否为负数（出库）超卖
            if (newStorage < 0) {
                insufficientStockProducts.add(product.getName());
                continue;
            }
            // 更新新库存
            newStorageMap.put(productId, newStorage);
            // 计算总需要积分
            Long creditsExchange = product.getCreditsExchange();
            if (creditsExchange != null && creditsExchange > 0) {
                totalRequiredPoints += creditsExchange * Math.abs(number);
            }
        }
        // 如果遍历过程中有商品不在售中、不可积分兑换、库存不足，抛出异常
        if (!notSellingProducts.isEmpty()) {
            log.error("以下商品不在售: {}", notSellingProducts);
            throw new ProductException("以下商品不在售: " + String.join(", ", notSellingProducts));
        }

        if (!notConvertibleProducts.isEmpty()) {
            log.error("以下商品不可积分兑换: {}", notConvertibleProducts);
            throw new ProductException("以下商品不可积分兑换: " + String.join(", ", notConvertibleProducts));
        }

        if (!insufficientStockProducts.isEmpty()) {
            log.error("以下商品库存不足: {}", insufficientStockProducts);
            throw new ProductException("以下商品库存不足: " + String.join(", ", insufficientStockProducts));
        }

        // 校验用户积分是否足够
        if (currentUserPoint < totalRequiredPoints) {
            log.error("用户积分不足, 当前积分: {}, 所需积分: {}", currentUserPoint, totalRequiredPoints);
            throw new UserPointException("用户积分不足，当前积分: " + currentUserPoint + "，所需积分: " + totalRequiredPoints);
        }

        return new PointConversionParam(newStorageMap, totalRequiredPoints);
    }


    /**
     * 创建商品积分兑换出库记录
     *
     * @param productInOutDTOList 商品出库入库信息列表
     * @param productMap          商品Map
     * @param newStorageMap       新库存Map
     */
    private void createInOutRecords(List<ProductInOutDTO> productInOutDTOList,
                                    Map<String, Product> productMap,
                                    Map<String, Long> newStorageMap) {
        LocalDateTime now = LocalDateTime.now();
        List<InOutProductRecord> records = productInOutDTOList.stream()
                .map(dto -> {
                    Product product = productMap.get(dto.getProductId());
                    return InOutProductRecord.builder()
                            .id(IdUtil.generateInOutRecordId())
                            .productId(dto.getProductId())
                            .productName(product.getName())
                            .number(Math.abs(dto.getNumber()))
                            .remainNumber(newStorageMap.get(dto.getProductId()))
                            .description(dto.getDescription())
                            .type(2)
                            .time(now)
                            .build();
                })
                .toList();
        inOutProductRecordService.addInOutRecordCountBatch(records);
    }


    /**
     * 创建积分兑换商品兑换成功个人消息
     *
     * @param openId              用户Id
     * @param productInOutDTOList 商品出库入库信息列表
     * @param productMap          商品Map
     * @param totalRequiredPoints 总所需积分
     * @param afterAmount         剩余积分
     */
//    private void createConversionNotification(String openId,
//                                              List<ProductInOutDTO> productInOutDTOList,
//                                              Map<String, Product> productMap,
//                                              long totalRequiredPoints,
//                                              long afterAmount) {
//        Information information = InformationUtil.buildPointConversionNotification(
//                openId, productInOutDTOList, productMap, totalRequiredPoints, afterAmount);
//        informationService.saveInformation(information);
//        log.info("积分兑换通知发送成功, openId: {}", openId);
//    }


    /**
     * 删除商品
     *
     * @param id 商品id
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = ProductException.class, isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<Result<Boolean>> delete(String id) {
        Response<Boolean> response = new Response<>();
        Product existProduct = this.getById(id);
        if (existProduct == null) {
            log.error("删除商品失败，商品不存在，product_id: {}", id);
            throw new ProductException("删除商品失败，商品不存在，product_id: " + id);
        }

        if (!this.removeById(id)) {
            log.error("删除商品失败, product_id: " + id);
            throw new ProductException("删除商品失败, product_id: " + id);
        }
        // 记录历史操作：商品删除 (type=2)
        historyOperationService.addHistoryOperation(2, "删除商品：" + existProduct.getName());
        response.setData(true);
        return response.value();
    }

    /**
     * 订单创建后的商品消费,该方法在订单创建后的商品消费方法中调用，使用事务机制，并且传入的productConsumeParam参数已经确认可以消费
     *
     * @param productConsumeParam 商品消费信息，包含商品ID和新的商品数量
     * @throws ProductException 商品异常
     */
    public void consumeNotCheck(ProductConsumeParam productConsumeParam) throws ProductException {
        String productId = productConsumeParam.getProductId();
        long newNumber = productConsumeParam.getNewNumber();
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
     *
     * @param categoryId 类目id
     * @return 商品详情
     */
    public boolean existByCategoryId(Long categoryId) {
        if (categoryId == null) {
            log.error("类目ID不能为空");
            throw new ProductException("类目ID不能为空");
        }
        return this.count(new QueryWrapper<Product>().eq("category_id", categoryId)) > 0;
    }

    /**
     * 商户出入库大盘接口，获取昨日零点到今日零点的出入库数据量和当前库存总数量和金额
     *
     * @return 入出库数据量和当前库存总数量和金额
     */
    public ResponseEntity<Result<InoutProductDashBoardVO>> getInoutDashboard() {
        Response<InoutProductDashBoardVO> response = new Response<>();

        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime yesterdayStart = todayStart.minusDays(1);

        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        orderWrapper.eq("pay_type", OrderTypeEnum.OVER.getCode())
                .ge("end_time", yesterdayStart)
                .lt("end_time", todayStart);
        List<Order> orders = orderService.list(orderWrapper);

        long yesterdayProfit = orders.stream()
                .mapToLong(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0L)
                .sum();

        long currentUserCount = userService.count();

        QueryWrapper<Product> productWrapper = new QueryWrapper<>();
        productWrapper.in("status", ProductStatusEnum.SELL, ProductStatusEnum.SOLD_OUT);
        List<Product> products = this.list(productWrapper);

        long currentlyTotalStock = products.stream()
                .mapToLong(product -> product.getStorage() != null ? product.getStorage() : 0L)
                .sum();

        long currentLyTotalAmount = products.stream()
                .mapToLong(product -> {
                    long storage = product.getStorage() != null ? product.getStorage() : 0L;
                    int costPrice = product.getCostPrice() != null ? product.getCostPrice() : 0;
                    return storage * costPrice;
                })
                .sum();

        InoutProductDashBoardVO dashboardVO = InoutProductDashBoardVO.builder()
                .yesterdayProfit(yesterdayProfit)
                .currentUserCount(currentUserCount)
                .currentlyTotalStock(currentlyTotalStock)
                .currentLyTotalAmount(currentLyTotalAmount)
                .build();

        response.setData(dashboardVO);
        return response.value();
    }
}
