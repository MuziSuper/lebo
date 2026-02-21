package cn.muzisheng.lebo.handler;

import cn.muzisheng.lebo.exception.GeneralException;
import cn.muzisheng.lebo.utils.AESUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 加密解密类型处理器
 * 
 * 用于 MyBatis 自动加密解密数据库字段
 * - 存入数据库时自动加密
 * - 从数据库读取时自动解密
 * 
 * 使用示例：
 * <pre>
 * @TableField(typeHandler = EncryptionTypeHandler.class)
 * private String encryptionSessionKey;
 * </pre>
 * 
 * @author MuziSheng
 * @date 2024-02-08
 */
public class EncryptionTypeHandler extends BaseTypeHandler<String> {

    /**
     * 设置非空参数（存入数据库时自动加密）
     * 
     * @param ps PreparedStatement 对象
     * @param i 参数位置
     * @param parameter 原始明文字符串
     * @param jdbcType JDBC类型
     * @throws SQLException SQL异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将明文加密后存入数据库
            String encrypted = AESUtil.encrypt(parameter);
            ps.setString(i, encrypted);
        } catch (Exception e) {
            throw new GeneralException("加密失败", e);
        }
    }

    /**
     * 根据列名获取结果（从数据库读取时自动解密）
     * 
     * @param rs ResultSet 对象
     * @param columnName 列名
     * @return 解密后的明文字符串
     * @throws SQLException SQL异常
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String encrypted = rs.getString(columnName);
        if (encrypted == null) {
            return null;
        }
        try {
            // 从数据库读取密文，解密后返回
            return AESUtil.decrypt(encrypted);
        } catch (Exception e) {
            throw new GeneralException("解密失败", e);
        }
    }

    /**
     * 根据列索引获取结果（从数据库读取时自动解密）
     * 
     * @param rs ResultSet 对象
     * @param columnIndex 列索引
     * @return 解密后的明文字符串
     * @throws SQLException SQL异常
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String encrypted = rs.getString(columnIndex);
        if (encrypted == null) {
            return null;
        }
        try {
            // 从数据库读取密文，解密后返回
            return AESUtil.decrypt(encrypted);
        } catch (Exception e) {
            throw new GeneralException("解密失败", e);
        }
    }

    /**
     * 从存储过程中获取结果（从数据库读取时自动解密）
     * 
     * @param cs CallableStatement 对象
     * @param columnIndex 列索引
     * @return 解密后的明文字符串
     * @throws SQLException SQL异常
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encrypted = cs.getString(columnIndex);
        if (encrypted == null) {
            return null;
        }
        try {
            // 从存储过程读取密文，解密后返回
            return AESUtil.decrypt(encrypted);
        } catch (Exception e) {
            throw new GeneralException("解密失败", e);
        }
    }
}
