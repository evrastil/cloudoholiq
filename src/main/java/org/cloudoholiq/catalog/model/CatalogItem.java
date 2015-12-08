package org.cloudoholiq.catalog.model;

import org.cloudoholiq.catalog.common.localization.LocalizedField;

import javax.validation.constraints.NotNull;

public abstract class CatalogItem extends Entity{
    private String name;
    private String key;
    private String description;
    private LocalizedField localizedName;
    private LocalizedField localizedDescription;
    private String icon;

    public CatalogItem() {
    }

    public CatalogItem(String name, String key, String description, String icon) {
        this.name = name;
        this.key = key;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalizedField getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(LocalizedField localizedName) {
        this.localizedName = localizedName;
    }

    public LocalizedField getLocalizedDescription() {
        return localizedDescription;
    }

    public void setLocalizedDescription(LocalizedField localizedDescription) {
        this.localizedDescription = localizedDescription;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
