package org.cloudoholiq.catalog.api;

import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.PgRule;
import org.cloudoholiq.catalog.model.common.BlobMetadata;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class BlobStoreApiTest {
    @ClassRule
    public static PgRule pgRule = new PgRule();

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";

    private Client client = ClientBuilder.newClient().register(MultiPartFeature.class);

    @Test
    public void testFindById() throws Exception {
        UUID entityId = UUID.randomUUID();
        List<UUID> blobList = create("logo2.png", "service", entityId, "test");
        List<BlobMetadata> blobMetadataList = find(entityId, "service");
        Assert.assertTrue(blobMetadataList.size()>0);
        delete(entityId, "service");
    }

    @Test
    public void testCreateBlob() throws Exception {
        UUID entityId = UUID.randomUUID();
        List<UUID> blobList = create("logo2.png", "service", entityId, "test");
        Assert.assertNotNull(blobList);
        Assert.assertTrue(blobList.size()>0);
        delete(entityId, "service");
    }

    @Test
    public void testDeleteByEntityId() throws Exception {
        UUID entityId = UUID.randomUUID();
        List<UUID> blobList = create("logo2.png", "service", entityId, "test");
        delete(entityId, "service");
        List<BlobMetadata> blobs = find(entityId, "service");
        Assert.assertTrue(blobs.isEmpty());
    }

    private List<UUID> create(String name, String entityType, UUID entityId, String collection) {

        InputStream inputStream = BlobStoreApiTest.class.getResourceAsStream("/img/" + name);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new StreamDataBodyPart("file", inputStream, name, MediaType.valueOf("image/png")));

        List<UUID> blobs = client.target(host + ":" + port)
                .path("api/blob-store/" + entityType + "/" + entityId + "/" + collection)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new GenericType<List<UUID>>() {});
        return blobs;
    }

    private List<BlobMetadata> find(UUID entityId, String entityType) {
        return client.target(host + ":" + port).path("api/blob-store/" + entityType+"/"+entityId)
                .request(MediaType.APPLICATION_JSON)
                .get(new javax.ws.rs.core.GenericType<List<BlobMetadata>>(){});
    }

    private List<BlobMetadata> delete(UUID entityId, String entityType) {
        return client.target(host + ":" + port).path("api/blob-store/" + entityType+"/"+entityId)
                .request(MediaType.APPLICATION_JSON)
                .delete(new javax.ws.rs.core.GenericType<List<BlobMetadata>>(){});
    }
}