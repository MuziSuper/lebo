package cn.muzisheng.lebo.exception;
/**
 * 商品类目异常
 * code: 506
 */
public class CategoryException extends RuntimeException{
    public CategoryException(String message) {
        super(message);
    }
    public CategoryException(String message, Throwable cause) {
        super(message, cause);
    }
    public CategoryException(Throwable cause) {
        super(cause);
    }
    public CategoryException() {
        super();
    }

}
