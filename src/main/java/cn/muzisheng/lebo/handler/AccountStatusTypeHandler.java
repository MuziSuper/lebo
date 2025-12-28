package cn.muzisheng.lebo.handler;


import cn.muzisheng.lebo.model.AccountStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountStatusTypeHandler extends BaseTypeHandler<AccountStatusEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AccountStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public AccountStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return AccountStatusEnum.fromCode(rs.getInt(columnName));
    }

    @Override
    public AccountStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return AccountStatusEnum.fromCode(rs.getInt(columnIndex));
    }

    @Override
    public AccountStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return AccountStatusEnum.fromCode(cs.getInt(columnIndex));

    }
}
