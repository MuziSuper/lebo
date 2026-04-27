package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.config.StorageConfig;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.StorageService;
import cn.muzisheng.lebo.utils.IdUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
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

    public FileStorageServiceImpl(StorageConfig properties) {
        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("文件上传位置不能为空");
        }
        this.rootLocation = Paths.get(properties.getLocation()).normalize();
    }

    @Override
    public ResponseEntity<Result<String>> store(MultipartFile file, String pictureCategory) {
        Response<String> response = new Response<>();
        try {
            if (file.isEmpty()) {
                throw new StorageException("文件为空");
            }

            Path subDirectory = this.rootLocation.resolve(pictureCategory).normalize();
            Files.createDirectories(subDirectory);

            String fileName = IdUtil.generateFileName(file);
            Path destinationFile = subDirectory.resolve(fileName).normalize().toAbsolutePath();

            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                log.error("存储文件失败：{}", destinationFile.toAbsolutePath());
                throw new StorageException("无法将文件存储在当前目录之外");
            }

            try (InputStream inputStream = file.getInputStream()) {
                long size = Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

                if (size > 0) {
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


    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            log.error("读取文件失败: {}", e.getMessage());
            throw new StorageException("读取文件失败", e);
        }

    }

    @Override
    public Path load(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new StorageException("文件名不能为空");
        }
        Path filePath = rootLocation.resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            log.error("文件不存在: {}", filename);
            throw new StorageException("文件不存在: " + filename);
        }

        if (!Files.isReadable(filePath)) {
            log.error("文件不可读: {}", filename);
            throw new StorageException("文件不可读: " + filename);
        }

        return filePath;
    }

    @Override
    public Resource loadAsResource(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new StorageException("文件名不能为空");
        }

        Resource resource = tryLoadFromFileSystem(filename);
        if (resource != null) {
            return resource;
        }

        resource = tryLoadFromClasspath(filename);
        if (resource != null) {
            return resource;
        }

        throw new StorageException("读取不到存储的文件: " + filename);
    }

    private Resource tryLoadFromFileSystem(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize();
            if (Files.exists(file) && Files.isReadable(file)) {
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() && resource.isReadable()) {
                    log.debug("从文件系统加载文件: {}", filename);
                    return resource;
                }
            }
        } catch (MalformedURLException e) {
            log.debug("文件系统路径解析失败: {}", filename);
        }
        return null;
    }

    private Resource tryLoadFromClasspath(String filename) {
        try {
            String classpathLocation = "static/" + filename;
            Resource resource = new ClassPathResource(classpathLocation);
            if (resource.exists() && resource.isReadable()) {
                log.debug("从classpath加载文件: {}", classpathLocation);
                return resource;
            }
        } catch (Exception e) {
            log.debug("classpath加载失败: {}", filename);
        }
        return null;
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            log.error("不能初始化下载路径失败: {}", e.getMessage());
            throw new StorageException("不能初始化下载路径失败", e);
        }
    }
}