package org.cloudoholiq.catalog.model;

public class Vendor extends CatalogItem{

//    @HalLink(name="self")
//    String self = String.format("/api/vendors/%s",this.id);

    String homePageURL;

    public String getHomePageURL() {
        return homePageURL;
    }

    public void setHomePageURL(String homePageURL) {
        this.homePageURL = homePageURL;
    }

    @Override
    public String selfPath() {
        return "/api/vendors";
    }
}
