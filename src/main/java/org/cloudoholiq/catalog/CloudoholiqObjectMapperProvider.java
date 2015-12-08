package org.cloudoholiq.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudoholiq.catalog.common.rest.hal.jackson.HalModule;

import javax.ws.rs.ext.ContextResolver;

/**
 * Created by vrastil on 11.5.2015.
 */
public class CloudoholiqObjectMapperProvider implements ContextResolver<ObjectMapper> {
    final ObjectMapper defaultObjectMapper;

    public CloudoholiqObjectMapperProvider() {
        defaultObjectMapper = createDefaultMapper();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return defaultObjectMapper;
    }

    private static ObjectMapper createDefaultMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new HalModule());

        return objectMapper;
    }
}
