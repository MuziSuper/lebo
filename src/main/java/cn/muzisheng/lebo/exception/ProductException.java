package cn.muzisheng.lebo.exception;
/**
 * 商品异常类
 * code: 504
 */
public class ProductException extends RuntimeException{
    public ProductException() {
    }
    public ProductException(String message) {
        super(message);
    }
    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
