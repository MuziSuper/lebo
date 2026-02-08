package cn.muzisheng.lebo.api;

import cn.muzisheng.lebo.constant.Constant;
import cn.muzisheng.lebo.exception.*;
import cn.muzisheng.lebo.param.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionApi {
    @ExceptionHandler(WXException.class)
    public ResponseEntity<ExceptionResponse> handleException(WXException e) {
        ExceptionResponse response = new ExceptionResponse(Constant.WX_EXCEPTION.getCode(), e.getMessage());
        return new ResponseEntity<>(response, Constant.WX_EXCEPTION.getHttpStatus());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ExceptionResponse> handleException(AuthorizationException e) {
        ExceptionResponse response = new ExceptionResponse(Constant.UNAPPROVED_EXCEPTION.getCode(), e.getMessage());
        return new ResponseEntity<>(response, Constant.UNAPPROVED_EXCEPTION.getHttpStatus());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleException(ForbiddenException e) {
        ExceptionResponse response = new ExceptionResponse(Constant.FORBIDDEN_EXCEPTION.getCode(), e.getMessage());
        return new ResponseEntity<>(response, Constant.FORBIDDEN_EXCEPTION.getHttpStatus());
    }
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ExceptionResponse> handleException(GeneralException e) {
        ExceptionResponse response = new ExceptionResponse(Constant.GENERAL_EXCEPTION.getCode(), e.getMessage());
        return new ResponseEntity<>(response, Constant.GENERAL_EXCEPTION.getHttpStatus());
    }

    @ExceptionHandler(IllegalException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalException e) {
        ExceptionResponse response = new ExceptionResponse(Constant.ILLEGAL_EXCEPTION.getCode(), e.getMessage());
        return new ResponseEntity<>(response, Constant.ILLEGAL_EXCEPTION.getHttpStatus());
    }
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ExceptionResponse> handleException(SQLException e) {
        ExceptionResponse response = new ExceptionResponse(Constant.NOT_FOUND_EXCEPTION.getCode(), e.getMessage());
        return new ResponseEntity<>(response, Constant.NOT_FOUND_EXCEPTION.getHttpStatus());
    }
}
