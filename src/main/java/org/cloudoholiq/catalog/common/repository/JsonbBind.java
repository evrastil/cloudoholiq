package org.cloudoholiq.catalog.common.repository;

import org.cloudoholiq.catalog.common.jackson.JsonUtils;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

@BindingAnnotation(JsonbBind.JsonbBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface JsonbBind {

    String value();

    public static class JsonbBinderFactory implements BinderFactory {
        @Override
        public Binder build(Annotation annotation) {
            return new Binder<JsonbBind, IEntity>() {
                @Override
                public void bind(SQLStatement<?> sql,
                                 JsonbBind accountBinder,
                                 IEntity json) {
                    sql.bindBySqlType(accountBinder.value(), JsonUtils.serialize(json), Types.OTHER);
                }
            };
        }
    }
}