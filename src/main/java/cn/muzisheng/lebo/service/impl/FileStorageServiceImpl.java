package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.config.StorageConfig;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.StorageService;
import cn.muzisheng.lebo.utils.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Log4j2
@Service
public class FileStorageServiceImpl implements StorageService {

    private final Path rootLocation;

    /**
     * 构造方法
     *
     * @param properties StorageProperties 对象
     **/
    @Autowired
    public FileStorageServiceImpl(StorageConfig properties) {
        // 若下载路径为空，抛出异常
        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("文件上传位置不能为空");
        }
        // 设置下载路径
        this.rootLocation = Paths.get(properties.getLocation()).normalize();
    }

    /**
     * 存储多文件对象到下载路径下
     *
     * @param file            MultipartFile 类型的多文件对象
     * @param pictureCategory 图片分类目录（子目录名）
     * @return 存储后的文件名
     * @throws StorageException 存储异常
     **/
    @Override
    public ResponseEntity<Result<String>> store(MultipartFile file, String pictureCategory) {
        Response<String> response = new Response<>();
        try {
            // 判断是否为空
            if (file.isEmpty()) {
                throw new StorageException("文件为空");
            }

            // 创建子目录路径
            Path subDirectory = this.rootLocation.resolve(pictureCategory).normalize();

            // 确保子目录存在
            Files.createDirectories(subDirectory);

            // 生成文件名（包含子目录的相对路径）
            String fileName = generateFileName(file);  // 建议重命名这个方法

            // 构建目标文件路径（在子目录下）
            Path destinationFile = subDirectory.resolve(fileName).normalize().toAbsolutePath();

            // 安全检查：确保目标文件仍在根目录内
            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                log.error("存储文件失败：{}", destinationFile.toAbsolutePath());
                throw new StorageException("无法将文件存储在当前目录之外");
            }

            // 拷贝文件到子目录
            try (InputStream inputStream = file.getInputStream()) {
                long size = Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

                if (size > 0) {
                    // 返回完整的相对路径（包含子目录）
                    response.setData(fileName);
                    log.info("文件上传成功：{}, 大小：{} bytes", fileName, size);
                    return response.value();
                }
            }

        } catch (IOException e) {
            log.error("存储文件失败：{}", e.getMessage());
            throw new StorageException("存储文件失败", e);
        }

        return response.value();
    }

    // 生成唯一文件名的方法
    private String generateFileName(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        // 增强文件名处理
        if (originalFilename != null && !originalFilename.isEmpty()) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {  // 确保不是以点开头的文件
                extension = originalFilename.substring(lastDotIndex);
            }
        }

        // 使用StringBuilder提高性能
        return new StringBuilder()
                .append(System.currentTimeMillis()).append("_")
                .append(RandomUtil.generateId())
                .append(extension)
                .toString();
    }
    /**
     * 获得下载路径下的所有文件Path流
     *
     * @return Path文件流
     * @throws StorageException 存储异常
     **/
    @Override
    public Stream<Path> loadAll() {
        try {
            //获得下载路径下的深度为一的所有文件夹与文件
            return Files.walk(this.rootLocation, 1)
                    // 去掉下载路径的文件夹
                    .filter(path -> !path.equals(this.rootLocation))
                    // 返回处理成相对路径的Path流
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage());
            throw new StorageException("读取文件失败", e);
        }

    }

    /**
     * 获得Path文件路径,内部判空
     *
     * @param filename 文件名
     * @return Path文件路径
     * @throws StorageException 文件名为空或文件不存在或文件不可读
     **/
    @Override
    public Path load(String filename) {
        // 参数验证
        if (filename == null || filename.trim().isEmpty()) {
            throw new StorageException("文件名不能为空");
        }
        // 获得Path文件路径
        Path filePath=rootLocation.resolve(filename).normalize();
        // 检查文件是否存在
        if (!Files.exists(filePath)) {
            log.error("文件不存在: {}", filename);
            throw new StorageException("文件不存在: " + filename);
        }

        // 检查文件是否可读
        if (!Files.isReadable(filePath)) {
            log.error("文件不可读: {}", filename);
            throw new StorageException("文件不可读: " + filename);
        }

        return filePath;
    }

    /**
     * 获得文件资源
     *
     * @param filename 文件名
     * @return 文件资源
     * @throws StorageException 文件不存在异常
     **/
    @Override
    public Resource loadAsResource(String filename) {
        try {
            // Path 是 Java 7 引入的一个接口，它是 java.nio.file 包的一部分，用于表示文件系统中的路径。
            // Path 接口定义了一些基本的操作，而具体的实现类如 java.nio.file.Paths 提供了创建 Path 对象的方法。

            // 获得本地存储的此文件名的Path
            Path file = load(filename);
            // 获得文件URL，Resource是Spring Framework中的类，用于封装对资源的访问
            // toUri() 方法返回一个 URI 对象，表示文件可访问的网络位置
            Resource resource = new UrlResource(file.toUri());
            // 检测文件是否存在或可读
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageException("读取不到存储的文件: " + filename);

            }
        } catch (MalformedURLException e) {
            log.error("读取不到存储的文件: {}", e.getMessage());
            throw new StorageException("读取不到存储的文件: " + filename, e);
        }
    }

    /**
     * 删除下载路径文件夹及其路径下所有文件
     **/
    @Override
    public void deleteAll() {
        // toFile()将下载路径转为File对象,并遍历删除所有文件
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    /**
     * 初始化下载路径
     *
     * @throws StorageException 存储异常
     **/
    @Override
    public void init() {
        try {
            // 创建下载路径文件夹,若该文件夹已经存在则不做任何操作
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("不能初始化下载路径失败: {}", e.getMessage());
            throw new StorageException("不能初始化下载路径失败", e);
        }
    }
}