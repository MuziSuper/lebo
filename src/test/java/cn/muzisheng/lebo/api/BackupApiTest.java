package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.UnitTestSupport;
import cn.muzisheng.lebo.service.BackupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.mockito.Mockito.verify;

class BackupApiTest extends UnitTestSupport {

    @Mock
    private BackupService backupService;

    @Test
    void delegatesExportAndImport() throws IOException {
        BackupApi api = new BackupApi(backupService);
        MultipartFile file = new MockMultipartFile("file", "backup.zip", "application/zip", "zip".getBytes());
        api.exportBackup();
        api.importBackup(file);

        verify(backupService).exportBackup();
        verify(backupService).importBackup(file);
    }
}
