package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.AccountStatusTypeHandler;
import cn.muzisheng.lebo.handler.EncryptionTypeHandler;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class User extends BaseModel{
    @Id
    private String openId;

    @TableField(typeHandler = EncryptionTypeHandler.class)
    private String sessionKey;
    @Column(name = "nick_name")
    @TableField(value = "nick_name")
    private String nickName;
    @Column(name = "union_id")
    @TableField(value = "union_id")
    private String unionId;
    private String avatar;
    private Integer gender;
    private String city;
    private Integer grade;
    private String birthday;
    private String email;
    private String phone;
    @TableField(typeHandler = AccountStatusTypeHandler.class)
    private AccountStatusEnum status;
    @Column(name = "last_login")
    @TableField(value = "last_login")
    private LocalDateTime last_login;
    @Transient
    @TableField(exist = false)
    private UserPoint point;

}
