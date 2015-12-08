package org.cloudoholiq.catalog.model;


public class Category extends CatalogItem{

    private String label;

    public Category() {
    }

    public Category(String name, String key, String description, String label, String icon) {
        super(name, key, description, icon);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String selfPath() {
        return "/api/categories";
    }

}
