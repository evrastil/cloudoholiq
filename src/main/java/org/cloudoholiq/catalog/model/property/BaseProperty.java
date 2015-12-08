package org.cloudoholiq.catalog.model.property;


public class BaseProperty {

    private String name;
    private Object value;
    private String unit;

    public BaseProperty() {
    }

    public BaseProperty(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public BaseProperty(String name, Object value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
