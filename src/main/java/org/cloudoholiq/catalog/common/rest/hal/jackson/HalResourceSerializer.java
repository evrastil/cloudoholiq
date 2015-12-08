package org.cloudoholiq.catalog.common.rest.hal.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.cloudoholiq.catalog.common.rest.hal.jackson.HalReflectionHelper.*;

/**
 * Jackson Serializer for classes implemented HalResource
 * For _links, either annotated with @HalLink or the field is a Collections of HalReference
 * For curies, annotated with @HalCuries, each curie have to be in a different field, serializer will combine them into an array
 * For _embedded, annotated with @HalEmbedded, if the class implemented HalResource, the _links, curies and _embedded will be honered too
 * All fields not annotated with HalEmbedded, HalCuries, HalLink (including Collections of HalReference),
 *   can be annotated with JsonProperty to change the field name
 */
public class HalResourceSerializer extends StdSerializer<HalResource> {

    public HalResourceSerializer(Class<HalResource> t) {
        super(t);
    }

    /**
     * called by Jackson when serializing class implemented HalResource
     * @param resource
     * @param jgen
     * @param provider
     * @throws IOException
     * @throws JsonProcessingException
     */
    @Override
    public void serialize(HalResource resource, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

        JsonInclude.Include include = provider.getConfig().getSerializationInclusion();
        Map<String, Collection<HalReference>> links = new HashMap<>();
        Collection<HalReference> curies = new ArrayList<>();
        List<Field> normalFields = new ArrayList<>();
        Map<String, Field> embeddedFields = new HashMap<>();

        List<Field> fields = getAllFields(resource.getClass());
        for( Field field : fields ) {
            /**
             * somehow inner class has a reference to it's parent causing infinite loops
             */
            if( field.getName().startsWith("this$") ) continue;
            /**
             * if the field is annotated with HalLink or a Collection of HalReference
             * convert the field into HalReference and add everything to a hash map
             */
            Map<String, Collection<HalReference>> fieldLinks = getLink(field, resource, include);
            if( fieldLinks != null ) {
                links.putAll(fieldLinks);
                continue;
            }
            /**
             * Curies
             */
            HalReference curie = getCuries(field,resource,include);
            if( curie != null ) {
                curies.add(curie);
                continue;
            }
            /**
             * embedded fields
             */
            String embeddedName = getEmbedded(field,resource);
            if( embeddedName != null ) {
                embeddedFields.put(embeddedName,field);
                continue;
            }
            /**
             * for normal fields, simply add that to the array list
             */
            normalFields.add(field);
        }
        // curies is just links
        if( curies.size() > 0 )
            links.put("curies", curies );

        jgen.writeStartObject();
        serializeLinks(jgen,links);
        serializeNormalFields(jgen, normalFields, resource, include);
        serializedEmbedded(jgen, embeddedFields, resource, include, provider);
        jgen.writeEndObject();
    }

    @SuppressWarnings("rawtypes")
	private void serializedEmbedded(JsonGenerator jgen, Map<String, Field> embedded, HalResource resource, JsonInclude.Include include, SerializerProvider provider) throws IOException {
        if( embedded.size() == 0 ) return;
        jgen.writeFieldName("_embedded");
        jgen.writeStartObject();
        for( String key : embedded.keySet() ) {
            Field fields = embedded.get(key);
            Object value = getValue(fields,resource);
            if( Collection.class.isAssignableFrom(value.getClass()) ) {
                Collection values = (Collection)value;
                if( (!(include==JsonInclude.Include.NON_EMPTY||include==JsonInclude.Include.NON_NULL)||values!=null) && (include!= JsonInclude.Include.NON_EMPTY || values.size() > 0) ) {
                    jgen.writeFieldName(key);
                    jgen.writeStartArray();
                    for (Object v : values) {
                        if( provider instanceof DefaultSerializerProvider.Impl ) {
                            DefaultSerializerProvider defaultSerializerProvider = (DefaultSerializerProvider)provider;
                            defaultSerializerProvider.serializeValue(jgen,v);
                        }else {
                            jgen.writeObject(v);
                        }
                    }
                    jgen.writeEndArray();
                }
            } else {
                if( !(include == JsonInclude.Include.NON_NULL || include == JsonInclude.Include.NON_EMPTY) || value != null ) {
                    jgen.writeObjectField(key,value);
                }
            }
        }
        jgen.writeEndObject();
    }

    private void serializeNormalFields(JsonGenerator jgen, List<Field> normalFields, HalResource resource, JsonInclude.Include include ) throws IOException {
        for ( Field field : normalFields ) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            String name = ( jsonProperty != null ) ? jsonProperty.value() : field.getName();
            Object value = getValue(field, resource);
            boolean writeObject = !(include==JsonInclude.Include.NON_EMPTY||include==JsonInclude.Include.NON_NULL) && value != null;
            if (writeObject && Collection.class.isAssignableFrom(value.getClass())) {
                Collection values = (Collection)value;
                writeObject &= !(include==JsonInclude.Include.NON_EMPTY) || values.size()>0;
            }
            if( writeObject )
                jgen.writeObjectField(name, getValue(field, resource));
        }
    }
    private void serializeLinks(JsonGenerator jgen, Map<String, Collection<HalReference>> links) throws IOException {
        jgen.writeFieldName("_links");
        jgen.writeStartObject();
        for( String key : links.keySet() ) {
            Collection<HalReference> references = links.get(key);
            if( references.size() == 1 ) {
                jgen.writeObjectField(key, references.iterator().next());
            } else {
                jgen.writeArrayFieldStart(key);
                for( HalReference reference : references ) {
                    jgen.writeObject(reference);
                }
                jgen.writeEndArray();
            }

        }
        jgen.writeEndObject();
    }

}
