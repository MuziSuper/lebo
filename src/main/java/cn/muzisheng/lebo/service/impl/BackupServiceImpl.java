package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.dto.BackupDataDTO;
import cn.muzisheng.lebo.entity.*;
import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 数据备份服务实现类
 * 实现全量数据备份功能，将商品、类目、订单、用户等数据导出为压缩的JSON文件
 */
@Log4j2
@Service
public class BackupServiceImpl implements BackupService {
    
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final UserPointService userPointService;
    private final InOutProductRecordService inOutProductRecordService;
    private final PointRecordService pointRecordService;
    private final HistoryOperationService historyOperationService;
    private final UserSignInService userSignInService;
    private final ObjectMapper objectMapper;
    
    // 批量插入每批次数量
    private static final int BATCH_SIZE = 500;
    
    public BackupServiceImpl(ProductService productService,
                             CategoryService categoryService,
                             OrderService orderService,
                             OrderItemService orderItemService,
                             UserService userService,
                             UserPointService userPointService,
                             InOutProductRecordService inOutProductRecordService,
                             PointRecordService pointRecordService,
                             HistoryOperationService historyOperationService,
                             UserSignInService userSignInService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.userPointService = userPointService;
        this.inOutProductRecordService = inOutProductRecordService;
        this.pointRecordService = pointRecordService;
        this.historyOperationService = historyOperationService;
        this.userSignInService = userSignInService;
        this.objectMapper = new ObjectMapper();
        // 注册Java 8时间模块，支持LocalDateTime序列化
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Override
    public ResponseEntity<byte[]> exportBackup() throws IOException {
        // 1. 全量查询所有商品
        List<Product> products = productService.list(new LambdaQueryWrapper<>());
        
        // 2. 全量查询所有类目
        List<Category> categories = categoryService.list(new LambdaQueryWrapper<>());
        
        // 3. 全量查询所有订单
        List<Order> orders = orderService.list(new LambdaQueryWrapper<>());
        
        // 4. 全量查询所有订单项
        List<OrderItem> orderItems = orderItemService.list(new LambdaQueryWrapper<>());
        
        // 5. 全量查询所有用户
        List<User> users = userService.list(new LambdaQueryWrapper<>());
        
        // 6. 全量查询所有用户积分
        List<UserPoint> userPoints = userPointService.list(new LambdaQueryWrapper<>());
        
        // 7. 全量查询所有商品出入库记录
        List<InOutProductRecord> inOutProductRecords = inOutProductRecordService.list(new LambdaQueryWrapper<>());
        
        // 8. 全量查询所有积分记录
        List<PointRecord> pointRecords = pointRecordService.list(new LambdaQueryWrapper<>());
        
        // 9. 全量查询所有用户签到记录
        List<UserSignIn> userSignIns = userSignInService.list(new LambdaQueryWrapper<>());
        
        // 10. 组装备份数据DTO
        BackupDataDTO backupData = BackupDataDTO.builder()
                .products(products)
                .categories(categories)
                .orders(orders)
                .orderItems(orderItems)
                .users(users)
                .userPoints(userPoints)
                .inOutProductRecords(inOutProductRecords)
                .pointRecords(pointRecords)
                .userSignIns(userSignIns)
                .build();
        
        // 10. 序列化为JSON字符串
        String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(backupData);
        
        // 11. 压缩为ZIP格式
        byte[] zipBytes = compressToZip(jsonContent);
        
        // 12. 构建响应，设置下载文件名
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "backup_" + timestamp + ".zip";
        
        // 13. 记录导出备份历史操作
        historyOperationService.addHistoryOperation(3, "手动导出备份数据，文件名：" + filename);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());
        headers.setContentLength(zipBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(zipBytes);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<Boolean>> importBackup(MultipartFile file) throws IOException {
        // 1. 校验文件
        if (file == null || file.isEmpty()) {
            throw new GeneralException("上传文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".zip")) {
            throw new GeneralException("请上传ZIP格式的备份文件");
        }
        
        // 2. 解析ZIP文件，提取JSON内容
        String jsonContent = extractJsonFromZip(file.getInputStream());
        
        // 3. 反序列化JSON为BackupDataDTO（如果解析失败会抛出异常，事务还未开始操作数据库）
        BackupDataDTO backupData;
        try {
            backupData = objectMapper.readValue(jsonContent, BackupDataDTO.class);
        } catch (Exception e) {
            log.error("解析备份数据失败: {}", e.getMessage());
            throw new GeneralException("备份数据格式错误，解析失败：" + e.getMessage());
        }
        
        // 4. 数据校验（在删除数据前先校验数据完整性）
        if (backupData == null) {
            throw new GeneralException("备份数据为空");
        }
        
        log.info("开始导入备份数据，商品:{} 类目:{} 订单:{} 订单项:{} 用户:{} 用户积分:{} 出入库记录:{} 积分记录:{} 签到记录:{}",
                backupData.getProducts() != null ? backupData.getProducts().size() : 0,
                backupData.getCategories() != null ? backupData.getCategories().size() : 0,
                backupData.getOrders() != null ? backupData.getOrders().size() : 0,
                backupData.getOrderItems() != null ? backupData.getOrderItems().size() : 0,
                backupData.getUsers() != null ? backupData.getUsers().size() : 0,
                backupData.getUserPoints() != null ? backupData.getUserPoints().size() : 0,
                backupData.getInOutProductRecords() != null ? backupData.getInOutProductRecords().size() : 0,
                backupData.getPointRecords() != null ? backupData.getPointRecords().size() : 0,
                backupData.getUserSignIns() != null ? backupData.getUserSignIns().size() : 0);
        
        // 5. 清空并覆盖写入各表数据
        productService.remove(new LambdaQueryWrapper<>());
        categoryService.remove(new LambdaQueryWrapper<>());
        orderService.remove(new LambdaQueryWrapper<>());
        orderItemService.remove(new LambdaQueryWrapper<>());
        userService.remove(new LambdaQueryWrapper<>());
        userPointService.remove(new LambdaQueryWrapper<>());
        inOutProductRecordService.remove(new LambdaQueryWrapper<>());
        pointRecordService.remove(new LambdaQueryWrapper<>());
        userSignInService.remove(new LambdaQueryWrapper<>());
        
        // 批量插入备份数据（按依赖顺序：类目 -> 商品 -> 用户 -> 用户积分 -> 订单 -> 订单项 -> 出入库记录 -> 积分记录 -> 签到记录）
        // 批量插入类目
        if (backupData.getCategories() != null && !backupData.getCategories().isEmpty()) {
            categoryService.saveBatch(backupData.getCategories(), BATCH_SIZE);
        }
        // 批量插入商品
        if (backupData.getProducts() != null && !backupData.getProducts().isEmpty()) {
            productService.saveBatch(backupData.getProducts(), BATCH_SIZE);
        }
        // 批量插入用户
        if (backupData.getUsers() != null && !backupData.getUsers().isEmpty()) {
            userService.saveBatch(backupData.getUsers(), BATCH_SIZE);
        }
        // 批量插入用户积分
        if (backupData.getUserPoints() != null && !backupData.getUserPoints().isEmpty()) {
            userPointService.saveBatch(backupData.getUserPoints(), BATCH_SIZE);
        }
        // 批量插入订单
        if (backupData.getOrders() != null && !backupData.getOrders().isEmpty()) {
            orderService.saveBatch(backupData.getOrders(), BATCH_SIZE);
        }
        // 批量插入订单项
        if (backupData.getOrderItems() != null && !backupData.getOrderItems().isEmpty()) {
            orderItemService.saveBatch(backupData.getOrderItems(), BATCH_SIZE);
        }
        // 批量插入出入库记录
        if (backupData.getInOutProductRecords() != null && !backupData.getInOutProductRecords().isEmpty()) {
            inOutProductRecordService.saveBatch(backupData.getInOutProductRecords(), BATCH_SIZE);
        }
        // 批量插入积分记录
        if (backupData.getPointRecords() != null && !backupData.getPointRecords().isEmpty()) {
            pointRecordService.saveBatch(backupData.getPointRecords(), BATCH_SIZE);
        }
        // 批量插入签到记录
        if (backupData.getUserSignIns() != null && !backupData.getUserSignIns().isEmpty()) {
            userSignInService.saveBatch(backupData.getUserSignIns(), BATCH_SIZE);
        }
        
        // 6. 记录导入备份历史操作
        historyOperationService.addHistoryOperation(9, "导入备份数据，文件名：" + originalFilename);
        
        log.info("导入备份数据成功，文件名：{}", originalFilename);
        
        return ResponseEntity.ok(new Result<>(true, null, LocalDateTime.now()));
    }
    
    /**
     * 将JSON字符串压缩为ZIP格式
     * 
     * @param jsonContent JSON字符串内容
     * @return ZIP文件字节数组
     * @throws IOException 压缩过程中发生IO异常
     */
    private byte[] compressToZip(String jsonContent) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            
            // 创建ZIP条目
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String jsonFilename = "backup_" + timestamp + ".json";
            ZipEntry zipEntry = new ZipEntry(jsonFilename);
            zos.putNextEntry(zipEntry);
            
            // 写入JSON内容
            zos.write(jsonContent.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
            
            zos.finish();
            return baos.toByteArray();
        }
    }
    
    /**
     * 从ZIP文件中提取JSON内容
     * 
     * @param inputStream ZIP文件输入流
     * @return JSON字符串内容
     * @throws IOException 解压过程中发生IO异常
     */
    private String extractJsonFromZip(InputStream inputStream) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".json")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    zis.closeEntry();
                    return baos.toString(StandardCharsets.UTF_8);
                }
            }
        }
        throw new GeneralException("ZIP文件中未找到JSON备份文件");
    }
}
