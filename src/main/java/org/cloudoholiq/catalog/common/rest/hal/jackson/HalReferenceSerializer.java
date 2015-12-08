package org.cloudoholiq.catalog.common.rest.hal.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Jackson serializer for HalReference
 * HalResourceSerializer will convert all links (including curies) into a map of HalReference
 * and it'll call JsonGenerator.writeObject for each of the HalReference, which will be serialize by this serializer
 */
public class HalReferenceSerializer extends StdSerializer<HalReference> {

    public HalReferenceSerializer(Class<HalReference> t) {
        super(t);
    }
    @Override
    public void serialize(HalReference value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        jgen.writeStartObject();
        if( value.getName() != null && value.getName().length() > 0 ) {
            jgen.writeStringField("name", value.getName());
        }
        jgen.writeStringField("href",value.getHref());
        if( value.getTitle() != null && value.getTitle().length() > 0 ) {
            jgen.writeStringField("title", value.getTitle());
        }
        if( value.isTemplated() ) {
            jgen.writeBooleanField("templated",true);
        }
        jgen.writeEndObject();
    }
}
