package org.cloudoholiq.catalog;

import de.flapdoodle.embed.process.runtime.Executable;
import org.junit.rules.ExternalResource;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import java.io.IOException;
import java.net.ServerSocket;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.PRODUCTION;

public class PgRule extends ExternalResource {

    Executable exec;
    private static PostgresConfig config;

    @Override
    protected void before() throws Throwable {
        PostgresStarter runtime = PostgresStarter.getDefaultInstance();
        config = new PostgresConfig(PRODUCTION, new AbstractPostgresConfig.Net("localhost", findFreePort()),
                new AbstractPostgresConfig.Storage("test"), new AbstractPostgresConfig.Timeout(),
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
