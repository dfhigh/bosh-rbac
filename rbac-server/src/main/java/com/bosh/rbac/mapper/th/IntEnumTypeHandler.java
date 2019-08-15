package com.bosh.rbac.mapper.th;

import com.bosh.rbac.model.Action;
import com.bosh.rbac.model.EntityType;
import com.bosh.rbac.model.IntEnum;
import com.bosh.rbac.model.ResourceType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static org.mib.common.validator.Validator.validateObjectNotNull;

@MappedJdbcTypes({JdbcType.INTEGER})
@MappedTypes({Action.class, EntityType.class, ResourceType.class})
public class IntEnumTypeHandler<E extends Enum<?> & IntEnum> extends BaseTypeHandler<IntEnum> {

    private final Class<E> type;

    public IntEnumTypeHandler(final Class<E> type) {
        validateObjectNotNull(type, "enum type");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, IntEnum intEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, intEnum.getValue());
    }

    @Override
    public IntEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return resultSet.wasNull() ? null : valueOf(resultSet.getInt(s));
    }

    @Override
    public IntEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resultSet.wasNull() ? null : valueOf(resultSet.getInt(i));
    }

    @Override
    public IntEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return callableStatement.wasNull() ? null : valueOf(callableStatement.getInt(i));
    }

    private E valueOf(int value) {
        return Arrays.stream(type.getEnumConstants()).filter(e -> e.getValue() == value).findFirst().orElse(null);
    }
}
