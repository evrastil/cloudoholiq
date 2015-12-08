package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.model.common.Blob;
import org.cloudoholiq.catalog.model.common.BlobMetadata;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.UUID;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class BlobRepository extends GenericRepository<Blob>{

    protected BlobRepository() {
        super(Blob.class);
    }

    @SqlQuery("SELECT * FROM blob WHERE cast(entity ->> 'entityId' as uuid) = :entityId")
    public abstract List<BlobMetadata> findByEntity(@Bind("entityId") UUID entityId);

    @SqlUpdate("DELETE FROM blob WHERE cast(entity ->> 'entityId' as uuid) = :entityId")
    public abstract void deleteByEntityId(@Bind("entityId") UUID entityId);

    @SqlQuery("SELECT * FROM blob WHERE cast(entity ->> 'entityId' as uuid) = :entityId and cast(entity ->> 'collection' as varchar) = :collection")
    public abstract List<Blob> findByEntity(UUID entityId, String collection);
}
