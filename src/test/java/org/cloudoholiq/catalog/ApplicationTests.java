package org.cloudoholiq.catalog;

import org.cloudoholiq.catalog.repository.BlobRepository;
import org.cloudoholiq.catalog.repository.CategoryRepository;
import org.cloudoholiq.catalog.repository.FilterGroupRepository;
import org.cloudoholiq.catalog.repository.FilterRepository;
import org.cloudoholiq.catalog.repository.ServiceOfferingRepository;
import org.cloudoholiq.catalog.repository.SortingRepository;
import org.cloudoholiq.catalog.repository.VendorRepository;
import org.cloudoholiq.catalog.repository.VisitEntryLogRepository;
import org.postgresql.ds.PGPoolingDataSource;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.spring.DBIFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import javax.sql.DataSource;

@SpringBootApplication()
public class ApplicationTests {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setBanner(new CloudoholiqBanner());
//        app.setShowBanner(false);
        app.run(args);

    }

    @Bean
    @Autowired
    public CategoryRepository categoryRepository(DBI dbi) {
        return dbi.onDemand(CategoryRepository.class);
    }

    @Bean
    @Autowired
    public FilterGroupRepository filterGroupRepository(DBI dbi) {
        return dbi.onDemand(FilterGroupRepository.class);
    }

    @Bean
    @Autowired
    public FilterRepository filterRepository(DBI dbi) {
        return dbi.onDemand(FilterRepository.class);
    }

    @Bean
    @Autowired
    public SortingRepository sortingRepository(DBI dbi) {
        return dbi.onDemand(SortingRepository.class);
    }

    @Bean
    @Autowired
    public BlobRepository imageRepository(DBI dbi) {
        return dbi.onDemand(BlobRepository.class);
    }

    @Bean
    @Autowired
    public ServiceOfferingRepository serviceOfferingRepository(DBI dbi) {
        return dbi.onDemand(ServiceOfferingRepository.class);
    }

    @Bean
    @Autowired
    public VendorRepository vendorRepository(DBI dbi) {
        return dbi.onDemand(VendorRepository.class);
    }

    @Bean
    @Autowired
    public VisitEntryLogRepository visitEntryLogRepository(DBI dbi) {
        return dbi.onDemand(VisitEntryLogRepository.class);
    }

    @Autowired
    @Bean
    public DBI dbi(DataSource dataSource) {
        synchronized (DBI.class) {
            return new DBI(dataSource);
        }
    }

    @Autowired
    @Bean
    public DBIFactoryBean dbiFactory(DataSource dataSource) {
        DBIFactoryBean dbiFactoryBean = new DBIFactoryBean();
        dbiFactoryBean.setDataSource(dataSource);
        return dbiFactoryBean;
    }

    @Autowired
    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public DataSource dataSource() {
        final PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName(PgRule.config().storage().dbName());
        source.setServerName(PgRule.config().net().host());
        source.setDatabaseName(PgRule.config().storage().dbName());
        source.setUser(PgRule.config().credentials().username());
        source.setPassword(PgRule.config().credentials().password());
        source.setPortNumber(PgRule.config().net().port());
        source.setMaxConnections(10);
        return source;

        //        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
//        final PostgresConfig config = PostgresConfig.defaultWithDbName("cloudoholiq_test");
//        PostgresExecutable exec = runtime.prepare(config);
//        PostgresProcess process = exec.start();
//
//        // connecting to a running Postgres
//        String url = format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
//                config.net().host(),
//                config.net().port(),
//                config.storage().dbName(),
//                config.credentials().username(),
//                config.credentials().password()
//        );
//        Connection conn = DriverManager.getConnection(url);
    }


}
