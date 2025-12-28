package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.AccountStatusTypeHandler;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@PearObject(
        desc = "User Information.",
        path = "/user",
        pluralName = "users",
        group = "user"
)
@Entity
public class User extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nick_name")
    @TableField(value = "nick_name")
    private String nickName;
    @Column(unique = true, nullable = false,name = "wx_id")
    @TableField(value = "wx_id")
    private String wxId;
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

}
