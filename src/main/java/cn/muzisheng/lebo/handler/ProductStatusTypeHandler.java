package cn.muzisheng.lebo.handler;

import cn.muzisheng.lebo.model.ProductStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductStatusTypeHandler extends BaseTypeHandler<ProductStatusEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,ProductStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public ProductStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return ProductStatusEnum.fromCode(rs.getInt(columnName));
    }

    @Override
    public ProductStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return ProductStatusEnum.fromCode(rs.getInt(columnIndex));
    }

    @Override
    public ProductStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return ProductStatusEnum.fromCode(cs.getInt(columnIndex));

    }
}
