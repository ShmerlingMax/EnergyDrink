package server;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {

    private static final Log LOGGER = LogFactory.getLog(Server.class);

    public static void main(String[] args) {
        Service service = new ServiceImpl();
        try {
            service.start().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            service.stop();
            LOGGER.error(e);
        }
    }
}
