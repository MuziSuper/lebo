package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.constant.Constant;
import cn.muzisheng.lebo.exception.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExceptionApi centralizes API error envelopes. Each assertion uses the same
 * constants as production code so the test captures real mapping behavior.
 */
class ExceptionApiTest {

    private final ExceptionApi api = new ExceptionApi();

    @Test
    void mapsDomainExceptionsToConfiguredStatusAndCode() {
        assertMapping(api.handleException(new WXException("wx")), Constant.WX_EXCEPTION.getCode());
        assertMapping(api.handleException(new AuthorizationException("auth")), Constant.UNAPPROVED_EXCEPTION.getCode());
        assertMapping(api.handleException(new ForbiddenException("forbidden")), Constant.FORBIDDEN_EXCEPTION.getCode());
        assertMapping(api.handleException(new GeneralException("general")), Constant.GENERAL_EXCEPTION.getCode());
        assertMapping(api.handleException(new IllegalException("illegal")), Constant.ILLEGAL_EXCEPTION.getCode());
        assertMapping(api.handleException(new SQLException("sql")), Constant.SQL_EXCEPTION.getCode());
        assertMapping(api.handleException(new StorageException("storage")), Constant.STORAGE_EXCEPTION.getCode());
        assertMapping(api.handleException(new UserPointException("point")), Constant.USER_POINT_EXCEPTION.getCode());
        assertMapping(api.handleException(new UserException("user")), Constant.USER_EXCEPTION.getCode());
        assertMapping(api.handleException(new ProductException("product")), Constant.PRODUCT_EXCEPTION.getCode());
        assertMapping(api.handleException(new OrderException("order")), Constant.ORDER_EXCEPTION.getCode());
        assertMapping(api.handleException(new CategoryException("category")), Constant.CATEGORY_EXCEPTION.getCode());
    }

    private void assertMapping(org.springframework.http.ResponseEntity<cn.muzisheng.lebo.param.ExceptionResponse> response,
                               int expectedCode) {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(expectedCode);
        assertThat(response.getStatusCode().isError()).isTrue();
    }
}
