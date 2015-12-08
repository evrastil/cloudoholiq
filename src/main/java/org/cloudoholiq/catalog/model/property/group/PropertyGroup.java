package org.cloudoholiq.catalog.model.property.group;


import org.cloudoholiq.catalog.model.pricing.Pricing;
import org.cloudoholiq.catalog.model.property.Property;

import java.util.List;

public class PropertyGroup {

    private String description;

    private String label;

    private Pricing pricing;

    private List<Property> properties;

    public PropertyGroup() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
