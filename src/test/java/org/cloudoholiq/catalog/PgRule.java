package org.cloudoholiq.catalog;

import org.junit.rules.ExternalResource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_4;

public class PgRule extends ExternalResource {
    protected PostgresProcess process;
    protected static PostgresConfig config;
    @Override
    protected void before() throws Throwable {
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        config = new PostgresConfig(PRODUCTION, new AbstractPostgresConfig.Net(
                "localhost", 3578
        ), new AbstractPostgresConfig.Storage("cloudoholiq_test", "temp_cloudoholiq"), new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials("user", "password"));
        PostgresExecutable exec = runtime.prepare(config);
        process = exec.start();
    }

    @Override
    protected void after() {
        process.stop();
    }

    public static PostgresConfig config(){
        return config;
    }
}
