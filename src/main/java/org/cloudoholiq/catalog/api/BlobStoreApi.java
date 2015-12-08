package org.cloudoholiq.catalog.api;

import com.google.common.io.ByteStreams;
import org.apache.log4j.Logger;
import org.cloudoholiq.catalog.model.common.Blob;
import org.cloudoholiq.catalog.model.common.BlobMetadata;
import org.cloudoholiq.catalog.repository.BlobRepository;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/blob-store")
@Component
public class BlobStoreApi {

    Logger logger = Logger.getLogger(BlobStoreApi.class);
    private static final String BLOB_STORE_PATH = System.getProperty("user.dir") + File.separator + "blobs" + File.separator;

    @Context
    private ServletContext servletContext;
//
    @Context
    private UriInfo uriInfo;

    @Autowired
    private BlobRepository blobRepository;


    @GET
    @Path("{id}")
    public Response findById(@PathParam("id")UUID id) {
        Blob blob = blobRepository.findById(id);
        if (blob == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        byte[] result = blob.getData();
        return Response.ok(result, MediaType.valueOf(blob.getMediaType())).build();
    }

    @GET
    @Path("{entityType}/{entityId}")
    public Response getEntityBlobMetadata(@PathParam("entityType")String entityType,
                                          @PathParam("entityId")UUID entityId) {
        List<BlobMetadata> blobs = blobRepository.findByEntity(entityId);
        return Response.ok(blobs).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{entityType}/{entityId}/{collection}")
    public Response createBlob(@PathParam("entityType")String entityType,
                               @PathParam("entityId")UUID entityId, @PathParam("collection") String collection,
                               @FormDataParam("file") List<FormDataBodyPart> bodyParts) throws IOException {

        List<UUID> blobIdList = new ArrayList<>();
        for (FormDataBodyPart dataBodyPart : bodyParts) {
            BodyPartEntity bodyPartEntity = (BodyPartEntity) dataBodyPart.getEntity();
            InputStream inputStream = bodyPartEntity.getInputStream();
            String mediaType = dataBodyPart.getMediaType().toString();
            byte[] data = ByteStreams.toByteArray(inputStream);
            FormDataContentDisposition fileDetail = dataBodyPart.getFormDataContentDisposition();
            Blob blob = blobRepository.create(new Blob(fileDetail.getName(), data.length, mediaType, collection, entityType, entityId, data));
            blobIdList.add(blob.getId());
        }
        return Response.status(201).entity(blobIdList).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id")UUID id) {
        blobRepository.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    @DELETE
    @Path("{entityType}/{entityId}")
    public Response deleteByEntityId(@PathParam("entityType") String entityType, @PathParam("entityId") UUID entityId) {
        blobRepository.deleteByEntityId(entityId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void writeToFile(InputStream inputStream, String blobPath) {
        try {
            OutputStream out = new FileOutputStream(new File(blobPath));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(blobPath));
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new WebApplicationException(e, 400);
        }
    }

    private void saveFileFromUrl(String url, File file) throws IOException {
        java.net.URL website = new java.net.URL(url);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }
}
