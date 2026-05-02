package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.TestResponses;
import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.exception.StorageException;
import cn.muzisheng.lebo.model.Result;
import cn.muzisheng.lebo.service.StorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class GeneralApiTest extends UnitTestSupport {

    @Mock
    private StorageService storageService;

    @Test
    void servesFileWithContentTypeAndOptionalDownloadHeader() {
        GeneralApi api = new GeneralApi(storageService);
        ByteArrayResource png = namedResource("demo.png");
        ByteArrayResource xlsx = namedResource("sheet.xlsx");
        when(storageService.loadAsResource("avatar/demo.png")).thenReturn(png);
        when(storageService.loadAsResource("docs/sheet.xlsx")).thenReturn(xlsx);

        ResponseEntity<?> download = api.serveFile("download", "demo.png", "avatar");
        ResponseEntity<?> preview = api.serveFile(null, "sheet.xlsx", "docs");

        assertThat(download.getBody()).isSameAs(png);
        assertThat(download.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo("image/png");
        assertThat(download.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=\"demo.png\"");
        assertThat(preview.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(preview.getHeaders()).doesNotContainKey(HttpHeaders.CONTENT_DISPOSITION);
    }

    @Test
    void servesAdditionalContentTypesFromActualFilenameExtension() {
        GeneralApi api = new GeneralApi(storageService);
        when(storageService.loadAsResource("docs/manual.pdf")).thenReturn(namedResource("manual.pdf"));
        when(storageService.loadAsResource("docs/archive.zip")).thenReturn(namedResource("archive.zip"));
        when(storageService.loadAsResource("avatar/photo.jpeg")).thenReturn(namedResource("photo.jpeg"));
        when(storageService.loadAsResource("docs/raw.bin")).thenReturn(namedResource("raw.bin"));
        when(storageService.loadAsResource("docs/nameless")).thenReturn(new ByteArrayResource("data".getBytes()));

        assertThat(api.serveFile(null, "manual.pdf", "docs").getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo("application/pdf");
        assertThat(api.serveFile(null, "archive.zip", "docs").getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo("application/zip");
        assertThat(api.serveFile(null, "photo.jpeg", "avatar").getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo("image/jpeg");
        assertThat(api.serveFile(null, "raw.bin", "docs").getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo("application/octet-stream");
        assertThat(api.serveFile(null, "nameless", "docs").getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
                .isEqualTo("application/octet-stream");
    }

    @Test
    void validatesCategoryAndUploadParameters() {
        GeneralApi api = new GeneralApi(storageService);
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "image/jpeg", "x".getBytes());
        ResponseEntity<Result<String>> stored = TestResponses.ok("/files/a.jpg");
        when(storageService.store(file, "product")).thenReturn(stored);
        when(storageService.loadAsResource("docs/missing.pdf")).thenReturn(null);

        assertThat(api.getFileExtension("a.b.pdf")).isEqualTo("pdf");
        assertThat(api.getFileExtension("filename")).isEmpty();
        assertThat(api.handleFileUpload(file, "product")).isSameAs(stored);
        assertThatThrownBy(() -> api.handleFileUpload(file, "../bad")).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.handleFileUpload(file, " ")).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.serveFile(null, "missing.pdf", "docs")).isInstanceOf(StorageException.class);
        assertThatThrownBy(() -> api.serveFile(null, "a.pdf", " ")).isInstanceOf(StorageException.class);
    }

    private ByteArrayResource namedResource(String filename) {
        return new ByteArrayResource("data".getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
