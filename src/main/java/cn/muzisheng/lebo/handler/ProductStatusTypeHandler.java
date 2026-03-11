package cn.muzisheng.lebo.handler;

import cn.muzisheng.lebo.model.ProductStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 商品状态类型处理器
 * 用于在 MyBatis 中实现 ProductStatusEnum 枚举与数据库整数类型之间的转换
 */
public class ProductStatusTypeHandler extends BaseTypeHandler<ProductStatusEnum> {

    /**
     * 将商品状态枚举设置到 PreparedStatement 参数中
     *
     * @param ps PreparedStatement 对象
     * @param i 参数索引位置（从 1 开始）
     * @param parameter ProductStatusEnum 类型的商品状态枚举参数
     * @param jdbcType JDBC 类型
     * @throws SQLException SQL 异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ProductStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    /**
     * 从 ResultSet 的指定列获取商品状态枚举值
     *
     * @param rs ResultSet 结果集对象
     * @param columnName 列名
     * @return ProductStatusEnum 商品状态枚举值
     * @throws SQLException SQL 异常
     */
    @Override
    public ProductStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ProductStatusEnum.fromCode(rs.getInt(columnName));
    }

    /**
     * 从 ResultSet 的指定列索引获取商品状态枚举值
     *
     * @param rs ResultSet 结果集对象
     * @param columnIndex 列索引位置（从 1 开始）
     * @return ProductStatusEnum 商品状态枚举值
     * @throws SQLException SQL 异常
     */
    @Override
    public ProductStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ProductStatusEnum.fromCode(rs.getInt(columnIndex));
    }

    /**
     * 从 CallableStatement 的指定列索引获取商品状态枚举值（用于存储过程）
     *
     * @param cs CallableStatement 存储过程调用对象
     * @param columnIndex 列索引位置（从 1 开始）
     * @return ProductStatusEnum 商品状态枚举值
     * @throws SQLException SQL 异常
     */
    @Override
    public ProductStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ProductStatusEnum.fromCode(cs.getInt(columnIndex));

    }
}
