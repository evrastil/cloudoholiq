package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.model.ServiceOffering;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.UUID;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class ServiceOfferingRepository extends GenericSearchRepository<ServiceOffering>{
    protected ServiceOfferingRepository() {
        super(ServiceOffering.class);
    }

    @SqlQuery("SELECT entity FROM serviceoffering WHERE to_tsvector(entity\\:\\:text) @@ plainto_tsquery(:query)")
    public abstract List<ServiceOffering> search(@Bind("query") String query);
}
