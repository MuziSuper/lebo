package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.TestResponses;
import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.dto.SlideshowDeleteDTO;
import cn.muzisheng.lebo.entity.SlideshowFileID;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.mapper.SlideshowFileIDMapper;
import cn.muzisheng.lebo.service.WxCloudStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SlideshowApiTest extends UnitTestSupport {

    @Mock
    private WxCloudStorageService wxCloudStorageService;
    @Mock
    private SlideshowFileIDMapper slideshowFileIDMapper;

    @Test
    void uploadsListsAndDeletesSlideshowFiles() {
        SlideshowApi api = new SlideshowApi(wxCloudStorageService, slideshowFileIDMapper);
        MockMultipartFile file = new MockMultipartFile("file", "banner.jpg", "image/jpeg", "x".getBytes());
        when(wxCloudStorageService.uploadFile(eq(file), any())).thenReturn(TestResponses.ok("cloud://banner"));
        when(slideshowFileIDMapper.selectList(null)).thenReturn(List.of(
                SlideshowFileID.builder().fileId("f1").build(),
                SlideshowFileID.builder().fileId("f2").build()));
        SlideshowFileID existing = SlideshowFileID.builder().id(9L).fileId("f1").build();
        when(slideshowFileIDMapper.selectOne(any())).thenReturn(existing);

        assertThat(api.upload(file).getBody().getData()).isEqualTo("cloud://banner");
        assertThat(api.getAllFileIds().getBody().getData()).containsExactly("f1", "f2");
        SlideshowDeleteDTO dto = new SlideshowDeleteDTO();
        dto.setFileId("f1");
        assertThat(api.delete(dto).getStatusCode().is2xxSuccessful()).isTrue();

        ArgumentCaptor<SlideshowFileID> captor = ArgumentCaptor.forClass(SlideshowFileID.class);
        verify(slideshowFileIDMapper).insert(captor.capture());
        assertThat(captor.getValue().getFileId()).isEqualTo("cloud://banner");
        verify(wxCloudStorageService).deleteFile("f1");
        verify(slideshowFileIDMapper).deleteById(9L);
    }

    @Test
    void rejectsInvalidUploadAndDeleteRequests() {
        SlideshowApi api = new SlideshowApi(wxCloudStorageService, slideshowFileIDMapper);
        SlideshowDeleteDTO blank = new SlideshowDeleteDTO();
        blank.setFileId(" ");
        SlideshowDeleteDTO missing = new SlideshowDeleteDTO();
        missing.setFileId("missing");
        when(slideshowFileIDMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> api.upload(new MockMultipartFile("file", new byte[0])))
                .isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.delete(blank)).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.delete(missing)).isInstanceOf(StorageException.class);
    }
}
