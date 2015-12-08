package org.cloudoholiq.catalog.common.localization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.HashMap;

public class LocalizedFieldDeserializer extends JsonDeserializer<LocalizedField> {
    @Override
    public LocalizedField deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        HashMap data = jp.readValueAs(HashMap.class);
        return new LocalizedField(data);
    }
}
