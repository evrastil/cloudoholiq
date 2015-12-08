package org.cloudoholiq.catalog.api;


import org.apache.log4j.Logger;
import org.cloudoholiq.catalog.model.ServiceOffering;
import org.cloudoholiq.catalog.search.GenericSearch;
import org.cloudoholiq.catalog.search.SearchQueryParam;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.cloudoholiq.catalog.repository.ServiceOfferingRepository;
import org.cloudoholiq.catalog.repository.SortingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


@Path("services")
@Component
public class ServiceOfferingApi {
    private static Logger logger = Logger.getLogger(ServiceOfferingApi.class);

    @Context
    private UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private SortingRepository sortingRepository;

    @GET
    public Response list() {
        SearchQueryParam searchQueryParam = GenericSearch.getSearchQueryParamFromURI(uriInfo, filterRepository, sortingRepository);
        List<ServiceOffering> serviceOfferings = serviceOfferingRepository.search(searchQueryParam);
        return Response.ok(serviceOfferings).build();
    }

    @GET
    @Path("count")
    public Response listCount() {
        SearchQueryParam searchQueryParam = GenericSearch.getSearchQueryParamFromURI(uriInfo, filterRepository, sortingRepository);
        long count = serviceOfferingRepository.searchCount(searchQueryParam);
        return Response.ok(new HashMap<String, Long>(){{put("count", count);}}).build();
    }

    @GET
    @Path("key/{service}")
    public Response findByKey(@PathParam("service") String service) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findByKey(service);
        if(serviceOffering == null) {
            throw new WebApplicationException(204);
        }
        return Response.ok(serviceOffering).build();
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID id) {
        ServiceOffering serviceOffering = serviceOfferingRepository.findById(id);
        if(serviceOffering == null) {
            throw new WebApplicationException(204);
        }
        return Response.ok(serviceOffering).build();
    }

    @POST
    public Response create(@Valid ServiceOffering serviceOffering) {
        ServiceOffering entity = serviceOfferingRepository.create(serviceOffering);
        UriBuilder pathBuilder = uriInfo.getAbsolutePathBuilder();
        URI created = pathBuilder.path(entity.getId().toString()).build();
        return Response.status(201).entity(entity).location(created).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        serviceOfferingRepository.delete(id);
        return Response.status(204).build();
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") UUID id,@Valid ServiceOffering serviceOffering) {
        serviceOffering.setId(id);
        return Response.ok(serviceOfferingRepository.update(serviceOffering)).build();
    }

}
