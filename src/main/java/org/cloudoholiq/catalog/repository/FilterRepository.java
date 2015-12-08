package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.model.search.Filter;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.UUID;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class FilterRepository extends GenericRepository<Filter>{

    public FilterRepository() {
        super(Filter.class);
    }

    @SqlQuery("SELECT * FROM filter WHERE cast(entity  ->> 'filterGroupId' as uuid) = :filterGroupId")
    public abstract List<Filter> findByFilterGroupId(@Bind("filterGroupId") UUID filterGroupId);

}
