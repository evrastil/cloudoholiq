package org.cloudoholiq.catalog.search;

import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.model.search.Sorting;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.cloudoholiq.catalog.repository.SortingRepository;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vrastil on 27.1.2015.
 */
public class GenericSearch {

    String aliasFields, filters, sorting, sortingFields, pagination = "";

    public GenericSearch() {
    }

    public GenericSearch(String aliasFields, String filters, String sorting, String sortingFields, String pagination) {
        this.aliasFields = aliasFields;
        this.filters = filters;
        this.sorting = sorting;
        this.sortingFields = sortingFields;
        this.pagination = pagination;
    }

    public String getAliasFields() {
        return aliasFields;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getSorting() {
        return sorting;
    }

    public void setSorting(String sorting) {
        this.sorting = sorting;
    }

    public String getPagination() {
        return pagination;
    }

    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

    public String getSortingFields() {
        return sortingFields;
    }

    public static SearchQueryParam getSearchQueryParamFromURI(UriInfo uriInfo, FilterRepository filterRepository, SortingRepository sortingRepository) {
        SearchQueryParam searchQueryParam = new SearchQueryParam();
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        Pagination pagination = new Pagination(0, 20);
        List<Sorting> sorting = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : queryParameters.entrySet()) {
            if ("q".equals(entry.getKey())) {
                for (String filterKey : entry.getValue()) {
                    String[] queryArr = filterKey.split("@");
                    int queryLength = queryArr.length;
                    if (queryLength != 2) {
                        throw new WebApplicationException(String.format("Query had %s paramters but it must have 2.", queryLength), 400);
                    }
                    Object query = queryArr[1];
                    String key = queryArr[0];
                    Filter filter = filterRepository.findByKey(key);
                    if (filter == null) {
                        throw new WebApplicationException(String.format("Filter %s is not defined", key), 400);
                    }
                    if(getExistingFilter(filters, filter)==null){
                        filter.setQuery(new HashSet<Object>(){{add(query);}});
                        filters.add(filter);
                    }else{
                        ((Set<Object>) getExistingFilter(filters, filter).getQuery()).add(query);
                    }
                }

            }
            if ("f".equals(entry.getKey())) {
                for (String filterKey : entry.getValue()) {
                    Filter filter = filterRepository.findByKey(filterKey);
                    if (filter == null) {
                        throw new WebApplicationException(String.format("Filter %s is not defined", filterKey), 400);
                    }
                    filters.add(filter);
                }
            }

            if ("s".equals(entry.getKey())) {
                for (String sortKey : entry.getValue()) {
                    Sorting sort = sortingRepository.findByKey(sortKey);
                    if (sort == null) {
                        throw new WebApplicationException(String.format("Sorting named %s is not defined", sortKey), 400);
                    }
                    sorting.add(sort);
                }
            }

            if ("limit".equals(entry.getKey())) {
                pagination.setLimit(Integer.valueOf(entry.getValue().get(0)));
            }
            if ("offset".equals(entry.getKey())) {
                pagination.setLimit(Integer.valueOf(entry.getValue().get(0)));
            }
        }
        searchQueryParam.setFilters(filters);
        searchQueryParam.setSorting(sorting);
        searchQueryParam.setPagination(pagination);
        return searchQueryParam;
    }

    private static Filter getExistingFilter(List<Filter> filters, Filter filter){
        for (Filter f : filters) {
            if(f.getId().equals(filter.getId())){
                return f;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "SELECT DISTINCT entity, CAST(entity ->> 'timestamp' AS INT) " + sortingFields +
                "FROM " +
                " " + aliasFields +
                " WHERE 1=1 " +
                " " + filters +
                " ORDER BY CAST(entity ->> 'timestamp' AS INT) ASC " + sorting +
                " " + pagination;
    }
}
