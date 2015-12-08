package org.cloudoholiq.catalog;


import org.cloudoholiq.catalog.api.BlobStoreApi;
import org.cloudoholiq.catalog.api.CategoryApi;
import org.cloudoholiq.catalog.api.FilterApi;
import org.cloudoholiq.catalog.api.ServiceOfferingApi;
import org.cloudoholiq.catalog.api.VendorApi;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(BlobStoreApi.class);
        register(CategoryApi.class);
        register(FilterApi.class);
        register(ServiceOfferingApi.class);
        register(VendorApi.class);

        //for multipart
        register(MultiPartFeature.class);
        //jackson
        register(CloudoholiqObjectMapperProvider.class); //hal
        register(JacksonFeature.class);

    }
}
