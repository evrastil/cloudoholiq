package org.cloudoholiq.catalog.common.localization;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudoholiq.catalog.common.exception.InvalidClientDataException;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

@JsonDeserialize(using = LocalizedFieldDeserializer.class )
public class LocalizedField {

    private Map<String, String> data;

    @JsonAnyGetter
    public Map<String, String> getData() {
        return data;
    }

    public LocalizedField() {
    }

    public LocalizedField(Map<String, String> data) {
        setData(data);
    }

    public void setData(Map<String, String> data) {
        for (String s : data.keySet()) {
            Locale lo = parseLocale(s);
            if (!isValid(lo)) {
              throw new InvalidClientDataException(String.format("Invalid locale %s", s));
            }
        }
        for (String s : data.values()) {
            if(s== null){
                throw new InvalidClientDataException("Value can not be null");
            } else if(s.isEmpty()){
                throw new InvalidClientDataException("Value can not be empty");
            }
        }
        this.data = data;
    }

    @JsonIgnore
    private boolean isValid(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    @JsonIgnore
    private Locale parseLocale(String locale) {
        String[] parts = locale.split("_");
        switch (parts.length) {
            case 3: return new Locale(parts[0], parts[1], parts[2]);
            case 2: return new Locale(parts[0], parts[1]);
            case 1: return new Locale(parts[0]);
            default: throw new InvalidClientDataException("Invalid locale: " + locale);
        }
    }
}
