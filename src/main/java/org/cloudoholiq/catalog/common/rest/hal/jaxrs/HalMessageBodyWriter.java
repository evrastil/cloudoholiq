package org.cloudoholiq.catalog.common.rest.hal.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudoholiq.catalog.common.rest.hal.jackson.HalModule;
import org.cloudoholiq.catalog.common.rest.hal.jackson.HalResource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/hal+json")
public class HalMessageBodyWriter implements MessageBodyWriter<HalResource> {
    private static ObjectMapper objectMapper;

    public HalMessageBodyWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        objectMapper.registerModule(new HalModule());
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAssignableFrom(HalResource.class) ;
    }

    @Override
    public long getSize(HalResource halResource, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(HalResource halResource, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        objectMapper.writeValue(entityStream,halResource);
    }
}
