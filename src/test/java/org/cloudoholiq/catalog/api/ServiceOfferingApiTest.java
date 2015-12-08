package org.cloudoholiq.catalog.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudoholiq.catalog.ApplicationTests;
import org.cloudoholiq.catalog.common.StringUtil;
import org.cloudoholiq.catalog.model.ServiceOffering;
import org.cloudoholiq.catalog.model.property.Property;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.cloudoholiq.catalog.model.property.group.PropertyGroup;
import org.cloudoholiq.catalog.model.search.Expression;
import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.search.SearchQueryParam;
import org.cloudoholiq.catalog.model.search.Sort;
import org.cloudoholiq.catalog.model.search.Sorting;
import org.cloudoholiq.catalog.model.search.Type;
import org.cloudoholiq.catalog.repository.FilterGroupRepository;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.cloudoholiq.catalog.repository.ServiceOfferingRepository;
import org.cloudoholiq.catalog.repository.SortingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTests.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ServiceOfferingApiTest {

    @Value("${local.server.port}")
    private int port;
    private String host = "http://localhost";

    private Client client = ClientBuilder.newClient();

    @Autowired
    ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    FilterRepository filterRepository;

    @Autowired
    FilterGroupRepository filterGroupRepository;

    @Autowired
    private SortingRepository sortingRepository;

    @Before
    public void setUp() throws Exception {
        List<ServiceOffering> all = serviceOfferingRepository.findAll();
        for (ServiceOffering serviceOffering : all) {
            serviceOfferingRepository.delete(serviceOffering.getId());
        }

        List<Sorting> sortings = sortingRepository.findAll();
        for (Sorting sorting : sortings) {
            sortingRepository.delete(sorting.getId());
        }

        List<Filter> allFilters = filterRepository.findAll();
        for (Filter allFilter : allFilters) {
            if (!allFilter.getKey().startsWith("name")
                    &&!allFilter.getKey().equals("key-is")
                    &&!allFilter.getKey().equals("vendor")
                    &&!allFilter.getKey().equals("category")) {
                filterRepository.delete(allFilter.getId());
            }
        }
        List<FilterGroup> allFilterGroups = filterGroupRepository.findAll();
        for (FilterGroup allFilterGroup : allFilterGroups) {
            filterGroupRepository.delete(allFilterGroup.getId());
        }
    }

    @Test
    public void testCreate() {
        ServiceOffering created = create(getServiceOffering());
        ServiceOffering offering = find(created.getId());
        assertNotNull(offering);
        assertTrue(offering.getPropertyGroups().size()>0);
        delete(created.getId());
    }

    @Test
    public void testDelete() {
        ServiceOffering created = create(getServiceOffering());
        ServiceOffering offering = find(created.getId());
        assertNotNull(offering);
        delete(created.getId());
        assertNull(find(created.getId()));
    }


    @Test
    public void testUpdate() {
        ServiceOffering created = create(getServiceOffering());
        created.setName("another name");
        update(created);
        assertEquals(find(created.getId()).getName(), "another name");
    }

    @Test
    public void testSearchNameIs() {
        ServiceOffering created = create(getServiceOffering("test1",4, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("q","name-is@test1")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
    }

    @Test
    public void testSearchNameContains() {
        ServiceOffering created = create(getServiceOffering("test123",2, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("q","name-contains@st12")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
    }

    @Test
    public void testSearchNameStartWith() {
        ServiceOffering created = create(getServiceOffering("test789",3, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("q","name-start-with@test7")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
    }

    @Test
    public void testCountSearchNameStartWith() {
        ServiceOffering created = create(getServiceOffering("test789",3, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        Response response = client.target(host + ":" + port).path("api/services/count").queryParam("q","name-start-with@test7")
                .request(MediaType.APPLICATION_JSON)
                .get(Response.class);
        Map entity = response.readEntity(new GenericType<Map>() {});
        assertTrue(entity != null);
        assertEquals(entity.get("count"), 1);
    }

    @Test
    public void testSearchByKey() {
        ServiceOffering created = create(getServiceOffering("aaa bbb",456, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("q","key-is@aaa-bbb")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertEquals(offeringList.stream().findFirst().get().getKey(), "aaa-bbb");
    }

    @Test
    public void testSearchByVendor() {
        UUID vendor = UUID.randomUUID();
        ServiceOffering created = create(getServiceOffering("ccc",564, "CPU", vendor, UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("q","vendor@"+vendor)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertEquals(offeringList.stream().findFirst().get().getVendor(), vendor);
    }

    @Test
    public void testSearchByCategory() {
        UUID category = UUID.randomUUID();
        ServiceOffering created = create(getServiceOffering("ddd",3987, "CPU", UUID.randomUUID(), category));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("q","category@"+category)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertEquals(offeringList.stream().findFirst().get().getCategory(), category);
    }

    @Test
    public void testSearchInProperties() {
        ///////////////////////
        Filter cpu1 = new Filter();
        cpu1.setLabel("with 50 CPU");
        cpu1.setPath("[propertyGroups].[properties]");
        cpu1.setExpression(Expression.EQ);
        Set<Map> queryCpu1 = new HashSet<>();
        queryCpu1.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 50);
        }});
        cpu1.setQuery(queryCpu1);
        cpu1.setType(Type.JSONB);
        cpu1.setKey(StringUtil.normalizeId(cpu1.getLabel()));
        filterRepository.create(cpu1);


        ServiceOffering created = create(getServiceOffering("big machine", 50, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services").queryParam("f","with-50-cpu")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertEquals(offeringList.stream().findFirst().get().getName(), "big machine");
    }

    @Test
    public void testSearchOrInMultipleCategories() {
        UUID vendor = UUID.randomUUID();
        UUID category = UUID.randomUUID();
        UUID category2 = UUID.randomUUID();
        UUID category3 = UUID.randomUUID();
        ServiceOffering created = create(getServiceOffering("big machine 123123", 50, "CPU", vendor, category));
        ServiceOffering created2 = create(getServiceOffering("big machine 7788", 45, "CPU", vendor, category2));
        ServiceOffering created3 = create(getServiceOffering("big machine 666", 4, "CPU", vendor, category3));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services")
                .queryParam("q", "category@" + category)
                .queryParam("q", "category@" + category2)
                        .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {
                });
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertTrue(offeringList.size() == 2);
    }


    @Test
    public void testSearchInMiltipleJsonBPropertiesOrStatement() {
        // category virtual-servers filter groups
        FilterGroup cpuFilterVirtualServerGroups = new FilterGroup();
        cpuFilterVirtualServerGroups.setLabel("CPU filter");
        cpuFilterVirtualServerGroups = filterGroupRepository.create(cpuFilterVirtualServerGroups);

        Filter cpu1 = new Filter();
        cpu1.setLabel("with 1 CPU");
        cpu1.setPath("[propertyGroups].[properties]");
        Set<Map> queryCpu1 = new HashSet<>();
        queryCpu1.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 1);
        }});
        cpu1.setQuery(queryCpu1);
        cpu1.setExpression(Expression.EQ);
        cpu1.setType(Type.JSONB);
        cpu1.setKey(StringUtil.normalizeId(cpu1.getLabel()));
        cpu1.setFilterGroupId(cpuFilterVirtualServerGroups.getId());
        filterRepository.create(cpu1);

        Filter cpu2 = new Filter();
        cpu2.setExpression(Expression.EQ);
        cpu2.setLabel("with 8 CPU");
        cpu2.setPath("[propertyGroups].[properties]");
        Set<Map> queryCpu2 = new HashSet<>();
        queryCpu2.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 8);
        }});
        cpu2.setQuery(queryCpu2);
        cpu2.setType(Type.JSONB);
        cpu2.setKey(StringUtil.normalizeId(cpu2.getLabel()));
        cpu2.setFilterGroupId(cpuFilterVirtualServerGroups.getId());
        filterRepository.create(cpu2);

        Filter ram1 = new Filter();
        ram1.setLabel("with 2GB RAM");
        ram1.setPath("[propertyGroups].[properties]");
        ram1.setExpression(Expression.EQ);
        Set<Map> queryRam = new HashSet<>();
        queryRam.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 2);
        }});
        ram1.setQuery(queryRam);
        ram1.setType(Type.JSONB);
        ram1.setKey(StringUtil.normalizeId(ram1.getLabel()));
        filterRepository.create(ram1);

        UUID category = UUID.randomUUID();
        ServiceOffering created = create(getServiceOffering("testing 123", 1, "CPU", UUID.randomUUID(), category));
        ServiceOffering created2 = create(getServiceOffering("testing 555", 4, "CPU", UUID.randomUUID(), category));
        UUID category2 = UUID.randomUUID();
        ServiceOffering created3 = create(getServiceOffering("testing 666", 8, "CPU", UUID.randomUUID(), category2));
        ServiceOffering created4 = create(getServiceOffering("testing 777", 12, "CPU", UUID.randomUUID(), category2));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services")
                .queryParam("f", "with-1-cpu")
                .queryParam("f", "with-8-cpu")
                .queryParam("f", "with-2gb-ram")
                .queryParam("q", "category@"+category)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertTrue(offeringList.size()==1);
    }

    @Test
    public void testSearchInMultipleProperties() {
        //////////////////

        Filter ram1 = new Filter();
        ram1.setLabel("with 2GB RAM");
        ram1.setPath("[propertyGroups].[properties]");
        ram1.setExpression(Expression.EQ);
        Set<Map> queryRam = new HashSet<>();
        queryRam.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 2);
        }});
        ram1.setQuery(queryRam);
        ram1.setType(Type.JSONB);
        ram1.setKey(StringUtil.normalizeId(ram1.getLabel()));
        filterRepository.create(ram1);

        ////////////////

        Filter hdd1 = new Filter();
        hdd1.setLabel("with 5GB HDD");
        hdd1.setPath("[propertyGroups].[properties]");
        hdd1.setExpression(Expression.EQ);
        Set<Map> queryHdd = new HashSet<>();
        queryHdd.add(new LinkedHashMap<String, Object>() {{
            put("name", "HDD");
            put("value", 5);
        }});
        hdd1.setQuery(queryHdd);
        hdd1.setType(Type.JSONB);
        hdd1.setKey(StringUtil.normalizeId(hdd1.getLabel()));
        filterRepository.create(hdd1);
        ///////////////////////
        Filter cpu1 = new Filter();
        cpu1.setLabel("with 1 CPU");
        cpu1.setPath("[propertyGroups].[properties]");
        cpu1.setExpression(Expression.EQ);
        Set<Map> queryCpu1 = new HashSet<>();
        queryCpu1.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 1);
        }});
        cpu1.setQuery(queryCpu1);
        cpu1.setType(Type.JSONB);
        cpu1.setKey(StringUtil.normalizeId(cpu1.getLabel()));
        filterRepository.create(cpu1);

        Filter cpu2 = new Filter();
        cpu2.setLabel("with 8 CPU");
        cpu2.setPath("[propertyGroups].[properties]");
        cpu2.setExpression(Expression.EQ);
        Set<Map> queryCpu2 = new HashSet<>();
        queryCpu2.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 8);
        }});
        cpu2.setQuery(queryCpu2);
        cpu2.setType(Type.JSONB);
        cpu2.setKey(StringUtil.normalizeId(cpu2.getLabel()));
        filterRepository.create(cpu2);

        ServiceOffering created = create(getServiceOffering("testing 123", 1, "CPU", UUID.randomUUID(), UUID.randomUUID()));
        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services")
                .queryParam("f", "with-1-cpu")
                .queryParam("f","with-2gb-ram")
                .queryParam("f","with-5gb-hdd")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<ServiceOffering>>() {});
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertEquals(offeringList.stream().findFirst().get().getName(), "testing 123");
    }


    @Test
    public void testComplexSearch() {
        //////////////////

        Filter ram1 = new Filter();
        ram1.setLabel("with 2GB RAM");
        ram1.setPath("[propertyGroups].[properties]");
        ram1.setExpression(Expression.EQ);
        Set<Map> queryRam = new HashSet<>();
        queryRam.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 2);
        }});
        ram1.setQuery(queryRam);
        ram1.setType(Type.JSONB);
        ram1.setKey(StringUtil.normalizeId(ram1.getLabel()));
        filterRepository.create(ram1);

        ////////////////

        Filter hdd1 = new Filter();
        hdd1.setLabel("with 5GB HDD");
        hdd1.setPath("[propertyGroups].[properties]");
        hdd1.setExpression(Expression.EQ);
        Set<Map> queryHdd = new HashSet<>();
        queryHdd.add(new LinkedHashMap<String, Object>() {{
            put("name", "HDD");
            put("value", 5);
        }});
        hdd1.setQuery(queryHdd);
        hdd1.setType(Type.JSONB);
        hdd1.setKey(StringUtil.normalizeId(hdd1.getLabel()));
        filterRepository.create(hdd1);
        ///////////////////////
        Filter cpu1 = new Filter();
        cpu1.setLabel("with 4 CPU");
        cpu1.setPath("[propertyGroups].[properties]");
        cpu1.setExpression(Expression.EQ);
        Set<Map> queryCpu1 = new HashSet<>();
        queryCpu1.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 4);
        }});
        cpu1.setQuery(queryCpu1);
        cpu1.setType(Type.JSONB);
        cpu1.setKey(StringUtil.normalizeId(cpu1.getLabel()));
        filterRepository.create(cpu1);

        Filter cpu2 = new Filter();
        cpu2.setLabel("with 8 CPU");
        cpu2.setPath("[propertyGroups].[properties]");
        cpu2.setExpression(Expression.EQ);
        Set<Map> queryCpu2 = new HashSet<>();
        queryCpu2.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 8);
        }});
        cpu2.setQuery(queryCpu2);
        cpu2.setType(Type.JSONB);
        cpu2.setKey(StringUtil.normalizeId(cpu2.getLabel()));
        filterRepository.create(cpu2);


        Sorting sortingNameAsc = new Sorting();
        sortingNameAsc.setType(Type.STRING);
        sortingNameAsc.setOrderBy("name");
        sortingNameAsc.setKey("name-asc");
        sortingNameAsc.setSort(Sort.ASC);
        sortingRepository.create(sortingNameAsc);

        Sorting sortingNameDesc = new Sorting();
        sortingNameDesc.setType(Type.STRING);
        sortingNameDesc.setOrderBy("name");
        sortingNameDesc.setKey("name-desc");
        sortingNameDesc.setSort(Sort.DESC);
        sortingRepository.create(sortingNameDesc);

        UUID vendor = UUID.randomUUID();
        UUID category = UUID.randomUUID();
        UUID category2 = UUID.randomUUID();
        ServiceOffering created1 = create(getServiceOffering("name987", 4, "CPU", vendor, category));
        ServiceOffering created2 = create(getServiceOffering("name 456", 4, "CPU", vendor, category2));
        ServiceOffering created3 = create(getServiceOffering("qqq999", 16, "LAN", vendor, category2));
        long before = System.currentTimeMillis();

        List<ServiceOffering> offeringList = client.target(host + ":" + port).path("api/services")
                .queryParam("f", "with-4-cpu")
                .queryParam("f", "with-5gb-hdd")
                .queryParam("f", "with-2gb-ram")
                .queryParam("q", "name-contains@me 4")
                .queryParam("q", "category@"+category)
                .queryParam("q", "category@" + category2)
                .queryParam("q", "vendor@" + vendor)
                .queryParam("s", "name-asc")
        .request(MediaType.APPLICATION_JSON).get(new GenericType<List<ServiceOffering>>() {});

        long after = System.currentTimeMillis();
        System.err.println(String.format("query took: %s", after-before));
        assertNotNull(offeringList);
        assertTrue(!offeringList.isEmpty());
        assertTrue(offeringList.size()==1);
        assertEquals(offeringList.stream().findFirst().get().getName(), "name 456");
    }


    private String getEncodedStringQueryParam(SearchQueryParam searchQueryParam) {
        String param = null;
        try {
            param = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(searchQueryParam);
            return URLEncoder.encode(param, "UTF-8");
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private ServiceOffering create(ServiceOffering serviceOffering) {
        return client.target(host + ":" + port).path("api/services")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(serviceOffering, MediaType.APPLICATION_JSON), ServiceOffering.class);
    }

    private ServiceOffering find(UUID id) {
        return client.target(host + ":" + port).path("api/services/" + id)
                .request(MediaType.APPLICATION_JSON)
                .get(ServiceOffering.class);
    }

    private void delete(UUID id) {
        client.target(host + ":" + port).path("api/services/" + id)
                .request(MediaType.APPLICATION_JSON)
                .delete();
    }

    private ServiceOffering update(ServiceOffering serviceOffering) {
        return client.target(host + ":" + port).path(String.format("api/services/%s", serviceOffering.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(serviceOffering, MediaType.APPLICATION_JSON), ServiceOffering.class);
    }

    public ServiceOffering getServiceOffering() {
        return getServiceOffering("service offering 1", 5, "count", UUID.randomUUID(), UUID.randomUUID());
    }

    public ServiceOffering getServiceOffering(String name, Object propertyValue, String propertyName, UUID vendor, UUID category) {
        ServiceOffering serviceOffering = new ServiceOffering();
        serviceOffering.setVendor(vendor);
        serviceOffering.setCategory(category);
        serviceOffering.setName(name);
        serviceOffering.setKey(StringUtil.normalizeId(serviceOffering.getName()));
        serviceOffering.setPropertyGroups(new ArrayList<>());
        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setLabel("HW properties");
        propertyGroup.setProperties(new ArrayList<>());
        Property property = new Property();
        property.setName(propertyName);
        property.setValue(propertyValue);
        propertyGroup.getProperties().add(property);
        Property property2 = new Property();
        property2.setName("RAM");
        property2.setValue(2);
        property2.setUnit("GB");
        propertyGroup.getProperties().add(property2);
        Property property3 = new Property();
        property3.setName("HDD");
        property3.setValue(5);
        property3.setUnit("GB");
        propertyGroup.getProperties().add(property3);
        serviceOffering.getPropertyGroups().add(propertyGroup);
        return serviceOffering;
    }
}