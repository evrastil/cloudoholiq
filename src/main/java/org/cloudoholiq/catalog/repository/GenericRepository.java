package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.common.repository.JsonbBind;
import org.cloudoholiq.catalog.model.Entity;
import org.cloudoholiq.catalog.repository.exception.OptimisticLockException;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.UUID;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class GenericRepository<T extends Entity>{

    private Class<T> clazz;

    protected GenericRepository(Class<T> clazz) {
        this.clazz = clazz;
    }
    public T findById(UUID id){
        return findById(id, getClazzName());
    }

    public T findByKey(String key){
        return findByKey(key, getClazzName());
    }

    public List<T> findAll(){
        return findAll(getView(), getClazzName());
    }

    protected String getView() {
        return "entity";
    }

    public void delete(UUID id){
        delete(id, getClazzName());
    }

    private String getClazzName() {
        return clazz.getSimpleName().toLowerCase();
    }

    @Transaction
    public T create(T entity){
        entity.setTimestamp(nextTimestamp());
        entity.setId(UUID.randomUUID());
        insert(entity.getId(), entity, getClazzName());
        return entity;
    }

    @Transaction
    public T update(T entity) throws OptimisticLockException {
        int timestamp = entity.getTimestamp();
        entity.setTimestamp(nextTimestamp());
        if(update(entity.getId(), entity, timestamp, getClazzName()) == 0) {
            throw new OptimisticLockException(
                    String.format("The Entity with id=%s has been updated by different transaction", entity.getId()));
        }
        return entity;
    }

    @SqlQuery("SELECT nextval('timestamp_seq')")
    abstract int nextTimestamp();

    @SqlQuery("SELECT entity FROM <table> WHERE id = :id")
    public abstract T findById(@Bind("id") UUID id, @Define("table") String table);

    @SqlQuery("SELECT entity FROM <table> WHERE cast(entity  ->> 'key' as varchar) = :key")
    public abstract T findByKey(@Bind("key") String key, @Define("table") String table);


    @SqlQuery("SELECT <view> FROM <table>")
    public abstract List<T> findAll(@Define("view") String view, @Define("table") String table);

    @SqlUpdate("DELETE FROM <table> WHERE id = :id")
    public abstract int delete(@Bind("id") UUID id, @Define("table") String table);

    @SqlUpdate("INSERT INTO <table> (id, entity) VALUES (:id, :entity)")
    abstract int insert(@Bind("id") UUID id, @JsonbBind("entity") T entity, @Define("table") String table);

    @SqlUpdate("UPDATE <table> SET entity=:entity where id = :id and cast(entity ->> 'timestamp' as int) = :timestamp")
    public abstract int update(@Bind("id") UUID id, @JsonbBind("entity") T entity, @Bind("timestamp") Integer timestamp, @Define("table") String table);
}
