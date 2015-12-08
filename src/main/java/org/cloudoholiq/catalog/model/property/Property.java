package org.cloudoholiq.catalog.model.property;


import org.cloudoholiq.catalog.model.pricing.Pricing;
import org.cloudoholiq.catalog.model.pricing.Pricing;
import org.cloudoholiq.catalog.model.property.group.PropertyGroup;

public class Property extends BaseProperty {

    private Pricing pricing;

    boolean tagged;

    public Property() {
    }

    public Property(String name, Object value, String unit) {
        super(name, value, unit);
    }

    public Property(String name, Object value) {
        super(name, value);
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }
}
