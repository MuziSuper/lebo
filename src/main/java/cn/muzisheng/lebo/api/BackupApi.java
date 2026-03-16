package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 数据备份接口
 * 提供数据导出和导入备份功能
 */
@RestController
@RequestMapping("/backup")
public class BackupApi {
    
    private final BackupService backupService;
    
    /**
     * 构造函数注入 BackupService
     * @param backupService 备份服务
     */
    public BackupApi(BackupService backupService) {
        this.backupService = backupService;
    }
    
    /**
     * 导出全量备份数据
     * 前端触发此接口后，后端全量查询所有商品数据、商品类目数据以及所有订单数据，
     * 将其压缩并转换为JSON数据，返回给客户端
     * 
     * @return ZIP压缩文件，包含备份的JSON数据
     * @throws IOException 当导出过程中发生IO异常时抛出
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBackup() throws IOException {
        return backupService.exportBackup();
    }
    
    /**
     * 导入备份数据
     * 前端上传ZIP备份文件，后端解析并覆盖写入数据库
     * 
     * @param file 上传的ZIP备份文件
     * @return 导入结果
     * @throws IOException 当导入过程中发生IO异常时抛出
     */
    @PostMapping("/import")
    public ResponseEntity<Result<Boolean>> importBackup(@RequestParam("file") MultipartFile file) throws IOException {
        return backupService.importBackup(file);
    }
}
