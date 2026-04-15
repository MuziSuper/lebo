package cn.muzisheng.lebo.service;

import cn.muzisheng.lebo.model.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 微信云托管对象存储服务接口
 * 
 * <p>提供基于微信云托管对象存储的文件管理功能，支持：
 * <ul>
 *   <li>文件上传：上传文件到腾讯云COS</li>
 *   <li>获取下载链接：生成临时下载URL</li>
 *   <li>文件删除：从云存储中删除文件</li>
 * </ul>
 * 
 * <p>使用开放接口服务模式，无需维护access_token
 */
public interface WxCloudStorageService {

    /**
     * 上传文件到云存储
     * 
     * <p>上传流程：
     * <ol>
     *   <li>调用微信API获取上传凭证</li>
     *   <li>将文件上传到腾讯云COS</li>
     *   <li>返回文件ID用于后续操作</li>
     * </ol>
     * 
     * @param file 要上传的文件
     * @param path 云端存储路径（不要以/开头）
     * @return 包含文件ID的响应实体
     */
    ResponseEntity<Result<String>> uploadFile(MultipartFile file, String path);

    /**
     * 获取单个文件的下载链接
     * 
     * @param fileId 文件ID（cloud://xxx格式）
     * @param maxAge 下载链接有效期（秒），为null时使用默认值86400秒
     * @return 包含下载链接的响应实体
     */
    ResponseEntity<Result<String>> getDownloadUrl(String fileId, Long maxAge);

    /**
     * 批量获取文件下载链接
     * 
     * <p>一次最多获取50个文件的下载链接
     * 
     * @param fileIds 文件ID列表
     * @param maxAge 下载链接有效期（秒），为null时使用默认值
     * @return 包含下载链接列表的响应实体
     */
    ResponseEntity<Result<List<String>>> getDownloadUrls(List<String> fileIds, Long maxAge);

    /**
     * 删除单个文件
     * 
     * @param fileId 要删除的文件ID
     * @return 包含删除结果的响应实体（true表示成功）
     */
    ResponseEntity<Result<Boolean>> deleteFile(String fileId);

    /**
     * 批量删除文件
     * 
     * <p>一次最多删除50个文件
     * 
     * @param fileIds 要删除的文件ID列表
     * @return 包含已删除文件ID列表的响应实体
     */
    ResponseEntity<Result<List<String>>> deleteFiles(List<String> fileIds);
}
