package cn.muzisheng.lebo.config;


import cn.muzisheng.lebo.constant.Constant;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;

/**
 * Token配置类，允许开发人员自行注TokenConfig实例，但只允许通过内部Builder类创建
 **/
@Data
@Configuration
public class TokenConfig {
    private String head=Constant.TOKEN_DEFAULT_SECRET_PREFIX;
    private Long expire=Constant.TOKEN_DEFAULT_EXPIRE_DAY;
    private String issue= Constant.TOKEN_DEFAULT_ISSUER;
    private String subject=Constant.TOKEN_DEFAULT_SUBJECT;
    private TokenConfig(){}
    @PostConstruct
    public void init(){
        if(head==null||head.isEmpty()){
            head=Constant.TOKEN_DEFAULT_SECRET_PREFIX;
        }
        if(expire==null||expire<=0){
            expire=Constant.TOKEN_DEFAULT_EXPIRE_DAY;
        }
        if(issue==null||issue.isEmpty()){
            issue=Constant.TOKEN_DEFAULT_ISSUER;
        }
        if(subject==null||subject.isEmpty()){
            subject=Constant.TOKEN_DEFAULT_SUBJECT;
        }

    }

}