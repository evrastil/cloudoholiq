package org.cloudoholiq.catalog.common.rest.hal.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class HalModule extends SimpleModule {

    public HalModule() {
        super("HalModule");
        addSerializer(new HalResourceSerializer(HalResource.class));
        addSerializer(new HalReferenceSerializer(HalReference.class));
    }
}
