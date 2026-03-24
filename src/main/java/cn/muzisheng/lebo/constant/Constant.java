package cn.muzisheng.lebo.constant;

import cn.muzisheng.lebo.param.CodeStatus;
import org.springframework.http.HttpStatus;

public class Constant {
    // 错误码
    public static final int ILLEGAL_EXCEPTION_CODE = 400;
    public static final int UNAPPROVED_EXCEPTION_CODE = 401;
    public static final int FORBIDDEN_EXCEPTION_CODE = 403;
    public static final int WX_EXCEPTION_CODE = 415;
    public static final int GENERAL_EXCEPTION_CODE = 500;
    public static final int USER_POINT_EXCEPTION_CODE = 501;
    public static final int SQL_EXCEPTION_CODE = 502;
    public static final int USER_EXCEPTION_CODE = 503;
    public static final int PRODUCT_EXCEPTION_CODE = 504;
    public static final int ORDER_EXCEPTION_CODE = 505;
    public static final int CATEGORY_POINT_EXCEPTION_CODE = 506;
    public static final int STORAGE_EXCEPTION_CODE = 511;

    public static final CodeStatus ILLEGAL_EXCEPTION=new CodeStatus(HttpStatus.BAD_REQUEST,ILLEGAL_EXCEPTION_CODE);
    public static final CodeStatus UNAPPROVED_EXCEPTION=new CodeStatus(HttpStatus.UNAUTHORIZED,UNAPPROVED_EXCEPTION_CODE);
    public static final CodeStatus FORBIDDEN_EXCEPTION=new CodeStatus(HttpStatus.FORBIDDEN,FORBIDDEN_EXCEPTION_CODE);
    public static final CodeStatus WX_EXCEPTION=new CodeStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE,WX_EXCEPTION_CODE);
    public static final CodeStatus GENERAL_EXCEPTION=new CodeStatus(HttpStatus.INTERNAL_SERVER_ERROR,GENERAL_EXCEPTION_CODE);
    public static final CodeStatus SQL_EXCEPTION=new CodeStatus(HttpStatus.BAD_GATEWAY, SQL_EXCEPTION_CODE);
    public static final CodeStatus STORAGE_EXCEPTION=new CodeStatus(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, STORAGE_EXCEPTION_CODE);
    public static final CodeStatus USER_POINT_EXCEPTION=new CodeStatus(HttpStatus.NOT_IMPLEMENTED, USER_POINT_EXCEPTION_CODE);
    public static final CodeStatus USER_EXCEPTION=new CodeStatus(HttpStatus.SERVICE_UNAVAILABLE, USER_EXCEPTION_CODE);
    public static final CodeStatus PRODUCT_EXCEPTION=new CodeStatus(HttpStatus.GATEWAY_TIMEOUT, PRODUCT_EXCEPTION_CODE);
    public static final CodeStatus ORDER_EXCEPTION=new CodeStatus(HttpStatus.HTTP_VERSION_NOT_SUPPORTED, ORDER_EXCEPTION_CODE);
    public static final CodeStatus CATEGORY_EXCEPTION=new CodeStatus(HttpStatus.INTERNAL_SERVER_ERROR, CATEGORY_POINT_EXCEPTION_CODE);

    // token默认配置
    public static final long TOKEN_DEFAULT_EXPIRE_DAY = 7 * 24 * 60 * 60 * 1000L;
    public static final String TOKEN_DEFAULT_SECRET_PREFIX = "Bearer ";
    public static final String TOKEN_DEFAULT_ISSUER = "lebo";
    public static final String TOKEN_DEFAULT_SUBJECT = "authentication";
    public static final String TOKEN_DEFAULT_SALT="LEBOTOKENDEFAULTSALTMUZISHENGSUPERHANDSOMEANDINTELLIGENT";

    // 微信API常量
    public static final String WX_API_BASE = "https://api.weixin.qq.com";
    public static final String WX_CHECK_SESSION_URL=WX_API_BASE+"/swa/checksession";
    public static final String WX_CODE2SESSION_URL = WX_API_BASE + "/sns/jscode2session";
    public static final String WX_ACCESS_TOKEN_URL = WX_API_BASE + "/cgi-bin/token";
    public static final String WX_STABLE_TOKEN_URL = WX_API_BASE + "/cgi-bin/stable_token";
    public static final String WX_GET_USER_INFO_URL = WX_API_BASE + "/cgi-bin/user/info";

    /**
     * 加密算法名称
     */
    public static final String ALGORITHM = "AES";
    /**
     * 加密模式：ECB模式，填充方式：PKCS5Padding
     */
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String KEY = "LEBOISAHARDCODEDKEY12345";
}
