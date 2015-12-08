package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.model.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ServiceOfferingRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    public void testCreate(){
        Category category = categoryRepository.create(new Category("name", null, null, null, null));
        assertNotNull(category);
        assertNotNull(category.getId());
        categoryRepository.delete(category.getId());

    }
}