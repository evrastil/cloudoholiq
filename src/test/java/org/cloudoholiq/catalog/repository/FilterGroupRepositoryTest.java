package org.cloudoholiq.catalog.repository;

import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.model.Category;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.cloudoholiq.catalog.model.search.Expression;
import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.model.search.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class FilterGroupRepositoryTest {

    @Autowired
    private FilterGroupRepository filterGroupRepository;

    @Autowired
    private FilterRepository filterRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    public void testCreate() {
        Filter filter = new Filter("path", "query", Expression.EQ, Type.STRING, "key");
        filterRepository.create(filter);
        FilterGroup fg = new FilterGroup();
        fg.setLabel("group");
        FilterGroup filterGroup = filterGroupRepository.create(fg);
        assertNotNull(filterGroup);
        Category category = categoryRepository.create(new Category("category1", null, null, null, null));
        filterGroupRepository.bindCategoryFilter(category.getId(), filterGroup.getId());
        List<FilterGroup> filterGroupList = filterGroupRepository.findByCategoryId(category.getId());
        assertNotNull(filterGroupList);
        assertTrue(filterGroupList.size()>0);

        filterRepository.delete(filter.getId());
        categoryRepository.delete(category.getId());
    }
}