package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.service.WxCloudStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

class WxCloudStorageApiTest extends UnitTestSupport {

    @Mock
    private WxCloudStorageService wxCloudStorageService;

    @Test
    void validatesInputNormalizesPathAndDelegatesStorageOperations() {
        WxCloudStorageApi api = new WxCloudStorageApi(wxCloudStorageService);
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", MediaType.TEXT_PLAIN_VALUE, "x".getBytes());
        api.uploadFile(file, "/folder/a.txt");
        api.getDownloadUrl("cloud://file", 60L);
        api.getDownloadUrls(Map.of("fileIds", List.of("f1"), "maxAge", 30));
        api.deleteFile("f1");
        api.deleteFiles(List.of("f1"));
        verify(wxCloudStorageService).uploadFile(file, "folder/a.txt");
        verify(wxCloudStorageService).getDownloadUrl("cloud://file", 60L);
        verify(wxCloudStorageService).getDownloadUrls(List.of("f1"), 30L);
        verify(wxCloudStorageService).deleteFile("f1");
        verify(wxCloudStorageService).deleteFiles(List.of("f1"));
        assertThatThrownBy(() -> api.uploadFile(new MockMultipartFile("file", new byte[0]), "folder/a.txt"))
                .isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.uploadFile(file, " ")).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.getDownloadUrl(" ", null)).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.getDownloadUrls(Map.of("fileIds", List.of()))).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.deleteFile(" ")).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.deleteFiles(List.of())).isInstanceOf(StorageException.class);
    }
}
