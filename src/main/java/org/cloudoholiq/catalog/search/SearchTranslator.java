package org.cloudoholiq.catalog.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudoholiq.catalog.model.search.Expression;
import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.model.search.Sort;
import org.cloudoholiq.catalog.model.search.Sorting;
import org.cloudoholiq.catalog.model.search.Type;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class SearchTranslator {

    /**
     * Creates aliasFields, filters, sorting, pagination string expressions to be user as search for json field.
     *
     * @param tableAlias
     * @param searchQueryParam
     * @return GenericSearch
     */
    public GenericSearch createGenericSearch(String table, String tableAlias, SearchQueryParam searchQueryParam) {
        String pagination = "", filters = "", sorting = "", sortingFields = "";
        List<String> expressionFields = new ArrayList<>();
        List<Filter> filtersParam = searchQueryParam.getFilters();
        if (filtersParam != null && !filtersParam.isEmpty()) {
            filters = getFilters(expressionFields, filtersParam);
        }
        List<Sorting> sortingParam = searchQueryParam.getSorting();
        if (sortingParam != null && !sortingParam.isEmpty()) {
            sorting = getSorting(expressionFields, sortingParam);
            sortingFields = getSortingFields(expressionFields, sortingParam);
        }
        Pagination paginationParam = searchQueryParam.getPagination();
        if (paginationParam != null) {
            pagination = "limit " + paginationParam.getLimit() + " offset " + paginationParam.getOffset();
        }

        return new GenericSearch(createCollectionCasts(table, tableAlias, expressionFields), filters, sorting, sortingFields, pagination);
    }

    /**
     * Build filters clauses.
     *
     * @param expressionFields
     * @param sortingParam
     * @return String
     */
    private String getSorting(List<String> expressionFields, List<Sorting> sortingParam) {
        StringBuilder sortingSB = new StringBuilder();
        for (int i = 0; i < sortingParam.size(); i++) {
            sortingSB.append(", ");
            Sorting sort = sortingParam.get(i);
            String orderByExpression = sort.getOrderBy();
            if (isCollectionExpression(orderByExpression)) {
                if (!expressionFields.contains(orderByExpression)) {
                    expressionFields.add(orderByExpression);
                }
                String orderBy = getFieldWithoutCollectionMarker(orderByExpression.substring(orderByExpression.lastIndexOf("["), orderByExpression.length()));
                String alias = orderBy.split("\\.")[0];
                orderBy = orderBy.replaceFirst(alias, "");
                orderBy = orderBy.substring(1, orderBy.length());
                sortingSB.append(getSortingExpression(alias, sort.getType(), orderBy, sort.getSort()));
            } else {
                sortingSB.append(getSortingExpression("entity", sort.getType(), sort.getOrderBy(), sort.getSort()));
            }
        }
        return sortingSB.toString();
    }

    private String getSortingFields(List<String> expressionFields, List<Sorting> sortingParam) {
        StringBuilder sortingSB = new StringBuilder();
        for (int i = 0; i < sortingParam.size(); i++) {
            sortingSB.append(", ");
            Sorting sort = sortingParam.get(i);
            String orderByExpression = sort.getOrderBy();
            if (isCollectionExpression(orderByExpression)) {
                if (!expressionFields.contains(orderByExpression)) {
                    expressionFields.add(orderByExpression);
                }
                String orderBy = getFieldWithoutCollectionMarker(orderByExpression.substring(orderByExpression.lastIndexOf("["), orderByExpression.length()));
                String alias = orderBy.split("\\.")[0];
                orderBy = orderBy.replaceFirst(alias, "");
                orderBy = orderBy.substring(1, orderBy.length());
                sortingSB.append(getSortingExpressionField(alias, sort.getType(), orderBy, sort.getSort()));
            } else {
                sortingSB.append(getSortingExpressionField("entity", sort.getType(), sort.getOrderBy(), sort.getSort()));
            }
        }
        return sortingSB.toString();
    }

    /**
     * Build filters clauses.
     *
     * @param expressionFields
     * @param filtersParam
     * @return String
     */
    private String getFilters(List<String> expressionFields, List<Filter> filtersParam) {
        StringBuilder result = new StringBuilder();
        List<StringBuilder> sbFilterList = new ArrayList<>();
        result.append(" AND ");

        List<Filter> jsonBFilters = new ArrayList<>();
        for (Filter filter : filtersParam) {
            String filedNameExpression = filter.getPath();
            String fieldName = filter.getPath();
            String alias = "entity";
            if (isCollectionExpression(filedNameExpression)) {
                if (!expressionFields.contains(filedNameExpression)) {
                    expressionFields.add(filedNameExpression);
                }
                fieldName = getFieldWithoutCollectionMarker(filedNameExpression.substring(filedNameExpression.lastIndexOf("["),
                        filedNameExpression.length()));
                alias = fieldName.split("\\.")[0];
                fieldName = fieldName.replaceFirst(alias, "");
            }

            if (filter.getType() == Type.JSONB) {
                jsonBFilters.add(filter);
            } else {
                getFilter(sbFilterList, filter, fieldName, alias);
            }
        }
        if (!jsonBFilters.isEmpty()) {
            List<JsonBQueryParam> jsonBQueryParams = new LinkedList<>();
//            Collections.sort(jsonBFilters, new Comparator<Filter>() {
//                @Override
//                public int compare(Filter o1, Filter o2) {
//                    return o1.getFilterGroupId().compareTo(o2.getFilterGroupId());
//                }
//            });
            for (Filter filter : jsonBFilters) {
                 getJsonBQueryParams(jsonBQueryParams, filter);
            }
            addJsonBFilters(sbFilterList, jsonBQueryParams);
        }
        for (int i = 0; i < sbFilterList.size(); i++) {
            StringBuilder f = sbFilterList.get(i);
            result.append(f);
            if (i < sbFilterList.size() - 1) {
                result.append(" AND ");
            }
        }
        return result.toString();
    }

    private void addJsonBFilters(List<StringBuilder> sbFilterList, List<JsonBQueryParam> jsonBQueryParams) {
        StringBuilder jsonFilterSB = new StringBuilder();
        if (jsonBQueryParams.size() > 1) {
            addMultiJsonBFilter(jsonBQueryParams, jsonFilterSB);
        } else {
            addJsonBFilter(jsonFilterSB, jsonBQueryParams.stream().findFirst().get());
        }
        sbFilterList.add(jsonFilterSB);
    }

    private void getFilter(List<StringBuilder> sbFilterList, Filter filter, String fieldName, String alias) {
        StringBuilder castFilterSB = new StringBuilder();
        @SuppressWarnings("unchecked")
        Set<Object> querySet = (Set) filter.getQuery();
        if (querySet.size() > 1) {
            addMultiFilter(castFilterSB, filter, querySet, fieldName, alias);
        } else {
            addFilter(castFilterSB, filter, fieldName, alias, querySet.stream().findFirst().get());
        }
        sbFilterList.add(castFilterSB);
    }

    private void getJsonBQueryParams(List<JsonBQueryParam> jsonBQueryParams, Filter filter) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> queries = (List<Map<String, Object>>) filter.getQuery();

        String filedNameExpression = filter.getPath();
        String fieldName = getFieldWithoutCollectionMarker(filedNameExpression.substring(filedNameExpression.lastIndexOf("["), filedNameExpression.length()));
        String jsonCollectionName = fieldName.split("\\.")[0];

        String[] paths = filedNameExpression.split("\\.");
        String jsonSearchCollectionName = null;
        for (String p : paths) {
            if (isCollectionExpression(p)) {
                jsonSearchCollectionName = getFieldWithoutCollectionMarker(p);
                break;
            }
        }

        if (jsonBQueryParams.isEmpty()) {
            JsonBQueryParam jsonBQueryParam = new JsonBQueryParam(jsonSearchCollectionName, jsonCollectionName);
            jsonBQueryParam.getJsonBQueries().put(filter.getFilterGroupId(), queries);
            jsonBQueryParams.add(jsonBQueryParam);
        } else {
            ListIterator<JsonBQueryParam> jsonBQueryParamListIterator = jsonBQueryParams.listIterator();
            while(jsonBQueryParamListIterator.hasNext()){
                JsonBQueryParam jsonBQueryParam = jsonBQueryParamListIterator.next();
                if ((filter.getFilterGroupId() == null
                        || !jsonBQueryParam.getJsonBQueries().keySet().contains(filter.getFilterGroupId()))) {
                    jsonBQueryParam.getJsonBQueries().put(filter.getFilterGroupId(), queries);
                } else {
                    JsonBQueryParam newJsonBQueryParam = new JsonBQueryParam(jsonSearchCollectionName, jsonCollectionName);
                    newJsonBQueryParam.getJsonBQueries().putAll(jsonBQueryParam.getJsonBQueries());
                    newJsonBQueryParam.getJsonBQueries().put(filter.getFilterGroupId(), queries);
                    jsonBQueryParamListIterator.add(newJsonBQueryParam);
                }
            }
        }
    }



    private void addMultiJsonBFilter(List<JsonBQueryParam> jsonBQueryParams, StringBuilder jsonFilterSB) {
        jsonFilterSB.append(" (");
        boolean first = true;
        for (JsonBQueryParam jsonBQueryParam : jsonBQueryParams) {
            if (!first) {
                jsonFilterSB.append(" OR ");
            } else {
                first = false;
            }
            addJsonBFilter(jsonFilterSB, jsonBQueryParam);
        }
        jsonFilterSB.append(") ");
    }

    private void addJsonBFilter(StringBuilder jsonFilterSB, JsonBQueryParam jsonBQueryParam) {
        StringBuilder jsonParamSB = new StringBuilder();
        jsonParamSB.append("{");
        jsonParamSB.append("\"" + jsonBQueryParam.getJsonCollectionName() + "\":");
        jsonParamSB.append("[");
        int i = 1;
        for (Map<String, Object> stringStringMap : jsonBQueryParam.getJsonBQueriesAsList()) {
            String query = serialize(stringStringMap);
            jsonParamSB.append(query);
            if (i < jsonBQueryParam.getJsonBQueries().size())
                jsonParamSB.append(", ");
            i++;
        }
        jsonParamSB.append("]");
        jsonParamSB.append("}");
        jsonFilterSB.append(jsonBQueryParam.getJsonSearchCollectionName() + "\\:\\:jsonb @> '" + jsonParamSB.toString() + "'\\:\\:jsonb");
    }


    private void addMultiFilter(StringBuilder filtersSB, Filter filter, Set<Object> queries, String fieldName, String alias) {
        filtersSB.append(" (");
        boolean first = true;
        for (Object query : queries) {
            if (!first) {
                filtersSB.append(" or ");
            } else {
                first = false;
            }
            addFilter(filtersSB, filter, fieldName, alias, query);
        }
        filtersSB.append(") ");
    }

    private void addFilter(StringBuilder filtersSB, Filter filter, String fieldName, String alias, Object query) {
        filtersSB.append(getFilterCast(alias, filter.getType(), fieldName))
                .append(getRestriction(filter.getExpression()))
                .append(getFilterQuery(query, filter.getExpression(), filter.getType()));
    }


    //TODO move to helper
    public static <T> String serialize(T object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO move to helper
    public static <T> T parse(String json, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(json, clazz);
    }

    /**
     * Process all candidate expressions to determine aliases.
     *
     * @param tableAlias
     * @param expressionFields
     * @return String
     */
    private String createCollectionCasts(String table, String tableAlias, List<String> expressionFields) {
        StringBuilder result = new StringBuilder().append(table).append(" ").append(tableAlias);
        List<String> resultList = new ArrayList<>();
        // create fields
        tableAlias = tableAlias + ".entity";

        for (String exrpession : expressionFields) {
            String trimmedExpression = exrpession.substring(0, exrpession.lastIndexOf("]") + 1);
            List<String> expressionGroups = Arrays.asList(trimmedExpression.split("(?<=])"));
            String groupAlias = null;
//            separate into groups by collection markers
            for (String group : expressionGroups) {
                if (groupAlias == null) {
                    groupAlias = tableAlias;
                }
                StringBuilder fieldsSB = new StringBuilder();
                List<String> fields = Arrays.asList(getFields(group));
                fieldsSB.append("jsonb_array_elements(").append(groupAlias).append("->");
//                process fields in collection group
                for (int i = 0; i < fields.size(); i++) {
                    String field = fields.get(i);
                    String fieldWithoutCollectionMarker = getFieldWithoutCollectionMarker(field);
                    fieldsSB.append("'")
                            .append(fieldWithoutCollectionMarker)
                            .append("'");

                    if (i < fields.size() - 1 && !isCollectionExpression(field)) {
                        fieldsSB.append(" -> ");
                    }
                    if (isCollectionExpression(field)) {
                        //is collection expression, closing and setting alias
                        groupAlias = getFieldWithoutCollectionMarker(field);
                        fieldsSB.append(") as ").append(groupAlias);
                        String item = fieldsSB.toString();
                        //set expression only once repeating paths can occur and are unwanted
                        if (!resultList.contains(item)) {
                            resultList.add(item);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < resultList.size(); i++) {
            String field = resultList.get(i);
            if (i < resultList.size()) {
                result.append(",");
            }
            result.append(field);
        }
        return result.toString();
    }

    /**
     * Remove collection marker from a field.
     *
     * @param field
     * @return String
     */
    private String getFieldWithoutCollectionMarker(String field) {
        return isCollectionExpression(field) ? field.replace("[", "").replace("]", "") : field;
    }

    /**
     * Determine if field/fields is collection expression*
     *
     * @param fieldsString
     * @return boolean
     */
    private boolean isCollectionExpression(String fieldsString) {
        return fieldsString.contains("]");
    }

    /**
     * Build sorting expressions.
     *
     * @param alias
     * @param type
     * @param orderBy
     * @param sort
     * @return String
     */
    private String getSortingExpression(String alias, Type type, String orderBy, Sort sort) {
        return getSortingExpressionField(alias, type, orderBy, sort) + " " + sort.name().toUpperCase();
    }

    private String getSortingExpressionField(String alias, Type type, String orderBy, Sort sort) {
        return "CAST(" + getFieldStructure(alias, orderBy).toString() + " AS " + getType(type) + ")";
    }

    /**
     * Build field structure.
     *
     * @param alias
     * @param fieldsString
     * @return StringBuilder
     */
    private StringBuilder getFieldStructure(String alias, String fieldsString) {
        String[] fields = getFields(fieldsString);
        StringBuilder fieldsSB = new StringBuilder();
        fieldsSB.append(alias);
        if (fields.length == 1) {
            //in structure alias is followed just by one field
            fieldsSB.append(" ->> ");
        } else {
            fieldsSB.append(" -> ");
        }
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            fieldsSB.append("'")
                    .append(field)
                    .append("'");

            if (i < fields.length - 2) {
                fieldsSB.append(" -> ");
            } else if (i < fields.length - 1) {
                fieldsSB.append(" ->> ");
            }
        }
        return fieldsSB;
    }

    /**
     * Add sql expression specific markers to query by expression type.
     *
     * @param query
     * @param expression
     * @param type
     * @return String
     */
    private String getFilterQuery(Object query, Expression expression, Type type) {
        switch (expression) {
            case CONTAINS:
                return "'%" + query + "%'";
            case START_WITH:
                return "'" + query + "%'";
            case EQ:
                return type == Type.STRING ? "'" + query + "'" : query.toString();
            default:
                return query.toString();

        }
    }

    /**
     * Determine expression restriction.
     *
     * @param expression
     * @return String
     */
    private String getRestriction(Expression expression) {
        switch (expression) {
            case START_WITH:
            case CONTAINS:
                return "LIKE ";
            case EQ:
                return "=";
            case GT:
                return ">";
            case LT:
                return "<";
            default:
                throw new IllegalArgumentException("unknown expression " + expression.name());
        }
    }

    /**
     * Create type casts for filter.
     *
     * @param alias
     * @param type
     * @param fields
     * @return String
     */
    private String getFilterCast(String alias, Type type, String fields) {
        return "CAST(" + getFieldStructure(alias, fields) + " as " + getType(type) + ") ";
    }

    /**
     * Split field expression to array by dot delimiter.
     *
     * @param filedExpression
     * @return String[]
     */
    private String[] getFields(String filedExpression) {
//        remove dots from the end and start of the field
        if (filedExpression.startsWith(".")) {
            filedExpression = filedExpression.substring(1, filedExpression.length());
        }
        if (filedExpression.endsWith(".")) {
            filedExpression = filedExpression.substring(0, filedExpression.length() - 1);
        }
        return filedExpression.split("\\.");
    }

    /**
     * Convert Search type to postgres type.
     *
     * @param type
     * @return String
     */
    private String getType(Type type) {
        switch (type) {
            case STRING:
                return "varchar";
            case NUMBER:
                return "int";
            case TIMESTAMP:
                return "timestamp";
            case MONEY:
                return "double precision";
            default:
                throw new IllegalArgumentException("unknown type " + type.name());
        }
    }

}
