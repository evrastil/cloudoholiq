package org.cloudoholiq.catalog.model;

import org.cloudoholiq.catalog.model.pricing.Pricing;
import org.cloudoholiq.catalog.model.property.group.PropertyGroup;

import java.util.List;
import java.util.UUID;

public class ServiceOffering extends CatalogItem {

//    @HalLink(name="self")
//    String self = String.format("/api/services/%s",getId());
//    @HalLink(name="vendor:get")
//    String vendorHref = String.format("/api/vendors/%s",getVendor());
//    @HalLink(name="category:get")
//    String categoryHref = String.format("/api/categories/%s",getCategory());

    private UUID vendor;
    private UUID category;
    private Pricing pricing;

    private List<PropertyGroup> propertyGroups;

    public ServiceOffering() {
    }

    public UUID getVendor() {
        return vendor;
    }

    public void setVendor(UUID vendor) {
        this.vendor = vendor;
    }

    public UUID getCategory() {
        return category;
    }

    public void setCategory(UUID category) {
        this.category = category;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public List<PropertyGroup> getPropertyGroups() {
        return propertyGroups;
    }

    public void setPropertyGroups(List<PropertyGroup> propertyGroups) {
        this.propertyGroups = propertyGroups;
    }

    @Override
    public String selfPath() {
        return "/api/services";
    }
}
