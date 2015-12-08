package org.cloudoholiq.catalog.api;


import org.apache.log4j.Logger;
import org.cloudoholiq.catalog.model.property.group.CategoryFilterGroup;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.cloudoholiq.catalog.model.property.group.FilterGroupDto;
import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.repository.FilterGroupRepository;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/filters")
@Component
public class FilterApi {
    private static Logger logger = Logger.getLogger(FilterApi.class);
    @Context
    private UriInfo uriInfo;

    @Autowired
    private FilterGroupRepository filterGroupRepository;
    @Autowired
    private FilterRepository filterRepository;


    @GET
    public Response list(@QueryParam("categories") List<String> categories) {
        List<FilterGroupDto> filterGroupDtoList = new ArrayList<>();
        List<FilterGroup> filterGroups = filterGroupRepository.findAll();
        for (FilterGroup filterGroup : filterGroups) {
            List<Filter> filters = filterRepository.findByFilterGroupId(filterGroup.getId());
            filterGroupDtoList.add(new FilterGroupDto(filters, filterGroup));
        }
        return Response.ok(filterGroupDtoList).build();
    }


    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID id) {
        FilterGroup filterGroup = filterGroupRepository.findById(id);
        if(filterGroup == null) {
            throw new WebApplicationException(204);
        }
        return Response.ok(filterGroup).build();
    }

    @GET
    @Path("items/{filterGroupId}")
    public Response findByItemId(@PathParam("filterGroupId") UUID filterGroupId) {
        List<Filter> filters = filterRepository.findByFilterGroupId(filterGroupId);
        return Response.ok(filters).build();
    }

    @POST
    @Path("item")
    public Response createFilterItem(Filter filter) {
        filter = filterRepository.create(filter);
        return Response.status(201).entity(filter).build();
    }

    @POST
    public Response create(CategoryFilterGroup categoryFilterGroup) {
        FilterGroup filterGroup = filterGroupRepository.create(categoryFilterGroup.getFilterGroup());
        for (UUID category : categoryFilterGroup.getCategories()) {
            filterGroupRepository.bindCategoryFilter(category, filterGroup.getId());
        }
        UriBuilder pathBuilder = uriInfo.getAbsolutePathBuilder();
        URI created = pathBuilder.path(filterGroup.getId().toString()).build();
        categoryFilterGroup.setFilterGroup(filterGroup);
        return Response.status(201).entity(categoryFilterGroup).location(created).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        filterGroupRepository.delete(id);
        return Response.status(204).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") String id,@Valid FilterGroup filterGroup) {
        return Response.ok(filterGroupRepository.update(filterGroup)).build();
    }

}
