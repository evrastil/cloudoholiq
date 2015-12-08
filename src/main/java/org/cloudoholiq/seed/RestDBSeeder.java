package org.cloudoholiq.seed;

import com.github.javafaker.Faker;
import org.cloudoholiq.catalog.common.StringUtil;
import org.cloudoholiq.catalog.common.localization.LocalizedField;
import org.cloudoholiq.catalog.model.CatalogItem;
import org.cloudoholiq.catalog.model.Category;
import org.cloudoholiq.catalog.model.ServiceOffering;
import org.cloudoholiq.catalog.model.Vendor;
import org.cloudoholiq.catalog.model.pricing.CurrencyType;
import org.cloudoholiq.catalog.model.pricing.Pricing;
import org.cloudoholiq.catalog.model.pricing.RecurrencePeriodType;
import org.cloudoholiq.catalog.model.property.Property;
import org.cloudoholiq.catalog.model.property.group.CategoryFilterGroup;
import org.cloudoholiq.catalog.model.property.group.FilterGroup;
import org.cloudoholiq.catalog.model.property.group.PropertyGroup;
import org.cloudoholiq.catalog.model.search.Expression;
import org.cloudoholiq.catalog.model.search.Filter;
import org.cloudoholiq.catalog.model.search.Type;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class RestDBSeeder {

    private static Logger logger = Logger.getLogger(RestDBSeeder.class.getName());

    private final Faker faker = new Faker();

    private Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
    private WebTarget webTarget;
    private int testDataSize;

    public RestDBSeeder(String protocol, String host, int port) {
        webTarget = client.target(String.format("%s://%s:%s", protocol, host, port));
    }

    private Category createCategory(Category category) {
        return webTarget.path("api/categories")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(category, MediaType.APPLICATION_JSON), Category.class);
    }

    private ServiceOffering createServiceOffering(ServiceOffering serviceOffering) {
        return webTarget.path("api/services")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(serviceOffering, MediaType.APPLICATION_JSON), ServiceOffering.class);
    }

    private Vendor createVendor(Vendor vendor) {
        return webTarget.path("api/vendors")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(vendor, MediaType.APPLICATION_JSON), Vendor.class);
    }

    private CategoryFilterGroup createFilter(CategoryFilterGroup categoryFilterGroup) {
        return webTarget.path("api/filters")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(categoryFilterGroup, MediaType.APPLICATION_JSON), CategoryFilterGroup.class);
    }

    private Filter createFilterItem(Filter filter) {
        return webTarget.path("api/filters/item")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(filter, MediaType.APPLICATION_JSON), Filter.class);
    }

    private ServiceOffering updateService(ServiceOffering serviceOffering) {
        return webTarget.path(String.format("api/services/%s", serviceOffering.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(serviceOffering, MediaType.APPLICATION_JSON), ServiceOffering.class);
    }

    private Vendor updateVendor(Vendor vendor) {
        return webTarget.path(String.format("api/vendors/%s", vendor.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(vendor, MediaType.APPLICATION_JSON), Vendor.class);
    }

    private UUID createBlob(String name, String entityType, UUID entityId, String collection) {

        InputStream inputStream = RestDBSeeder.class.getResourceAsStream("/icons/" + name);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.bodyPart(new StreamDataBodyPart("file", inputStream, name, MediaType.valueOf("image/png")));

        List<UUID> blobs = webTarget
                .path("api/blob-store/" + entityType + "/" + entityId + "/" + collection)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new GenericType<List<UUID>>() {});
        return blobs.stream().findFirst().get();
    }


    private Category updateCategory(Category category) {
        return webTarget.path(String.format("api/categories/%s", category.getId()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(category, MediaType.APPLICATION_JSON), Category.class);
    }


    public void fillSomeTestData(int testDataSize) {
        this.testDataSize = testDataSize;
        logger.info(String.format("Filling test data, items count is %s.", testDataSize));
        List<Vendor> vendors = createVendors();
        createCategories(vendors);
        logger.info("Completed");
    }
    private int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }

    public interface OptionFactory<T extends CatalogItem> {
        public  void createOptions(T catalogEntry);
    }

    public int getTestDataSize() {
        return testDataSize;
    }

    private void createServiceItems(Category category, List<Vendor> vendors, OptionFactory<ServiceOffering> optionFactory) {
        int j = 0;
        for (int i = 0; i < getTestDataSize(); i++) {
            ServiceOffering serviceOffering = new ServiceOffering();
            serviceOffering.setCategory(category.getId());
            serviceOffering.setName(faker.lorem().sentence(randInt(1,3)));
            serviceOffering.setVendor(vendors.get(j).getId());
            j++;
            if (i%2==0) {
                Pricing pricingVO = new Pricing();
                pricingVO.setCurrencyType(CurrencyType.CZK);
                pricingVO.setRecurrencePeriodType(RecurrencePeriodType.MONTH);
                pricingVO.setPrice(BigDecimal.valueOf(randDouble(1, 1000)));
                serviceOffering.setPricing(pricingVO);
            }
            serviceOffering.setDescription(faker.lorem().sentence(randInt(3, 10)));
            serviceOffering.setPropertyGroups(new ArrayList<>());
            optionFactory.createOptions(serviceOffering);
            serviceOffering = createServiceOffering(serviceOffering);
            UUID id = createBlob("server2.png", "service", serviceOffering.getId(), "icon");
            serviceOffering.setIcon(id.toString());
            updateService(serviceOffering);
            if(j>roundUp(getTestDataSize(),2)-1){
                j=0;
            }
        }
    }

    private void createManagementOptions(ServiceOffering serviceOffering) {
        //management
        PropertyGroup managementSpecifications = new PropertyGroup();
        managementSpecifications.setLabel("Management options");
        serviceOffering.getPropertyGroups().add(createManagementProperties(managementSpecifications));
    }

    private void createSecurityOptions(ServiceOffering serviceOffering) {
        //security
        PropertyGroup securitySpecifications = new PropertyGroup();
        securitySpecifications.setLabel("Security properties");
        serviceOffering.getPropertyGroups().add(createSecurityPropertiesProperties(securitySpecifications));
    }

    private void createBackupOptions(ServiceOffering serviceOffering) {
        //backup
        PropertyGroup backupSpecifications = new PropertyGroup();
        backupSpecifications.setLabel("Backup options");
        serviceOffering.getPropertyGroups().add(createBackupProperties(backupSpecifications));
    }

    private void createSoftwareOptions(ServiceOffering serviceOffering) {
        //supported sw
        PropertyGroup softwareSpecifications = new PropertyGroup();
        softwareSpecifications.setLabel("Software options");
        serviceOffering.getPropertyGroups().add(createSWProperties(softwareSpecifications));
    }

    private void createHardwareOptions(ServiceOffering serviceOffering) {
        //available hw options
        PropertyGroup hardwareSpecifications1 = new PropertyGroup();
        hardwareSpecifications1.setLabel("HW configuration");
        hardwareSpecifications1.setPricing(new Pricing(BigDecimal.valueOf(100), CurrencyType.CZK, RecurrencePeriodType.MONTH));
        serviceOffering.getPropertyGroups().add(createHWProperties(hardwareSpecifications1));
    }

    private PropertyGroup createManagementProperties(PropertyGroup managementSpecifications) {
        Property access = new Property("access", "FTP, rsync, SMB, SVN");
        Property monitoring = new Property("monitoring", "monitoring ICMP ping každých 5 minut");
        monitoring.setPricing(new Pricing(BigDecimal.valueOf(5), CurrencyType.CZK, RecurrencePeriodType.YEAR));
        managementSpecifications.setProperties(new ArrayList<>());
        managementSpecifications.getProperties().add(access);
        managementSpecifications.getProperties().add(monitoring);
        return managementSpecifications;
    }

    private PropertyGroup createBackupProperties(PropertyGroup backupSpecifications) {
        Property backup1 = new Property("backup space", 10, "GB");
        backup1.setPricing(new Pricing(BigDecimal.valueOf(3), CurrencyType.CZK, RecurrencePeriodType.MONTH));

        Property backup2 = new Property("backup space", 20, "GB");
        backup2.setPricing(new Pricing(BigDecimal.valueOf(5), CurrencyType.CZK, RecurrencePeriodType.MONTH));

        Property backup3 = new Property("backup space", 30, "GB");
        backup3.setName("backup space");
        backup3.setPricing(new Pricing(BigDecimal.valueOf(10), CurrencyType.CZK, RecurrencePeriodType.MONTH));

        Property backup4 = new Property("backup space", 40, "GB");
        backup4.setName("backup space");
        backup4.setPricing(new Pricing(BigDecimal.valueOf(15), CurrencyType.CZK, RecurrencePeriodType.MONTH));

        backupSpecifications.setProperties(new ArrayList<>());
        backupSpecifications.getProperties().add(backup1);
        backupSpecifications.getProperties().add(backup2);
        backupSpecifications.getProperties().add(backup3);
        backupSpecifications.getProperties().add(backup4);

        return backupSpecifications;
    }

    private PropertyGroup createSecurityPropertiesProperties(PropertyGroup securitySpecifications) {
        Property ssl = new Property("SSL service", "certifikát Rapid SSL");
        ssl.setPricing(new Pricing(BigDecimal.valueOf(100), CurrencyType.CZK, RecurrencePeriodType.YEAR));

        Property ssl2 = new Property("SSL service", "certifikát QuickSSL");
        ssl2.setPricing(new Pricing(BigDecimal.valueOf(200), CurrencyType.CZK, RecurrencePeriodType.YEAR));

        securitySpecifications.setProperties(new ArrayList<>());
        securitySpecifications.getProperties().add(ssl);
        securitySpecifications.getProperties().add(ssl2);

        return securitySpecifications;

    }

    private PropertyGroup createSWProperties(PropertyGroup softwareSpecifications) {
        Property database = new Property("database", "MS SQL 2012 Web edition");

        Property os1 = new Property("operation system", "CentOS");

        Property os2 = new Property("operation system", "Windows 2012 Server Datacenter (64 bit)");

        Property os3 = new Property("operation system", "Open Suse");

        Property os4 = new Property("operation system", "Win 2012 Server 64");
        os4.setPricing(new Pricing(BigDecimal.valueOf(50), CurrencyType.CZK, RecurrencePeriodType.ONCE));

        softwareSpecifications.setProperties(new ArrayList<>());
        softwareSpecifications.getProperties().add(database);
        softwareSpecifications.getProperties().add(os1);
        softwareSpecifications.getProperties().add(os2);
        softwareSpecifications.getProperties().add(os3);
        softwareSpecifications.getProperties().add(os4);

        return softwareSpecifications;
    }

    private PropertyGroup createHWProperties(PropertyGroup hardwareSpecifications1) {
        int[] cpuArr = {1,4,8};
        Property processor = new Property("CPU", cpuArr[randInt(0, 2)], "core");
        processor.setTagged(true);
        //ram
        int[] ramArr = {2,4,8,12};
        Property ram = new Property("RAM", ramArr[randInt(0, 3)], "GB");
        ram.setTagged(true);
        //hdd
        int[] hddArr = {5,10,50,100};
        Property hdd = new Property("HDD", hddArr[randInt(0, 3)], "GB");
        hdd.setTagged(true);
        //ip
        Property ip = new Property("IPv4", randInt(1, 10));
        //net
        Property connection = new Property("NET", randInt(1, 10));
        //model
        Property model = new Property("HW", "Dell R120");
        hardwareSpecifications1.setProperties(new ArrayList<>());

        hardwareSpecifications1.getProperties().add(processor);
        hardwareSpecifications1.getProperties().add(ram);
        hardwareSpecifications1.getProperties().add(hdd);
        hardwareSpecifications1.getProperties().add(ip);
        hardwareSpecifications1.getProperties().add(connection);
        hardwareSpecifications1.getProperties().add(model);

        return hardwareSpecifications1;
    }

    private int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    private double randDouble(int min, int max) {
        Random rand = new Random();
        return rand.nextDouble()*(max - min)+ min;
    }

    private List<Vendor> createVendors(){
        List<Vendor> vendors = new ArrayList<Vendor>();
        for (int i = 0; i < roundUp(getTestDataSize(),2); i++) {
            Vendor vendor = new Vendor();
            vendor.setName(faker.lorem().sentence(randInt(1, 3)));
            vendor.setDescription(faker.lorem().sentence(randInt(3, 10)));
            vendor.setKey(StringUtil.normalizeId(vendor.getName()));
            vendor = createVendor(vendor);
            UUID id = createBlob("server7.png", "vendor", vendor.getId(), "icon");
            vendor.setIcon(id.toString());
            updateVendor(vendor);
            vendors.add(vendor);
        }
        return  vendors;
    }

    private void createCategories(List<Vendor> vendors) {

        Category virtualServers = new Category();
        virtualServers.setName("Virtual servers");
        virtualServers.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Virtuální servery");
            put("EN", "Virtual servers");
        }}));
        virtualServers.setKey("virtual-servers");
        virtualServers.setDescription("A server, usually a Web server, that shares computer resources with other virtual servers. In this context, " +
                " the virtual part simply means that it is not a dedicated server-- that is, the entire computer is not " +
                " dedicated to running the server software.");
        virtualServers.setLocalizedDescription(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Je prostředí na kterém lze provozovat software na abstraktním stroji.");
            put("EN", "A server, usually a Web server, that shares computer resources with other virtual servers. In this context, " +
                    "the virtual part simply means that it is not a dedicated server-- that is, the entire computer is not " +
                    "dedicated to running the server software.");
        }}));
        virtualServers.setLabel("green");
        virtualServers = createCategory(virtualServers);
        UUID virtualServerIcon = createBlob("server1.png", "category", virtualServers.getId(), "icon");
        virtualServers.setIcon(virtualServerIcon.toString());
        updateCategory(virtualServers);
        createServiceItems(virtualServers, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createHardwareOptions(serviceItem);
                createSoftwareOptions(serviceItem);
                createBackupOptions(serviceItem);
                createSecurityOptions(serviceItem);
                createManagementOptions(serviceItem);
            }
        });

        Category dedicatedServers = new Category();
        dedicatedServers.setName("Dedikované servery");
        dedicatedServers.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Dedikované servery");
            put("EN", "Dedicated servers");
        }}));
        dedicatedServers.setKey("dedicated-servers");
        dedicatedServers.setDescription("A dedicated hosting service, dedicated server, or managed hosting service is a type of Internet hosting in which " +
                "the client leases an entire server not shared with anyone else. This is more flexible than shared hosting, as organizations " +
                "have full control over the server(s), including choice of operating system, hardware, etc.");
        dedicatedServers.setLocalizedDescription(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Server vyhrazený pro speciální účely, bez přímého přístupu uživatelů.");
            put("EN", "A dedicated hosting service, dedicated server, or managed hosting service is a type of Internet hosting in which " +
                    "the client leases an entire server not shared with anyone else. This is more flexible than shared hosting, as organizations " +
                    "have full control over the server(s), including choice of operating system, hardware, etc.");
        }}));
        dedicatedServers.setLabel("blue");
        dedicatedServers = createCategory(dedicatedServers);
        UUID dedicatedServersIcon = createBlob("server2.png", "category", dedicatedServers.getId(), "icon");
        dedicatedServers.setIcon(dedicatedServersIcon.toString());
        updateCategory(dedicatedServers);
        createServiceItems(dedicatedServers, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createHardwareOptions(serviceItem);
                createSoftwareOptions(serviceItem);
                createSecurityOptions(serviceItem);
                createManagementOptions(serviceItem);
            }
        });

        Category managedServers = new Category();
        managedServers.setLabel("gray");
        managedServers.setName("Managed servery");
        managedServers.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Managed servery");
            put("EN", "Managed servery");
        }}));
        managedServers.setKey("managed-servers");
        managedServers.setDescription("Managed servers are dedicated servers and we offer you services of our administrators " +
                "together with these servers. Server parameters can be selected from several variants or you can ask for individual " +
                "configuration customized according to your needs.");
        managedServers.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Výhodný pro náročné webové aplikace kde není dostačující (sdílený) webhosting.");
            put("EN", "Managed servers are dedicated servers and we offer you services of our administrators " +
                    " together with these servers. Server parameters can be selected from several variants or you can ask for individual " +
                    " configuration customized according to your needs.");
        }}));
        managedServers=createCategory(managedServers);
        UUID managedServersIcon = createBlob("server3.png", "category", managedServers.getId(), "icon");
        managedServers.setIcon(managedServersIcon.toString());
        updateCategory(managedServers);
        createServiceItems(managedServers, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createHardwareOptions(serviceItem);
                createBackupOptions(serviceItem);
                createManagementOptions(serviceItem);
            }
        });

        Category serverHousing = new Category();
        serverHousing.setDescription("Customer has their own server placed into providers server space.");
        serverHousing.setLocalizedDescription(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Zákazník svůj vlastní server umístí do prostor poskytovatele.");
            put("EN", "Customer has their own server placed into providers server space.");
        }}));
        serverHousing.setName("Server housing");
        serverHousing.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Server housing");
            put("EN", "Server housing");
        }}));
        serverHousing.setKey("server-housing");
        serverHousing.setLabel("purple");
        serverHousing=createCategory(serverHousing);
        UUID serverHousingIcon = createBlob("server4.png", "category", serverHousing.getId(), "icon");
        serverHousing.setIcon(serverHousingIcon.toString());
        updateCategory(serverHousing);
        createServiceItems(serverHousing, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createSoftwareOptions(serviceItem);
                createSecurityOptions(serviceItem);
                createManagementOptions(serviceItem);
            }
        });

        Category webHosting = new Category();
        webHosting.setName("Web hosting");
        webHosting.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Web hosting");
            put("EN", "Web hosting");
        }}));
        webHosting.setKey("web-hosting");
        webHosting.setDescription("A web hosting service is a type of Internet hosting service that allows individuals and organizations to make their website accessible via the World Wide Web.");
        webHosting.setLocalizedDescription(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Pronájem prostoru pro webové stránky na cizím serveru (poskytovatele).");
            put("EN", "A web hosting service is a type of Internet hosting service that allows individuals and organizations to make their website accessible via the World Wide Web.");
        }}));
        webHosting.setLabel("darkblue");
        webHosting=createCategory(webHosting);
        UUID webHostingIcon = createBlob("server5.png", "category", webHosting.getId(), "icon");
        webHosting.setIcon(webHostingIcon.toString());
        updateCategory(webHosting);
        createServiceItems(webHosting, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createHardwareOptions(serviceItem);
                createSoftwareOptions(serviceItem);
                createBackupOptions(serviceItem);
                createSecurityOptions(serviceItem);
                createManagementOptions(serviceItem);
            }
        });

        Category networkStorage = new Category();
        networkStorage.setLabel("darkgreen");
        networkStorage.setDescription("Network storage.");
        networkStorage.setLocalizedDescription(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Síťové úložiště.");
            put("EN", "Network storage.");
        }}));
        networkStorage.setName("Network data storage");
        networkStorage.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Network data storage");
            put("EN", "Network data storage");
        }}));
        networkStorage.setKey("network-data-storage");
        //filters
        networkStorage=createCategory(networkStorage);
        UUID networkStorageIcon = createBlob("server6.png", "category", networkStorage.getId(), "icon");
        networkStorage.setIcon(networkStorageIcon.toString());
        updateCategory(networkStorage);
        createServiceItems(networkStorage, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createHardwareOptions(serviceItem);
                createBackupOptions(serviceItem);
                createSecurityOptions(serviceItem);
                createManagementOptions(serviceItem);
            }
        });

        Category domains = new Category();
        domains.setName("Domains");
        domains.setLocalizedName(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Domény");
            put("EN", "Domains");
        }}));
        domains.setKey("domains");
        domains.setDescription("Search free domains.");
        domains.setLocalizedDescription(new LocalizedField(new HashMap<String, String>() {{
            put("CS", "Vyhledání volných domén.");
            put("EN", "Search free domains.");
        }}));
        domains.setLabel("darkgray");
        domains=createCategory(domains);
        UUID domainsIcon = createBlob("server7.png", "category", networkStorage.getId(), "icon");
        domains.setIcon(domainsIcon.toString());
        updateCategory(domains);
        createServiceItems(domains, vendors, new OptionFactory<ServiceOffering>() {
            @Override
            public void createOptions(ServiceOffering serviceItem) {
                createManagementOptions(serviceItem);
            }
        });

        List<UUID> categoriesCPU = new ArrayList<>();
        categoriesCPU.add(virtualServers.getId());
        categoriesCPU.add(managedServers.getId());
        List<UUID> categoriesHDD = new ArrayList<>();
        categoriesHDD.add(virtualServers.getId());
        categoriesHDD.add(managedServers.getId());
        categoriesHDD.add(serverHousing.getId());
        List<UUID> categoriesRAM = new ArrayList<>();
        categoriesRAM.add(virtualServers.getId());
        categoriesRAM.add(managedServers.getId());

        createCPUFilterGroup(categoriesCPU);
        createHddFilterGroup(categoriesHDD);
        createRAMFilterGroup(categoriesRAM);

    }

    private FilterGroup createRAMFilterGroup(List<UUID> categoriesRAM) {

        FilterGroup ramFilterVirtualServerGroup = new FilterGroup();
        ramFilterVirtualServerGroup.setLabel("RAM filter");
        ramFilterVirtualServerGroup = createFilter(new CategoryFilterGroup(categoriesRAM, ramFilterVirtualServerGroup)).getFilterGroup();

        Filter ram1 = new Filter();
        ram1.setLabel("with 2GB RAM");
        ram1.setPath("[propertyGroups].[properties]");
        ram1.setExpression(Expression.EQ);
        Set<Map> queryRam1 = new HashSet<>();
        queryRam1.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 2);
        }});
        ram1.setQuery(queryRam1);
        ram1.setType(Type.JSONB);
        ram1.setKey(StringUtil.normalizeId(ram1.getLabel()));
        ram1.setFilterGroupId(ramFilterVirtualServerGroup.getId());
        createFilterItem(ram1);


        Filter ram2 = new Filter();
        ram2.setLabel("with 4GB RAM");
        ram2.setExpression(Expression.EQ);
        ram2.setPath("[propertyGroups].[properties]");
        Set<Map> queryRam2 = new HashSet<>();
        queryRam2.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 4);
        }});
        ram2.setQuery(queryRam2);
        ram2.setType(Type.JSONB);
        ram2.setKey(StringUtil.normalizeId(ram2.getLabel()));
        ram2.setFilterGroupId(ramFilterVirtualServerGroup.getId());
        createFilterItem(ram2);

        Filter ram3 = new Filter();
        ram3.setLabel("with 8GB RAM");
        ram3.setExpression(Expression.EQ);
        ram3.setPath("[propertyGroups].[properties]");
        Set<Map> queryRam3 = new HashSet<>();
        queryRam3.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 8);
        }});
        ram3.setQuery(queryRam3);
        ram3.setType(Type.JSONB);
        ram3.setKey(StringUtil.normalizeId(ram3.getLabel()));
        ram3.setFilterGroupId(ramFilterVirtualServerGroup.getId());
        createFilterItem(ram3);

        Filter ram4 = new Filter();
        ram4.setLabel("with 12GB RAM");
        ram4.setExpression(Expression.EQ);
        ram4.setPath("[propertyGroups].[properties]");
        Set<Map> queryRam4 = new HashSet<>();
        queryRam4.add(new LinkedHashMap<String, Object>() {{
            put("name", "RAM");
            put("value", 12);
        }});
        ram4.setQuery(queryRam4);
        ram4.setType(Type.JSONB);
        ram4.setKey(StringUtil.normalizeId(ram4.getLabel()));
        ram4.setFilterGroupId(ramFilterVirtualServerGroup.getId());
        createFilterItem(ram4);

        return ramFilterVirtualServerGroup;
    }


    private FilterGroup createHddFilterGroup(List<UUID> categoriesHDD) {
        FilterGroup hddFilterVirtualServerGroups = new FilterGroup();
        hddFilterVirtualServerGroups.setLabel("HDD filter");
        hddFilterVirtualServerGroups = createFilter(new CategoryFilterGroup(categoriesHDD, hddFilterVirtualServerGroups)).getFilterGroup();

        Filter hdd1 = new Filter();
        hdd1.setLabel("with 5GB HDD");
        hdd1.setExpression(Expression.EQ);
        hdd1.setPath("[propertyGroups].[properties]");
        Set<Map> queryHdd1 = new HashSet<>();
        queryHdd1.add(new LinkedHashMap<String, Object>() {{
            put("name", "HDD");
            put("value", 5);
        }});
        hdd1.setQuery(queryHdd1);
        hdd1.setType(Type.JSONB);
        hdd1.setKey(StringUtil.normalizeId(hdd1.getLabel()));
        hdd1.setFilterGroupId(hddFilterVirtualServerGroups.getId());
        createFilterItem(hdd1);

        Filter hdd2 = new Filter();
        hdd2.setLabel("with 10GB HDD");
        hdd2.setExpression(Expression.EQ);
        hdd2.setPath("[propertyGroups].[properties]");
        Set<Map> queryHdd2 = new HashSet<>();
        queryHdd2.add(new LinkedHashMap<String, Object>() {{
            put("name", "HDD");
            put("value", 10);
        }});
        hdd2.setQuery(queryHdd2);
        hdd2.setType(Type.JSONB);
        hdd2.setKey(StringUtil.normalizeId(hdd2.getLabel()));
        hdd2.setFilterGroupId(hddFilterVirtualServerGroups.getId());
        createFilterItem(hdd2);

        Filter hdd3 = new Filter();
        hdd3.setLabel("with 50GB HDD");
        hdd3.setExpression(Expression.EQ);
        hdd3.setPath("[propertyGroups].[properties]");
        Set<Map> queryHdd3 = new HashSet<>();
        queryHdd3.add(new LinkedHashMap<String, Object>() {{
            put("name", "HDD");
            put("value", 50);
        }});
        hdd3.setQuery(queryHdd3);
        hdd3.setType(Type.JSONB);
        hdd3.setKey(StringUtil.normalizeId(hdd3.getLabel()));
        hdd3.setFilterGroupId(hddFilterVirtualServerGroups.getId());
        createFilterItem(hdd3);

        Filter hdd4 = new Filter();
        hdd4.setLabel("with 100GB HDD");
        hdd4.setExpression(Expression.EQ);
        hdd4.setPath("[propertyGroups].[properties]");
        Set<Map> queryHdd4 = new HashSet<>();
        queryHdd4.add(new LinkedHashMap<String, Object>() {{
            put("name", "HDD");
            put("value", 100);
        }});
        hdd4.setQuery(queryHdd4);
        hdd4.setType(Type.JSONB);
        hdd4.setKey(StringUtil.normalizeId(hdd4.getLabel()));
        hdd4.setFilterGroupId(hddFilterVirtualServerGroups.getId());
        createFilterItem(hdd4);
        return hddFilterVirtualServerGroups;
    }

    private FilterGroup createCPUFilterGroup(List<UUID> categoriesCPU) {
        // category virtual-servers filter groups
        FilterGroup cpuFilterVirtualServerGroups = new FilterGroup();
        cpuFilterVirtualServerGroups.setLabel("CPU filter");
        cpuFilterVirtualServerGroups = createFilter(new CategoryFilterGroup(categoriesCPU, cpuFilterVirtualServerGroups)).getFilterGroup();

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
        createFilterItem(cpu1);

        Filter cpu2 = new Filter();
        cpu2.setExpression(Expression.EQ);
        cpu2.setLabel("with 4 CPU");
        cpu2.setPath("[propertyGroups].[properties]");
        Set<Map> queryCpu2 = new HashSet<>();
        queryCpu2.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 4);
        }});
        cpu2.setQuery(queryCpu2);
        cpu2.setType(Type.JSONB);
        cpu2.setKey(StringUtil.normalizeId(cpu2.getLabel()));
        cpu2.setFilterGroupId(cpuFilterVirtualServerGroups.getId());
        createFilterItem(cpu2);

        Filter cpu3 = new Filter();
        cpu3.setLabel("with 8 CPU");
        cpu3.setExpression(Expression.EQ);
        cpu3.setPath("[propertyGroups].[properties]");
        Set<Map> queryCpu3 = new HashSet<>();
        queryCpu3.add(new LinkedHashMap<String, Object>() {{
            put("name", "CPU");
            put("value", 8);
        }});
        cpu3.setQuery(queryCpu3);
        cpu3.setType(Type.JSONB);
        cpu3.setKey(StringUtil.normalizeId(cpu3.getLabel()));
        cpu3.setFilterGroupId(cpuFilterVirtualServerGroups.getId());
        createFilterItem(cpu3);

        return cpuFilterVirtualServerGroups;
    }

}
