package org.cloudoholiq.catalog.common.rest.hal.jackson;


/**
 * links and curies will be converted into HalReference before serialization
 */
public class HalReference {

    public HalReference() {
    }

    public HalReference(String name, String href, String title, boolean templated) {
        this.name = name;
        this.href = href;
        this.title = title;
        this.templated = templated;
    }

    // only use in curies
    String name;
    String href;
    // curies has no title
    String title;
    boolean templated;

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
    }

    public String getTitle() {
        return title;
    }

    public boolean isTemplated() {
        return templated;
    }

    /**
     * convinent method to create a link
     * @param href
     * @param title
     * @param templated
     * @return
     */
    public HalReference createLink(String href, String title, boolean templated ) {
        return new HalReference(null,href,title,templated);
    }

}
