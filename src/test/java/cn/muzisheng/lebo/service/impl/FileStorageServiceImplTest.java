package cn.muzisheng.lebo.service.impl;

import cn.muzisheng.lebo.config.StorageConfig;
import cn.muzisheng.lebo.exception.StorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * File storage tests use JUnit temporary directories. The files are real, but
 * JUnit deletes them after the test, satisfying the cleanup requirement without
 * relying on a shared database or permanent filesystem state.
 */
class FileStorageServiceImplTest {

    @TempDir
    private Path tempDir;

    @Test
    void storeLoadAndLoadAllUseRealTemporaryFiles() throws Exception {
        FileStorageServiceImpl service = serviceAt(tempDir);
        service.init();
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "hello".getBytes());

        String storedName = service.store(file, "avatar").getBody().getData();
        Path loaded = service.load("avatar/" + storedName);

        assertThat(storedName).startsWith("IMAGE_").endsWith(".png");
        assertThat(Files.readString(loaded)).isEqualTo("hello");
        assertThat(service.loadAsResource("avatar/" + storedName).exists()).isTrue();
        try (var paths = service.loadAll()) {
            assertThat(paths.map(Path::toString).toList()).contains("avatar");
        }
    }

    @Test
    void storageRejectsUnsafeOrMissingInputs() {
        FileStorageServiceImpl service = serviceAt(tempDir);
        MockMultipartFile empty = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        assertThatThrownBy(() -> service.store(empty, "docs"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("文件为空");
        assertThatThrownBy(() -> service.load(" "))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("文件名不能为空");
        assertThatThrownBy(() -> service.load("missing.txt"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("文件不存在");
        assertThatThrownBy(() -> service.loadAsResource("missing.txt"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("读取不到存储的文件");
    }

    @Test
    void loadAsResourceFallsBackToStaticClasspathResources() throws Exception {
        FileStorageServiceImpl service = serviceAt(tempDir);

        assertThat(service.loadAsResource("icon/home.png").getFilename()).isEqualTo("home.png");
        assertThatThrownBy(() -> service.loadAsResource(" "))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("文件名不能为空");
    }

    @Test
    void filesystemFailuresAreWrappedAsStorageExceptions() throws Exception {
        Path fileAsRoot = tempDir.resolve("not-a-directory");
        Files.writeString(fileAsRoot, "occupied");
        FileStorageServiceImpl service = serviceAt(fileAsRoot);

        assertThatThrownBy(service::init)
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("不能初始化下载路径失败");
        assertThatThrownBy(() -> serviceAt(tempDir.resolve("missing-root")).loadAll())
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("读取文件失败");
        assertThatThrownBy(() -> service.store(fileThrowingOnRead(), "docs"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("存储文件失败");
    }

    @Test
    void constructorAndDeleteAllHandleStorageRootLifecycle() {
        StorageConfig blankConfig = new StorageConfig();
        ReflectionTestUtils.setField(blankConfig, "location", " ");
        assertThatThrownBy(() -> new FileStorageServiceImpl(blankConfig))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("文件上传位置不能为空");

        FileStorageServiceImpl service = serviceAt(tempDir);
        service.init();
        assertThat(Files.exists(tempDir)).isTrue();
        service.deleteAll();
        assertThat(Files.exists(tempDir)).isFalse();
    }

    private FileStorageServiceImpl serviceAt(Path root) {
        StorageConfig config = new StorageConfig();
        ReflectionTestUtils.setField(config, "location", root.toString());
        return new FileStorageServiceImpl(config);
    }

    private MultipartFile fileThrowingOnRead() {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return "broken.txt";
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 1;
            }

            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("broken stream");
            }

            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException("broken stream");
            }

            @Override
            public void transferTo(java.io.File dest) throws IOException {
                throw new IOException("broken stream");
            }
        };
    }
}
