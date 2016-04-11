package org.cloudoholiq.catalog;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.runtime.Executable;
import org.junit.rules.ExternalResource;
import ru.yandex.qatools.embed.postgresql.Command;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.DownloadConfigBuilder;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.RuntimeConfigBuilder;
import ru.yandex.qatools.embed.postgresql.ext.ArtifactStoreBuilder;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;

public class PgRule extends ExternalResource {

    private static final Logger logger = Logger.getLogger(PgRule.class.getName());

    Executable exec;
    private static PostgresConfig config;

    @Override
    protected void before() throws Throwable {
        String databaseDir = System.getProperty("user.home") + System.getProperty("file.separator") + ".embedpostgresql"+ System.getProperty("file.separator") + "pg";
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.Postgres)
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.Postgres)
                        .tempDir(new FixedPath(databaseDir + System.getProperty("file.separator") + "runtime"))
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(Command.Postgres)
                                .progressListener(new LoggingProgressListener(logger, Level.ALL))
                                .build()))

                .build();

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(runtimeConfig);
//        PostgresStarter runtime = PostgresStarter.getDefaultInstance();

        config = new PostgresConfig(PRODUCTION, new AbstractPostgresConfig.Net("localhost", findFreePort()),
                new AbstractPostgresConfig.Storage("test", databaseDir), new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials("user", "password"));
        exec = runtime.prepare(config);
        exec.start();

    }

    public static PostgresConfig getPostgresConfig() {
        return config;
    }

    @Override
    protected void after() {
        exec.stop();
    }

    public static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException ignored) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException ignored) {
            // Ignore IOException on open
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                    // Ignore IOException on close()
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }
}
