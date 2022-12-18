package server;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException,
            TimeoutException {
        Service service = new ServiceImpl();
        try {
            service.start().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            service.stop();
            throw e;
        }
    }
}
