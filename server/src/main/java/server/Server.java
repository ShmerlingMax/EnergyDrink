package server;

import ok.dht.ServiceConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
    private static final int CLUSTER_SIZE = 3;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException,
            TimeoutException {

        int port = 8084;

        List<String> clusterUrls = new ArrayList<>(CLUSTER_SIZE);
        int[] ports = new int[CLUSTER_SIZE];
        for (int i = 0; i < CLUSTER_SIZE; i++) {
            ports[i] = port + i;
            String url = "http://localhost:" + ports[i];
            clusterUrls.add(url);
        }

        for (int i = 0; i < CLUSTER_SIZE; i++) {
            ServiceConfig config = new ServiceConfig(
                    ports[i],
                    clusterUrls.get(i),
                    clusterUrls,
                    Files.createTempDirectory("server" + i)
            );
            new ServiceImpl(config).start().get(1, TimeUnit.SECONDS);
        }
    }
}
