package org.cloudoholiq.catalog.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vrastil on 30.5.2015.
 */
public class JsonBQueryParam {

    Map<UUID, List<Map<String, Object>>> jsonBQueries = new LinkedHashMap<>();
    String jsonCollectionName;
    String jsonSearchCollectionName;

    public JsonBQueryParam(String jsonSearchCollectionName, String jsonCollectionName) {
        this.jsonCollectionName = jsonCollectionName;
        this.jsonSearchCollectionName = jsonSearchCollectionName;
    }

    public Map<UUID, List<Map<String, Object>>> getJsonBQueries() {
        return jsonBQueries;
    }

    public List<Map<String, Object>> getJsonBQueriesAsList() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<Map<String, Object>> maps : jsonBQueries.values()) {
            result.addAll(maps);
        }
        return result;
    }

    public String getJsonCollectionName() {
        return jsonCollectionName;
    }

    public String getJsonSearchCollectionName() {
        return jsonSearchCollectionName;
    }
}
