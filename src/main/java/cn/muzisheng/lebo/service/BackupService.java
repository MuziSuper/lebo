package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 数据备份服务接口
 * 提供全量数据备份功能，将商品、类目、订单数据导出为压缩的JSON文件
 */
public interface BackupService {
    
    /**
     * 导出全量备份数据
     * 查询所有商品、类目、订单及订单项数据，转换为JSON并压缩为ZIP格式返回
     * 
     * @return 压缩后的ZIP文件字节数组，封装在ResponseEntity中
     * @throws IOException 当压缩过程中发生IO异常时抛出
     */
    ResponseEntity<byte[]> exportBackup() throws IOException;
    
    /**
     * 导入备份数据
     * 解析上传的ZIP备份文件，将数据覆盖写入数据库
     * 
     * @param file 上传的ZIP备份文件
     * @return 导入结果
     * @throws IOException 当解压或解析过程中发生IO异常时抛出
     */
    ResponseEntity<Result<Boolean>> importBackup(MultipartFile file) throws IOException;
}
