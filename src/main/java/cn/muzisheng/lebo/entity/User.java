package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.AccountStatusTypeHandler;
import cn.muzisheng.lebo.handler.EncryptionTypeHandler;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 继承基础模型，包含用户基本信息及状态
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class User extends BaseModel {
    @Id
    private String openId;
    /**
     * 会话密钥
     */
    @TableField(typeHandler = EncryptionTypeHandler.class)
    private String sessionKey;
    /**
     * 昵称
     */
    @Column(name = "nick_name")
    @TableField(value = "nick_name")
    private String nickName;
    /**
     * 微信unionId
     */
    @Column(name = "union_id")
    @TableField(value = "union_id")
    private String unionId;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 城市
     */
    private String city;
    /**
     * 年级
     */
    private Integer grade;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 状态
     */
    @TableField(typeHandler = AccountStatusTypeHandler.class)
    private AccountStatusEnum status;
    /**
     * 上次登录时间
     */
    @Column(name = "last_login")
    @TableField(value = "last_login")
    private LocalDateTime lastLogin;
    /**
     * 用户积分, 非数据库字段
     */
    @Transient
    @TableField(exist = false)
    private UserPoint point;

}
