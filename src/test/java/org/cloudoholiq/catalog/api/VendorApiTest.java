package org.cloudoholiq.catalog.api;

import org.cloudoholiq.catalog.Application;
import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.model.Vendor;
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
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class VendorApiTest {

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";

    private Client client = ClientBuilder.newClient();
    
    @Test
    public void testList() throws Exception {

    }

    @Test
    public void testFindById() throws Exception {

    }

    @Test
    public void testFindByKey() throws Exception {

    }

    @Test
    public void testCreate() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    private Vendor create(Vendor vendor) {
        return client.target(host + ":" + port).path("api/vendors")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(vendor, MediaType.APPLICATION_JSON), Vendor.class);
    }

    private Vendor find(UUID id) {
        return client.target(host + ":" + port).path("api/vendors/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(Vendor.class);
    }

    private Vendor update(Vendor vendor) {
        return client.target(host + ":" + port).path(String.format("api/vendors/%s", vendor.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(vendor, MediaType.APPLICATION_JSON), Vendor.class);
    }
}