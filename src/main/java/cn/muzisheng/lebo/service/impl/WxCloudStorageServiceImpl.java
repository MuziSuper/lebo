package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.config.WxCloudStorageConfig;
import cn.muzisheng.lebo.constant.Constant;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.WXService;
import cn.muzisheng.lebo.service.WxCloudStorageService;
import cn.muzisheng.lebo.utils.HttpClientUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 微信云托管对象存储服务实现类
 * 
 * <p>基于微信云托管 API 实现文件的云端存储功能，支持：
 * <ul>
 *   <li>文件上传：获取上传凭证后上传到腾讯云COS</li>
 *   <li>获取下载链接：生成临时下载URL</li>
 *   <li>文件删除：从云存储中删除文件</li>
 * </ul>
 * 
 * <p>使用前提：
 * <ol>
 *   <li>配置微信小程序 AppID 和 AppSecret</li>
 *   <li>在微信云托管控制台开通云开发环境</li>
 *   <li>配置环境变量 WX_CLOUD_ENV 为云环境ID</li>
 * </ol>
 * 
 * @see WxCloudStorageConfig
 * @see WxCloudStorageService
 * @see HttpClientUtil
 * @see WXService
 * @see Constant
 */
@Slf4j
@Service
public class WxCloudStorageServiceImpl implements WxCloudStorageService {

    private final WxCloudStorageConfig config;
    private final WXService wxService;
    private final ObjectMapper objectMapper;

