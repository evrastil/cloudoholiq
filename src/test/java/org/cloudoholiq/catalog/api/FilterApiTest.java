package org.cloudoholiq.catalog.api;

import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.PgRule;
import org.cloudoholiq.catalog.model.Category;
import org.cloudoholiq.catalog.model.property.group.CategoryFilterGroup;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.cloudoholiq.catalog.model.property.group.FilterGroupDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class FilterApiTest {

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";

    private Client client = ClientBuilder.newClient();

    @ClassRule
    public static PgRule pgRule = new PgRule();

    @Test
    public void testFindAll(){
        Category category = createCategory(getCategory());
        List<UUID> categoryIds = new ArrayList<>();
        categoryIds.add(category.getId());
        CategoryFilterGroup filterGroup = create(new CategoryFilterGroup(categoryIds, getFilterGroup()));
        List<FilterGroupDto> filterGroups = client.target(host + ":" + port)
                .path("api/filters").request(MediaType.APPLICATION_JSON).get(new GenericType<List<FilterGroupDto>>(){});
        assertTrue(filterGroups.size()>0);
        delete(filterGroup.getFilterGroup().getId());
        deleteCategory(category.getId());
    }

    @Test
    public void testFindByCategory(){
        Category category = createCategory(getCategory());
        List<UUID> categoryIds = new ArrayList<>();
        categoryIds.add(category.getId());
        CategoryFilterGroup filterGroup = create(new CategoryFilterGroup(categoryIds, getFilterGroup()));
        List<FilterGroupDto> filterGroups = client.target(host + ":" + port)
                .queryParam("category", category.getId())
                .path("api/filters").request(MediaType.APPLICATION_JSON).get(new GenericType<List<FilterGroupDto>>(){});
        assertTrue(filterGroups.size()>0);
        delete(filterGroup.getFilterGroup().getId());
        deleteCategory(category.getId());
    }

    @Test
    public void testCreate() {
        Category category = createCategory(getCategory());
        List<UUID> categoryIds = new ArrayList<>();
        categoryIds.add(category.getId());
        CategoryFilterGroup created = create(new CategoryFilterGroup(categoryIds, getFilterGroup()));
        FilterGroup filterGroup = find(created.getFilterGroup().getId());
        assertNotNull(filterGroup);
        delete(created.getFilterGroup().getId());
        deleteCategory(category.getId());
    }


    private CategoryFilterGroup create(CategoryFilterGroup categoryFilterGroup) {
        return client.target(host + ":" + port).path("api/filters")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(categoryFilterGroup, MediaType.APPLICATION_JSON), CategoryFilterGroup.class);
    }

    private FilterGroup find(UUID id) {
        return client.target(host + ":" + port).path("api/filters/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(FilterGroup.class);
    }

    private void delete(UUID id) {
        client.target(host + ":" + port).path("api/filters/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
    }

    private FilterGroup update(FilterGroup filterGroup) {
        return client.target(host + ":" + port).path(String.format("api/filters/%s", filterGroup.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(filterGroup, MediaType.APPLICATION_JSON), FilterGroup.class);
    }

    public FilterGroup getFilterGroup() {
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.setLabel("fitler group 1");
        return filterGroup;
    }

    private Category createCategory(Category category) {
        return client.target(host + ":" + port).path("api/categories")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(category, MediaType.APPLICATION_JSON), Category.class);
    }

    private void deleteCategory(UUID id) {
        client.target(host + ":" + port).path("api/categories/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
    }

    private Category getCategory() {
        return new Category("name", "key", null, "label", "icon");
    }
}