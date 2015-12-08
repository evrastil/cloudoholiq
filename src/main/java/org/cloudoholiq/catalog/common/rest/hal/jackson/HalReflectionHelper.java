package org.cloudoholiq.catalog.common.rest.hal.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudoholiq.catalog.common.rest.hal.annotation.HalCuries;
import org.cloudoholiq.catalog.common.rest.hal.annotation.HalEmbedded;
import org.cloudoholiq.catalog.common.rest.hal.annotation.HalLink;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * helper class for HalResourceSerializer
 */
public class HalReflectionHelper {

    public static String getEmbedded(Field field, HalResource resource) {
        HalEmbedded halEmbeddedAnnotation = field.getAnnotation(HalEmbedded.class);
        if( halEmbeddedAnnotation != null ) {
            String name = halEmbeddedAnnotation.name();
            if( name.length() == 0 ) { //no need to do null check, annotation value cannot be null
                name = field.getName();
            }
            return name;
        }
        return null;
    }

    public static HalReference getCuries(Field field, HalResource resource, JsonInclude.Include include) {
        HalCuries halCuriesAnnotation = field.getAnnotation(HalCuries.class);
        if( halCuriesAnnotation != null ) {
            String name = halCuriesAnnotation.name();
            if( name.length() == 0 ) {
                name = field.getName();
            }
            String href = getStringValue(field,resource);
            if( !(include == Include.NON_NULL||include == Include.NON_EMPTY) || href != null )
                return new HalReference(name, href,null,halCuriesAnnotation.templated());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public static Map<String,Collection<HalReference>> getLink(Field field, HalResource resource, JsonInclude.Include include){
        Map<String,Collection<HalReference>> referenceMap = new HashMap<>();
        HalLink halLinkAnnotation = field.getAnnotation(HalLink.class);
        if( halLinkAnnotation != null ){
            String name = halLinkAnnotation.name();
            if( name == null || name.length() == 0 ) {
                name = field.getName();
            }
            String title = halLinkAnnotation.title();
            if( title.length() == 0 ) title = null;
            String href = getStringValue(field, resource);
            if( !(include == Include.NON_NULL||include == Include.NON_EMPTY) || href != null )
                referenceMap.put(name, Collections.singletonList( new HalReference(null,href,title,halLinkAnnotation.templated())) );
        }
        else {
            if(Collection.class.isAssignableFrom(field.getType())){
                ParameterizedType gType = (ParameterizedType) field.getGenericType();
                Type[] actualTypeArguments = gType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    if (HalReference.class.equals(actualTypeArguments[0])) {
                        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                        String name = ( jsonProperty != null ) ? jsonProperty.value() : field.getName();
                        Collection<HalReference> resources = getValue(field,resource,null);
                        if( (!(include == Include.NON_NULL||include == Include.NON_EMPTY) || resources != null ) && ( include != Include.NON_EMPTY || resources.size() > 0 ) ) {
                            referenceMap.put(name,resources);
                        }
                    } else {
                        // collection but not HalReference
                        return null;
                    }

                }
            } else {
                // not annotated nor HalReference
                return null;
            }
        }
        return referenceMap;
    }

    /**
     * return the value of the field, if it's not String, call toString
     * @param field
     * @param resource
     * @return
     */
    private static String getStringValue(Field field, HalResource resource) {
        try {
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            Object value = field.get(resource);

            if (!accessible) {
                field.setAccessible(false);
            }
            return ( value instanceof String ) ? (String)value : value.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Object getValue(Field field, HalResource resource) {
        return getValue(field, resource, null);
    }
    /**
     * Field type has to be the same as default value,
     * @param field
     * @param resource
     * @param defaultValue
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T getValue(Field field, HalResource resource, T defaultValue ) {
        try {
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            Object value = field.get(resource);

            if (!accessible) {
                field.setAccessible(false);
            }
            return (T)value;
        } catch (Exception ex) {
            return defaultValue;
        }
    }
    /**
     * return all fields in a class
     * @param type Class type
     * @return list of fields
     */
    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }
}
