package org.cloudoholiq.catalog.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.cloudoholiq.catalog.model.Entity;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Filter extends Entity {
//    @HalLink(name = "self")
//    String self = String.format("/api/filters/%s", getId());

    //path to json field
    private String path;
    private String label;
    private Object query;
    private Expression expression;
    private Type type;
    //unique identifier, rest query param name
    private String key;
    //UUId of filter group
    UUID filterGroupId;

    public Filter() {
    }

    public Filter(String path, Object query, Expression expression, Type type, String key) {
        this.path = path;
        this.query = query;
        this.expression = expression;
        this.type = type;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getQuery() {
        return query;
    }

    public void setQuery(Object query) {
        this.query = query;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public UUID getFilterGroupId() {
        return filterGroupId;
    }

    public void setFilterGroupId(UUID filterGroupId) {
        this.filterGroupId = filterGroupId;
    }

    @Override
    public String selfPath() {
        return "/api/filters/items";
    }
}
