package mo.capture.webActivity.server.handler.behavior;

import com.sun.net.httpserver.HttpExchange;

public interface StopEndpoint {
    void stop(HttpExchange httpExchange);
}
