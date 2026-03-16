package cn.muzisheng.lebo.entity;

import cn.muzisheng.lebo.handler.AccountStatusTypeHandler;
import cn.muzisheng.lebo.handler.EncryptionTypeHandler;
import cn.muzisheng.lebo.model.AccountStatusEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 继承基础模型，包含用户基本信息及状态
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(value = "open_id")
    @Id
    @Column(name = "open_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String openId;
    /**
     * 会话密钥
     */
    @Column(name = "session_key")
    @TableField(typeHandler = EncryptionTypeHandler.class)
    private String sessionKey;
    /**
     * 昵称
     */
    @Column(name = "nick_name",unique = true)
    @TableField(value = "nick_name")
    private String nickName;
    /**
     * 微信unionId
     */
    @Column(name = "union_id")
    @TableField(value = "union_id")
    private String unionId;
    /**
     * 密码
     */
    @Column(name = "password",length = 20)
    @TableField(value = "password")
    private String password;
    /**
     * 头像
     */
    @Column(name = "avatar")
    @TableField(value = "avatar")
    private String avatar;
    /**
     * 性别,0-保密，1-男，2-女
     */
    @Column(name = "gender")
    @TableField(value = "gender")
    private Integer gender;
    /**
     * 城市,默认省级行政区
     */
    @Column(name = "city")
    @TableField(value = "city")
    private String city;
    /**
     * 年龄
     */
    @Column(name = "age")
    @TableField(value = "age")
    private Integer age;
    /**
     * 生日,2023-01-01格式
     */
    @Column(name = "birthday")
    @TableField(value = "birthday")
    private String birthday;
    /**
     * 邮箱
     */
    @Column(name = "email")
    @TableField(value = "email")
    private String email;
    /**
     * 手机号
     */
    @Column(name = "phone")
    @TableField(value = "phone")
    private String phone;
    /**
     * 状态,0-正常，1-不活跃，2-暂停，3-封禁，4-注销
     */
    @Column(name = "status")
    @TableField(typeHandler = AccountStatusTypeHandler.class)
    private AccountStatusEnum status;
    /**
     * 上次登录时间
     */
    @Column(name = "last_login")
    @TableField(value = "last_login")
    private LocalDateTime lastLogin;
    /**
     * 创建时间
     */
    @Column(name = "gmt_created")
    @TableField(value = "gmt_created",fill = FieldFill.INSERT)
    private LocalDateTime gmtCreated;
    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    @TableField(value = "gmt_modified",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
    /**
     * 逻辑删除,0-正常，1-删除
     */
    @Column(name = "is_deleted")
    @TableField(value = "is_deleted")
    private Integer deleted;
}
