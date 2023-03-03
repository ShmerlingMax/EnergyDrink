package server;

import one.nio.http.HttpServer;
import one.nio.http.HttpServerConfig;
import one.nio.server.AcceptorConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final Log LOGGER = LogFactory.getLog(Main.class);

    public static void main(String[] args) {
        try {
            URL shopsResource = Main.class.getClassLoader().getResource("shopsMock.txt");
            if (shopsResource == null) {
                LOGGER.error("Shops resources not get");
                return;
            }
            URL brandsResources = Main.class.getClassLoader().getResource("brandsMock.txt");
            if (brandsResources == null) {
                LOGGER.error("Brands resources not get");
                return;
            }

            String shops = Files.readString(Paths.get(shopsResource.toURI()));
            String brands = Files.readString(Paths.get(brandsResources.toURI()));
            HttpServer server = new TestHttpServer(createConfigFromPort(8081), shops, brands);
            server.start();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static HttpServerConfig createConfigFromPort(int port) {
        HttpServerConfig httpConfig = new HttpServerConfig();
        AcceptorConfig acceptor = new AcceptorConfig();
        acceptor.port = port;
        acceptor.reusePort = true;
        httpConfig.acceptors = new AcceptorConfig[]{acceptor};
        return httpConfig;
    }
}