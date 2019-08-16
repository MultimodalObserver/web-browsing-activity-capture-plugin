package mo.capture.webActivity.server.handler.behavior;

import com.sun.net.httpserver.HttpExchange;

public interface StopEndpoint extends LifecycleEndpoint {
    void stop(HttpExchange httpExchange);
}
