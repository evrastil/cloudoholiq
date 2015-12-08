package org.cloudoholiq.catalog.api;


import org.cloudoholiq.catalog.model.Vendor;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.cloudoholiq.catalog.repository.SortingRepository;
import org.cloudoholiq.catalog.repository.VendorRepository;
import org.cloudoholiq.catalog.search.GenericSearch;
import org.cloudoholiq.catalog.search.SearchQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Path("/vendors")
@Component
public class VendorApi {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private SortingRepository sortingRepository;

    @GET
    public Response list() {
        SearchQueryParam searchQueryParam = GenericSearch.getSearchQueryParamFromURI(uriInfo, filterRepository, sortingRepository);
        List<Vendor> vendors = vendorRepository.search(searchQueryParam);
        return Response.ok(vendors).build();
    }

    @GET
    @Path("count")
    public Response listCount() {
        SearchQueryParam searchQueryParam = GenericSearch.getSearchQueryParamFromURI(uriInfo, filterRepository, sortingRepository);
        long count = vendorRepository.searchCount(searchQueryParam);
        return Response.ok(new HashMap<String, Long>(){{put("count", count);}}).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID id) {
        return Response.ok(vendorRepository.findById(id)).build();
    }

    @GET
    @Path("key/{vendor}")
    public Response findByKey(@PathParam("vendor") String vendor) {
        Vendor byKey = vendorRepository.findByKey(vendor);
        if(byKey == null) {
            throw new WebApplicationException(204);
        }
        return Response.ok(byKey).build();
    }

    @POST
    public Response create(@Valid Vendor vendor) {
        vendor = vendorRepository.create(vendor);
        UriBuilder pathBuilder = uriInfo.getAbsolutePathBuilder();
        URI created = pathBuilder.path(vendor.getId().toString()).build();
        return Response.status(201).entity(vendor).location(created).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        vendorRepository.delete(id);
        return Response.status(204).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") UUID id, @Valid Vendor vendor) {
        vendorRepository.update(vendor);
        return Response.ok(vendor).build();
    }

}
