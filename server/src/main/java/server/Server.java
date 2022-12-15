package server;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException,
            TimeoutException {

        int port = 8080;
        new ServiceImpl().start().get(1, TimeUnit.SECONDS);
    }
}
