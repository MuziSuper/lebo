package cn.muzisheng.lebo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response<T> implements Serializable {
    private int status = 200;
    private Result<T> body = new Result<>();
    private MultiValueMap<String, String> headers = new HttpHeaders();

    public Response(Result<T> body) {
        this.body = body;
    }

    public ResponseEntity<Result<T>> value() {
        return new ResponseEntity<>(this.body,this.headers, this.status);
    }
    public ResponseEntity<T> valueOnlyData(){
        return new ResponseEntity<>(this.body.getData(),this.headers, this.status);
    }
    public void putHeader(String key, String value) {
        this.headers.add(key, value);
    }

    public void putHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            this.headers.add(entry.getKey(), entry.getValue());
        }
    }

    public void removeHeader(String key) {
        this.headers.remove(key);
    }

    public void clearHeader() {
        this.headers.clear();
    }

    public List<String> getHeader(String key) {
        return this.headers.get(key);
    }

    public MultiValueMap<String, String> getHeaders() {
        return new HttpHeaders(this.headers);
    }
    /**
     * 设置响应数据
     */
    public void setData(T data) {
        this.body.setData(data);
    }

    /**
     * 设置错误信息
     */
    public void setError(String error) {
        this.body.setError(error);
    }

    /**
     * 同时设置错误信息和数据
     */
    public void setBody(String error, T data) {
        this.body.setData(data);
        this.body.setError(error);
    }
}