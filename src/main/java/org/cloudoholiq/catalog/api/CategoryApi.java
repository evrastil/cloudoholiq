package org.cloudoholiq.catalog.api;


import org.apache.log4j.Logger;
import org.cloudoholiq.catalog.model.Category;
import org.cloudoholiq.catalog.repository.CategoryRepository;
import org.cloudoholiq.catalog.repository.FilterGroupRepository;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.cloudoholiq.catalog.repository.SortingRepository;
import org.cloudoholiq.catalog.search.GenericSearch;
import org.cloudoholiq.catalog.search.SearchQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Component
@Path("categories")
public class CategoryApi {
    private static Logger logger = Logger.getLogger(CategoryApi.class);

    @Context
    private UriInfo uriInfo;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private SortingRepository sortingRepository;

    @GET
    public Response list() {
        SearchQueryParam searchQueryParam = GenericSearch.getSearchQueryParamFromURI(uriInfo, filterRepository, sortingRepository);
        List<Category> categories = categoryRepository.search(searchQueryParam);
        return Response.ok(categories).build();
    }

    @GET
    @Path("count")
    public Response listCount() {
        SearchQueryParam searchQueryParam = GenericSearch.getSearchQueryParamFromURI(uriInfo, filterRepository, sortingRepository);
        long count = categoryRepository.searchCount(searchQueryParam);
        return Response.ok(new HashMap<String, Long>(){{put("count", count);}}).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID id) {
        Category category = categoryRepository.findById(id);
        if(category == null) {
            throw new WebApplicationException(204);
        }
        return Response.ok(category).build();
    }

    @GET
    @Path("key/{category}")
    public Response findByCategory(@PathParam("category") String category) {
        Category categoryByKey = categoryRepository.findByKey(category);
        if(categoryByKey == null) {
            throw new WebApplicationException(204);
        }
        return Response.ok(categoryByKey).build();
    }

    @POST
    public Response create(Category category) {
        Category entity = categoryRepository.create(category);
        UriBuilder pathBuilder = uriInfo.getAbsolutePathBuilder();
        URI created = pathBuilder.path(entity.getId().toString()).build();
        return Response.status(201).entity(entity).location(created).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        try {
            categoryRepository.delete(id);
        } catch (DataIntegrityViolationException e) {
            throw new WebApplicationException(e, 409);
        }
        return Response.status(204).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") UUID id, @Valid Category category) {
        return Response.ok(categoryRepository.update(category)).build();
    }

}
