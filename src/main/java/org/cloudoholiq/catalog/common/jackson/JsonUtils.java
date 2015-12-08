package org.cloudoholiq.catalog.common.jackson;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class JsonUtils {

    static ObjectMapper relaxedMapper = new ObjectMapper(){{
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }};

    /**
     * Creates projection of <code>clazz</code> to <code>json</code>. The unknown elements are ignored. Validation is
     * not performed.
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws java.io.IOException
     */
    public static <T> T project(String json, Class<T> clazz) throws IOException {
        return relaxedMapper.readValue(json, clazz);
    }

    public static <T> T parse(String json, Class<T> clazz) throws IOException {
        return relaxedMapper.readValue(json, clazz);
    }

    public static <T> String serialize(T node) {
        try {
            return relaxedMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTree(InputStream is) throws IOException {
        return relaxedMapper.readTree(is);
    }

    public static JsonNode readTree(Reader reader) throws IOException {
        return relaxedMapper.readTree(reader);
    }

}
