package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.common.repository.JsonMapperFactory;
import org.cloudoholiq.catalog.model.Vendor;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(JsonMapperFactory.class)
public abstract class VendorRepository extends GenericSearchRepository<Vendor>{

    protected VendorRepository() {
        super(Vendor.class);
    }
}
