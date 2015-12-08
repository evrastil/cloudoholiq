package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;
import java.util.UUID;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class FilterGroupRepository extends GenericRepository<FilterGroup>{
    public FilterGroupRepository() {
        super(FilterGroup.class);
    }

    @SqlQuery("SELECT entity FROM filtergroup WHERE id in (select filterGroupId from categoryfilter WHERE categoryId =: categoryId)")
    public abstract List<FilterGroup> findByCategoryId(@Bind("categoryId") UUID categoryId);

    @SqlUpdate("INSERT INTO categoryfilter (categoryId, filterGroupId) VALUES (:categoryId, :filterGroupId)")
    public abstract int bindCategoryFilter(@Bind("categoryId") UUID categoryId, @Bind("filterGroupId") UUID filterGroupId);

    @SqlUpdate("DELETE FROM categoryfilter WHERE filterGroupId =: filterGroupId")
    public abstract int deleteCategoryFilter(@Bind("filterGroupId") UUID filterGroupId);
}
