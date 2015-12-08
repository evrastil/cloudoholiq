package org.cloudoholiq.catalog.common.repository;

import org.cloudoholiq.catalog.common.jackson.JsonUtils;
import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This factory automatically maps jdoc column in a table to corresponding data type specified as a return type of
 * a DAO functio
 */
public class JsonMapperFactory implements ResultSetMapperFactory {

    public Map<Class, String> type2columnName = new HashMap<Class, String>();

    public JsonMapperFactory() {
        type2columnName.put(IEntity.class, "entity");
    }

    public JsonMapperFactory(Map<Class, String> type2columnName) {
        this.type2columnName = type2columnName;
    }

    public Map<Class, String> getType2columnName() {
        return type2columnName;
    }

    @Override
    public boolean accepts(Class type, StatementContext ctx) {
        for(Class t : type2columnName.keySet()) {
            if(t.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResultSetMapper mapperFor(Class type, StatementContext ctx) {
        for(Map.Entry<Class, String> e : type2columnName.entrySet()) {
            if(e.getKey().isAssignableFrom(type)) {
                return new JsonMapper(type, e.getValue());
            }
        }
        throw new IllegalStateException("Should never happen");
    }

    public static final class JsonMapper implements ResultSetMapper {

        Class clazz;
        String columnName;

        public JsonMapper(Class clazz, String columnName) {
            this.clazz = clazz;
            this.columnName = columnName;
        }

        @Override
        public Object map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {

            String jdoc = resultSet.getString(columnName);
            try {
                return JsonUtils.parse(jdoc, clazz);
            } catch (IOException e) {
                throw new SQLException(e);
            }
        }
    }
}
