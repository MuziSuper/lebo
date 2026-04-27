package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.dto.SlideshowDeleteDTO;
import cn.muzisheng.lebo.entity.SlideshowFileID;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.mapper.SlideshowFileIDMapper;
import cn.muzisheng.lebo.model.Response;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.WxCloudStorageService;
import cn.muzisheng.lebo.utils.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/slideshow")
public class SlideshowApi {

    private final WxCloudStorageService wxCloudStorageService;
    private final SlideshowFileIDMapper slideshowFileIDMapper;

    public SlideshowApi(WxCloudStorageService wxCloudStorageService, SlideshowFileIDMapper slideshowFileIDMapper) {
        this.wxCloudStorageService = wxCloudStorageService;
        this.slideshowFileIDMapper = slideshowFileIDMapper;
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Result<String>> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("上传文件不能为空");
        }

        String fileName = IdUtil.generateFileName(file);
        String cloudPath = "slideshow/" + fileName;

        ResponseEntity<Result<String>> uploadResponse = wxCloudStorageService.uploadFile(file, cloudPath);

        String fileId = uploadResponse.getBody().getData();

        SlideshowFileID slideshowFileID = SlideshowFileID.builder()
                .fileId(fileId)
                .build();
        slideshowFileIDMapper.insert(slideshowFileID);

        return uploadResponse;
    }

    @GetMapping("/fileIds")
    @ResponseBody
    public ResponseEntity<Result<List<String>>> getAllFileIds() {
        Response<List<String>> response = new Response<>();

        List<String> fileIds = slideshowFileIDMapper.selectList(null)
                .stream()
                .map(SlideshowFileID::getFileId)
                .toList();

        response.setData(fileIds);
        return response.value();
    }

    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Result<Void>> delete(@RequestBody SlideshowDeleteDTO dto) {
        Response<Void> response = new Response<>();

        if (dto.getFileId() == null || dto.getFileId().trim().isEmpty()) {
            throw new StorageException("fileId不能为空");
        }

        SlideshowFileID slideshowFileID = slideshowFileIDMapper.selectOne(
                new QueryWrapper<SlideshowFileID>()
                        .eq("file_id", dto.getFileId())
        );
        if (slideshowFileID == null) {
            throw new StorageException("轮播图记录不存在");
        }

        wxCloudStorageService.deleteFile(slideshowFileID.getFileId());

        slideshowFileIDMapper.deleteById(slideshowFileID.getId());

        return response.value();
    }
}
