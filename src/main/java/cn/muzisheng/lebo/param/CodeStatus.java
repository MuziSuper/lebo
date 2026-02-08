package cn.muzisheng.lebo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CodeStatus {
    private HttpStatus httpStatus;
    private Integer code;
}
