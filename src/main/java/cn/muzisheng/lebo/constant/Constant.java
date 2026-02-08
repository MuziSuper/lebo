package cn.muzisheng.lebo.constant;

import cn.muzisheng.lebo.param.CodeStatus;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.http.HttpStatus;

public class Constant {
    // 错误码
    public static final int ILLEGAL_EXCEPTION_CODE = 400;
    public static final int UNAPPROVED_EXCEPTION_CODE = 401;
    public static final int FORBIDDEN_EXCEPTION_CODE = 403;
    public static final int WX_EXCEPTION_CODE = 415;
    public static final int GENERAL_EXCEPTION_CODE = 500;
    public static final int SQL_EXCEPTION_CODE = 502;

    public static final CodeStatus ILLEGAL_EXCEPTION=new CodeStatus(HttpStatus.BAD_REQUEST,ILLEGAL_EXCEPTION_CODE);
    public static final CodeStatus UNAPPROVED_EXCEPTION=new CodeStatus(HttpStatus.UNAUTHORIZED,UNAPPROVED_EXCEPTION_CODE);
    public static final CodeStatus FORBIDDEN_EXCEPTION=new CodeStatus(HttpStatus.FORBIDDEN,FORBIDDEN_EXCEPTION_CODE);
    public static final CodeStatus WX_EXCEPTION=new CodeStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE,WX_EXCEPTION_CODE);
    public static final CodeStatus GENERAL_EXCEPTION=new CodeStatus(HttpStatus.INTERNAL_SERVER_ERROR,GENERAL_EXCEPTION_CODE);
    public static final CodeStatus SQL_EXCEPTION=new CodeStatus(HttpStatus.BAD_GATEWAY, SQL_EXCEPTION_CODE);
    // token默认配置
    public static final long TOKEN_DEFAULT_EXPIRE_DAY = 7 * 24 * 60 * 60 * 1000L;
    public static final String TOKEN_DEFAULT_SECRET_PREFIX = "Bearer ";
    public static final String TOKEN_DEFAULT_ISSUER = "lebo";
    public static final String TOKEN_DEFAULT_SUBJECT = "authentication";
    public static final String TOKEN_DEFAULT_SALT="LEBOTOKENDEFAULTSALTMUZISHENGSUPERHANDSOMEANDINTELLIGENT";
}
