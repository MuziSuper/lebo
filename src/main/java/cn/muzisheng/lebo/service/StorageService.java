package cn.muzisheng.lebo.service;


import cn.muzisheng.lebo.model.Result;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    /**
     * 初始化下载路径
     **/
    void init();
    /**
     * 存储多文件对象到下载路径下
     * @param file MultipartFile类型的多文件对象
     * @return 存储后的文件名
     **/
    ResponseEntity<Result<String>> store(MultipartFile file, String pictureCategory);
    /**
     * 获得下载路径下的所有文件Path流
     * @return Path文件流
     **/
    Stream<Path> loadAll();
    /**
     * 获得Path文件路径
     * @param filename 文件名
     * @return Path文件路径
     **/
    Path load(String filename);
    /**
     * 获得文件资源
     * @param filename 文件名
     * @return 文件资源
     **/
    Resource loadAsResource(String filename);
    /**
     * 删除下载路径文件夹及其路径下所有文件
     **/
    void deleteAll();

}