    /**
     * 构造函数
     * 
     * @param config 微信云存储配置
     * @param wxService 微信服务（用于获取 access_token）
     */
    public WxCloudStorageServiceImpl(WxCloudStorageConfig config, WXService wxService) {
        this.config = config;
        this.wxService = wxService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 上传文件到微信云托管对象存储
     * 
     * <p>上传流程：
     * <ol>
     *   <li>调用微信API获取上传凭证（包含COS上传地址、签名等）</li>
     *   <li>使用凭证将文件上传到腾讯云COS</li>
     *   <li>返回文件ID用于后续操作</li>
     * </ol>
     * 
     * @param file 要上传的文件
     * @param path 云端存储路径（不要以/开头），如 "images/product/abc.png"
     * @return 包含文件ID的响应实体
     * @throws StorageException 上传失败时抛出
     */
    @Override
    public ResponseEntity<Result<String>> uploadFile(MultipartFile file, String path) {
        Response<String> response = new Response<>();

        try {
            // 步骤1: 获取上传凭证
            UploadCredentials credentials = getUploadCredentials(path);
            
            // 步骤2: 上传文件到COS
            uploadToCos(file, credentials);
            
            // 步骤3: 返回文件ID
            response.setData(credentials.getFileId());
            log.info("文件上传成功, fileId: {}, path: {}", credentials.getFileId(), path);
            return response.value();
        } catch (Exception e) {
            log.error("文件上传失败, path: {}", path, e);
            throw new StorageException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个文件的临时下载链接
     * 
     * <p>调用微信API获取文件的临时下载URL，该URL有过期时间限制
     * 
     * @param fileId 文件ID（上传时返回的cloud://xxx格式）
     * @param maxAge 下载链接有效期（秒），为null时使用默认值86400秒（24小时）
     * @return 包含下载链接的响应实体
     * @throws StorageException 获取失败时抛出
     */
    @Override
    public ResponseEntity<Result<String>> getDownloadUrl(String fileId, Long maxAge) {
        Response<String> response = new Response<>();

        // 使用默认有效期
        if (maxAge == null) {
            maxAge = config.getDefaultMaxAge();
        }

        try {
            // 构建请求URL（带 access_token）
            String url = buildWxApiUrl(Constant.WX_CLOUD_DOWNLOAD_URL);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("env", config.getEnv());
            requestBody.put("file_list", List.of(
                    Map.of("fileid", fileId, "max_age", maxAge)
            ));

            // 使用 HttpClientUtil 发送POST请求
            String responseBody = HttpClientUtil.postJson(url, requestBody);

            // 解析响应
            JsonNode root = objectMapper.readTree(responseBody);
            int errcode = root.path("errcode").asInt();

            if (errcode != 0) {
                String errmsg = root.path("errmsg").asText();
                throw new StorageException("获取下载链接失败: " + errmsg);
            }

            // 提取下载链接
            JsonNode fileList = root.path("file_list");
            if (fileList.isArray() && fileList.size() > 0) {
                String downloadUrl = fileList.get(0).path("download_url").asText();
                response.setData(downloadUrl);
                log.info("获取下载链接成功, fileId: {}", fileId);
                return response.value();
            } else {
                throw new StorageException("获取下载链接失败: 返回数据为空");
            }
        } catch (IOException e) {
            log.error("获取下载链接失败, fileId: {}", fileId, e);
            throw new StorageException("获取下载链接失败: " + e.getMessage());
        }
    }

    /**
     * 批量获取多个文件的临时下载链接
     * 
     * <p>一次最多获取50个文件的下载链接
     * 
     * @param fileIds 文件ID列表
     * @param maxAge 下载链接有效期（秒），为null时使用默认值
     * @return 包含下载链接列表的响应实体，顺序与请求中的fileIds对应
     * @throws StorageException 获取失败时抛出
     */
    @Override
    public ResponseEntity<Result<List<String>>> getDownloadUrls(List<String> fileIds, Long maxAge) {
        Response<List<String>> response = new Response<>();

        // 使用默认有效期
        if (maxAge == null) {
            maxAge = config.getDefaultMaxAge();
        }

        // 参数校验
        if (fileIds == null || fileIds.isEmpty()) {
            throw new StorageException("文件ID列表不能为空");
        }

        if (fileIds.size() > 50) {
            throw new StorageException("一次最多获取50个文件的下载链接");
        }

        try {
            // 构建请求URL（带 access_token）
            String url = buildWxApiUrl(Constant.WX_CLOUD_DOWNLOAD_URL);

            // 构建文件列表
            List<Map<String, Object>> fileList = new ArrayList<>();
            for (String fileId : fileIds) {
                fileList.add(Map.of("fileid", fileId, "max_age", maxAge));
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("env", config.getEnv());
            requestBody.put("file_list", fileList);

            // 使用 HttpClientUtil 发送请求
            String responseBody = HttpClientUtil.postJson(url, requestBody);

            // 解析响应
            JsonNode root = objectMapper.readTree(responseBody);
            int errcode = root.path("errcode").asInt();

            if (errcode != 0) {
                String errmsg = root.path("errmsg").asText();
                throw new StorageException("获取下载链接失败: " + errmsg);
            }

            // 提取下载链接列表
            List<String> downloadUrls = new ArrayList<>();
            JsonNode fileListResult = root.path("file_list");
            if (fileListResult.isArray()) {
                for (JsonNode item : fileListResult) {
                    downloadUrls.add(item.path("download_url").asText());
                }
            }

            response.setData(downloadUrls);
            log.info("批量获取下载链接成功, count: {}", downloadUrls.size());
            return response.value();
        } catch (IOException e) {
            log.error("批量获取下载链接失败", e);
            throw new StorageException("批量获取下载链接失败: " + e.getMessage());
        }
    }

    /**
     * 删除单个文件
     * 
     * <p>从微信云托管对象存储中删除指定文件
     * 
     * @param fileId 要删除的文件ID
     * @return 包含删除结果的响应实体（true表示成功）
     * @throws StorageException 删除失败时抛出
     */
    @Override
    public ResponseEntity<Result<Boolean>> deleteFile(String fileId) {
        Response<Boolean> response = new Response<>();

        try {
            // 构建请求URL（带 access_token）
            String url = buildWxApiUrl(Constant.WX_CLOUD_DELETE_URL);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("env", config.getEnv());
            requestBody.put("fileid_list", List.of(fileId));

            // 使用 HttpClientUtil 发送请求
            String responseBody = HttpClientUtil.postJson(url, requestBody);

            // 解析响应
            JsonNode root = objectMapper.readTree(responseBody);
            int errcode = root.path("errcode").asInt();

            if (errcode != 0) {
                String errmsg = root.path("errmsg").asText();
                throw new StorageException("删除文件失败: " + errmsg);
            }

            response.setData(true);
            log.info("文件删除成功, fileId: {}", fileId);
            return response.value();
        } catch (IOException e) {
            log.error("删除文件失败, fileId: {}", fileId, e);
            throw new StorageException("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除多个文件
     * 
     * <p>一次最多删除50个文件
     * 
     * @param fileIds 要删除的文件ID列表
     * @return 包含已删除文件ID列表的响应实体
     * @throws StorageException 删除失败时抛出
     */
    @Override
    public ResponseEntity<Result<List<String>>> deleteFiles(List<String> fileIds) {
        Response<List<String>> response = new Response<>();

        // 参数校验
        if (fileIds == null || fileIds.isEmpty()) {
            throw new StorageException("文件ID列表不能为空");
        }

        if (fileIds.size() > 50) {
            throw new StorageException("一次最多删除50个文件");
        }

        try {
            // 构建请求URL（带 access_token）
            String url = buildWxApiUrl(Constant.WX_CLOUD_DELETE_URL);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("env", config.getEnv());
            requestBody.put("fileid_list", fileIds);

            // 使用 HttpClientUtil 发送请求
            String responseBody = HttpClientUtil.postJson(url, requestBody);

            // 解析响应
            JsonNode root = objectMapper.readTree(responseBody);
            int errcode = root.path("errcode").asInt();

            if (errcode != 0) {
                String errmsg = root.path("errmsg").asText();
                throw new StorageException("批量删除文件失败: " + errmsg);
            }

            // 提取已删除文件列表
            List<String> deletedList = new ArrayList<>();
            JsonNode deleteList = root.path("delete_list");
            if (deleteList.isArray()) {
                for (JsonNode item : deleteList) {
                    String deletedFileId = item.path("fileid").asText();
                    int status = item.path("status").asInt();
                    if (status == 0) {
                        deletedList.add(deletedFileId);
                    } else {
                        String errMsg = item.path("errmsg").asText();
                        log.warn("删除文件失败, fileId: {}, status: {}, errmsg: {}", deletedFileId, status, errMsg);
                    }
                }
            }

            response.setData(deletedList);
            log.info("批量删除文件成功, count: {}", deletedList.size());
            return response.value();
        } catch (IOException e) {
            log.error("批量删除文件失败", e);
            throw new StorageException("批量删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 构建微信 API 请求 URL（带 access_token）
     * 
     * <p>使用传统的 access_token 方式调用微信 API
     * 
     * @param apiUrl API 完整地址（来自 Constant 类）
     * @return 完整的请求 URL，包含 access_token 参数
     */
    private String buildWxApiUrl(String apiUrl) {
        String accessToken = wxService.getStableAccessToken();
        return apiUrl + "?access_token=" + accessToken;
    }

    /**
     * 获取文件上传凭证
     * 
     * <p>调用微信API /v2/tcb/uploadfile 获取上传所需的凭证信息，包括：
     * <ul>
     *   <li>url: COS上传地址</li>
     *   <li>token: 临时安全令牌</li>
     *   <li>authorization: 签名信息</li>
     *   <li>file_id: 文件ID（用于后续操作）</li>
     *   <li>cos_file_id: COS文件ID（上传时必须携带）</li>
     * </ul>
     * 
     * @param path 云端存储路径
     * @return 上传凭证对象
     * @throws IOException 网络请求失败时抛出
     * @throws StorageException API返回错误时抛出
     */
    private UploadCredentials getUploadCredentials(String path) throws IOException {
        // 构建请求URL（带 access_token）
        String url = buildWxApiUrl(Constant.WX_CLOUD_UPLOAD_URL);

        // 构建请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("env", config.getEnv());
        requestBody.put("path", path);

        // 使用 HttpClientUtil 发送POST请求获取上传凭证
        String responseBody = HttpClientUtil.postJson(url, requestBody);

        // 解析响应JSON
        JsonNode root = objectMapper.readTree(responseBody);
        int errcode = root.path("errcode").asInt();

        if (errcode != 0) {
            String errmsg = root.path("errmsg").asText();
            throw new StorageException("获取上传凭证失败: " + errmsg);
        }

        // 构建并返回上传凭证对象
        return new UploadCredentials(
                path,
                root.path("url").asText(),
                root.path("token").asText(),
                root.path("authorization").asText(),
                root.path("file_id").asText(),
                root.path("cos_file_id").asText()
        );
    }

    /**
     * 上传文件到腾讯云COS
     * 
     * <p>使用获取到的凭证将文件上传到COS，请求格式为 multipart/form-data
     * 
     * <p>必须包含以下字段：
     * <ul>
     *   <li>key: 文件路径</li>
     *   <li>Signature: 签名信息</li>
     *   <li>x-cos-security-token: 临时安全令牌</li>
     *   <li>x-cos-meta-fileid: COS文件ID（必填，否则文件无法下载）</li>
     *   <li>file: 文件内容</li>
     * </ul>
     * 
     * @param file 要上传的文件
     * @param credentials 上传凭证
     * @throws StorageException 上传失败时抛出
     */
    private void uploadToCos(MultipartFile file, UploadCredentials credentials) {
        // 构建文本参数
        Map<String, String> textParams = new HashMap<>();
        textParams.put("key", credentials.getPath());
        textParams.put("Signature", credentials.getAuthorization());
        textParams.put("x-cos-security-token", credentials.getToken());
        // 重要：x-cos-meta-fileid 必须填写，否则文件无法下载
        textParams.put("x-cos-meta-fileid", credentials.getCosFileId());

        try {
            // 使用 HttpClientUtil 发送 multipart 请求
            String responseBody = HttpClientUtil.postMultipart(
                    credentials.getUrl(),
                    textParams,
                    file.getBytes(),
                    file.getOriginalFilename()
            );

            log.debug("文件上传到COS成功, path: {}, response: {}", credentials.getPath(), responseBody);
        } catch (Exception e) {
            log.error("上传文件到COS失败, path: {}", credentials.getPath(), e);
            throw new StorageException("上传文件到COS失败: " + e.getMessage());
        }
    }

    /**
     * 上传凭证内部类
     * 
     * <p>封装从微信API获取的上传凭证信息
     */
    private static class UploadCredentials {
        /** 云端存储路径 */
        private final String path;
        /** COS上传地址 */
        private final String url;
        /** 临时安全令牌 */
        private final String token;
        /** 签名信息 */
        private final String authorization;
        /** 文件ID（用于后续下载和删除操作） */
        private final String fileId;
        /** COS文件ID（上传时必须携带，否则文件无法下载） */
        private final String cosFileId;

        /**
         * 构造函数
         * 
         * @param path 云端存储路径
         * @param url COS上传地址
         * @param token 临时安全令牌
         * @param authorization 签名信息
         * @param fileId 文件ID
         * @param cosFileId COS文件ID
         */
        public UploadCredentials(String path, String url, String token, String authorization, String fileId, String cosFileId) {
            this.path = path;
            this.url = url;
            this.token = token;
            this.authorization = authorization;
            this.fileId = fileId;
            this.cosFileId = cosFileId;
        }

        public String getPath() {
            return path;
        }

        public String getUrl() {
            return url;
        }

        public String getToken() {
            return token;
        }

        public String getAuthorization() {
            return authorization;
        }

        public String getFileId() {
            return fileId;
        }

        public String getCosFileId() {
            return cosFileId;
        }
    }
}
