package cn.muzisheng.lebo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unified base class for fast unit tests.
 *
 * These tests deliberately do not start the Spring Boot application context:
 * service collaborators, mappers, databases, and external APIs are mocked at
 * their boundaries so each test verifies one class's real behavior directly.
 * Full-context integration tests can add a separate @SpringBootTest bootstrap
 * class later when a dedicated test database is configured.
 */
@ExtendWith(MockitoExtension.class)
public abstract class UnitTestSupport {
}
