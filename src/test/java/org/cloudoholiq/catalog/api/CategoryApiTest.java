package org.cloudoholiq.catalog.api;

import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.PgRule;
import org.cloudoholiq.catalog.model.Category;
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
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class CategoryApiTest {

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";

    private Client client = ClientBuilder.newClient();

    @ClassRule
    public static PgRule pgRule = new PgRule();

//    @Test
//    public void testList() throws Exception {
//        assertEquals("test123", new TestRestTemplate().getForObject("http://localhost:" + port + "/categories", Category.class).getName());
//    }

    @Test
    public void testList() throws Exception {
        Category category =create( getCategory());
        List<Category> clientResponse = client.target(host + ":" + port)
                .path("api/categories").request(MediaType.APPLICATION_JSON).get(new GenericType<List<Category>>(){});
        assertNotNull(clientResponse);
        delete(category.getId());
    }

    private Category getCategory() {
        return new Category("name", "key", null, "label", "icon");
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        Response  response = client.target(host + ":" + port)
                .path("api/categories/" + UUID.randomUUID()).request(MediaType.APPLICATION_JSON).get();
        assertTrue(response.getStatus()==204);
    }

    @Test
    public void testFindById() throws Exception {
        Category createdCategory = create(getCategory());
        Category categoryById = find(createdCategory.getId());
        assertNotNull(categoryById);
        delete(createdCategory.getId());
    }

    @Test
    public void testFindByCategory() throws Exception {
        Category createdCategory = create(getCategory());
        Category categoryByKey = client.target(host + ":" + port).path("api/categories/key/" + createdCategory.getKey())
                .request(MediaType.APPLICATION_JSON)
                .get(Category.class);
        assertNotNull(categoryByKey);
        delete(createdCategory.getId());
    }

    @Test
    public void testCreate() throws Exception {
        Category category = create(getCategory());
        assertNotNull(category);
        assertNotNull(category.getName());
        delete(category.getId());
    }

    @Test
    public void testDelete() throws Exception {
        Category createdCategory = create(getCategory());
        assertNotNull(createdCategory);
        delete(createdCategory.getId());
        Category categoryById = find(createdCategory.getId());
        assertNull(categoryById);
    }

    protected void delete(UUID id) {
        client.target(host + ":" + port).path("api/categories/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
    }

    @Test
    public void testUpdate() throws Exception {
        Category createdCategory = create(getCategory());
        assertNotNull(createdCategory);
        createdCategory.setName("newname");
        Category updatedCategory = update(createdCategory);
        assertNotNull(updatedCategory);
        assertTrue(updatedCategory.getName().equals(createdCategory.getName()));
    }

    private Category create(Category category) {
        return client.target(host + ":" + port).path("api/categories")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(category, MediaType.APPLICATION_JSON), Category.class);
    }

    private Category find(UUID id) {
        return client.target(host + ":" + port).path("api/categories/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(Category.class);
    }

    private Category update(Category category) {
        return client.target(host + ":" + port).path(String.format("api/categories/%s", category.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(category, MediaType.APPLICATION_JSON), Category.class);
    }
}