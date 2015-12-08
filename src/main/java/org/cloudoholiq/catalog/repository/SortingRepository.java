package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.model.search.Sorting;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class SortingRepository extends GenericRepository<Sorting>{
    public SortingRepository() {
        super(Sorting.class);
    }

}
