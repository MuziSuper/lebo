package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.WxCloudStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 微信云托管对象存储API接口
 * 
 * <p>提供基于微信云托管对象存储的文件管理接口，支持：
 * <ul>
 *   <li>文件上传：上传文件到腾讯云COS</li>
 *   <li>获取下载链接：生成临时下载URL</li>
 *   <li>文件删除：从云存储中删除文件</li>
 * </ul>
 * 
 * <p>基础路径：/wx-cloud
 * 
 * @see WxCloudStorageService
 */
@RestController
@RequestMapping("/wx-cloud")
public class WxCloudStorageApi {

    private final WxCloudStorageService wxCloudStorageService;

    public WxCloudStorageApi(WxCloudStorageService wxCloudStorageService) {
        this.wxCloudStorageService = wxCloudStorageService;
    }

    /**
     * 上传文件到云存储
     * 
     * <p>将文件上传到微信云托管对象存储，返回文件ID用于后续操作
     * 
     * @param file 上传的文件
     * @param path 云端存储路径（不要以/开头）
     * @return 包含文件ID的响应
     * @throws StorageException 文件或路径为空时抛出
     */
    @PostMapping("/upload")
    public ResponseEntity<Result<String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("path") String path) {

        if (file == null || file.isEmpty()) {
            throw new StorageException("上传文件不能为空");
        }

        if (path == null || path.trim().isEmpty()) {
            throw new StorageException("文件路径不能为空");
        }

        // 移除路径开头的斜杠
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return wxCloudStorageService.uploadFile(file, path);
    }

    /**
     * 获取单个文件的下载链接
     * 
     * <p>获取文件的临时下载URL，该URL有过期时间限制
     * 
     * @param fileId 文件ID
     * @param maxAge 下载链接有效期（秒），可选，默认86400秒
     * @return 包含下载链接的响应
     * @throws StorageException 文件ID为空时抛出
     */
    @GetMapping("/download-url")
    public ResponseEntity<Result<String>> getDownloadUrl(
            @RequestParam("fileId") String fileId,
            @RequestParam(value = "maxAge", required = false) Long maxAge) {

        if (fileId == null || fileId.trim().isEmpty()) {
            throw new StorageException("文件ID不能为空");
        }

        return wxCloudStorageService.getDownloadUrl(fileId, maxAge);
    }

    /**
     * 批量获取文件下载链接
     * 
     * <p>批量获取多个文件的临时下载URL（最多50个）
     * 
     * @param request 请求体，包含fileIds和maxAge
     * @return 包含下载链接列表的响应
     * @throws StorageException 文件ID列表为空时抛出
     */
    @PostMapping("/download-urls")
    public ResponseEntity<Result<List<String>>> getDownloadUrls(
            @RequestBody Map<String, Object> request) {

        @SuppressWarnings("unchecked")
        List<String> fileIds = (List<String>) request.get("fileIds");
        Long maxAge = request.get("maxAge") != null 
                ? ((Number) request.get("maxAge")).longValue() 
                : null;

        if (fileIds == null || fileIds.isEmpty()) {
            throw new StorageException("文件ID列表不能为空");
        }

        return wxCloudStorageService.getDownloadUrls(fileIds, maxAge);
    }

    /**
     * 删除单个文件
     * 
     * <p>从云存储中删除指定文件
     * 
     * @param fileId 要删除的文件ID
     * @return 包含删除结果的响应
     * @throws StorageException 文件ID为空时抛出
     */
    @DeleteMapping("/file")
    public ResponseEntity<Result<Boolean>> deleteFile(
            @RequestParam("fileId") String fileId) {

        if (fileId == null || fileId.trim().isEmpty()) {
            throw new StorageException("文件ID不能为空");
        }

        return wxCloudStorageService.deleteFile(fileId);
    }

    /**
     * 批量删除文件
     * 
     * <p>批量删除多个文件（最多50个）
     * 
     * @param fileIds 要删除的文件ID列表
     * @return 包含已删除文件ID列表的响应
     * @throws StorageException 文件ID列表为空时抛出
     */
    @DeleteMapping("/files")
    public ResponseEntity<Result<List<String>>> deleteFiles(
            @RequestBody List<String> fileIds) {

        if (fileIds == null || fileIds.isEmpty()) {
            throw new StorageException("文件ID列表不能为空");
        }

        return wxCloudStorageService.deleteFiles(fileIds);
    }
}
