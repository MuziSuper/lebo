package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.StorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class GeneralApi {

    private final StorageService storageService;

    public GeneralApi(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 代理下载或查看资源
     * @param download 是否下载（不为空时触发下载）
     * @param filename 文件名
     * @param pictureCategory 文件分类目录（子目录名）
     * @return 文件资源
     **/
    @GetMapping(value = {"/files/{filename:.+}", "/files/{download:.+}/{filename:.+}"})
    @ResponseBody
    public ResponseEntity<Resource> serveFile(
            @PathVariable(required = false) String download,
            @PathVariable String filename,
            @RequestParam String pictureCategory) {

        // 验证目录名是否为空
        if (pictureCategory == null || pictureCategory.trim().isEmpty()) {
            throw new StorageException("文件分类目录不能为空");
        }

        // 防止目录遍历攻击
        if (!pictureCategory.matches("^[a-zA-Z0-9_-]+$")) {
            throw new StorageException("非法的目录名称");
        }

        // 获取文件数据（带子目录路径）
        Resource file = storageService.loadAsResource(pictureCategory + "/" + filename);

        // 如果文件为空就返回 404
        if (file == null) {
            throw new StorageException("文件不存在");
        }

        Response<Resource> response = new Response<>();

        // 如果 download 不为空，则执行下载，添加消息头 attachment
        if (download != null) {
            response.putHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"");
        }

        // 设置默认文件类型为application/octet-stream，二进制流
        String contentType = "application/octet-stream";
        if (file.getFilename() != null) {
            // 获得文件名后缀
            String ext = getFileExtension(file.getFilename());
            contentType = switch (ext) {
                case "pdf" -> "application/pdf";
                case "png", "gif", "jpg" -> "image/" + ext;
                case "jpeg" -> "image/jpeg";
                case "ofd", "zip" -> "application/" + ext;
                case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                default -> contentType;
            };
        }

        response.putHeader(HttpHeaders.CONTENT_TYPE, contentType);
        response.setData(file);

        // 返回封装好的响应实体
        return response.valueOnlyData();
    }

    /**
     * 获得文件名后缀
     * @param fileName 文件名
     * @return 文件后缀
     **/
    public String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else
            return "";
    }

    /**
     * 上传文件
     * @param file 上传的文件
     * @param pictureCategory 文件分类目录（子目录名）
     * @return 上传后的服务器文件地址，如果为 null 则上传失败
     * @throws StorageException 存储异常
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Result<String>> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String pictureCategory) {

        // 验证目录名是否为空
        if (pictureCategory == null || pictureCategory.trim().isEmpty()) {
            throw new StorageException("文件分类目录不能为空");
        }

        // 防止目录遍历攻击
        if (!pictureCategory.matches("^[a-zA-Z0-9_-]+$")) {
            throw new StorageException("非法的目录名称");
        }

        return storageService.store(file, pictureCategory);
    }
}