package org.cloudoholiq.catalog.repository;

import com.github.javafaker.Faker;
import org.cloudoholiq.catalog.Application;
import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.PgRule;
import org.cloudoholiq.catalog.model.Category;
import org.cloudoholiq.catalog.model.ServiceOffering;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class CategoryRepositoryTest {

    private final Faker faker = new Faker();

    @ClassRule
    public static PgRule pgRule = new PgRule();

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;


    @Test
    public void testCreate(){
        String query = "";
        for (int i = 0; i < 10; i++) {
            ServiceOffering serviceOffering = new ServiceOffering();
            String sentence = faker.lorem().sentence(5);
            serviceOffering.setName(sentence);
            serviceOfferingRepository.create(serviceOffering);
            query = serviceOffering.getName();
        }
        List<ServiceOffering> offerings = serviceOfferingRepository.search(query);
        Assert.assertTrue(offerings.size()>0);

    }
}