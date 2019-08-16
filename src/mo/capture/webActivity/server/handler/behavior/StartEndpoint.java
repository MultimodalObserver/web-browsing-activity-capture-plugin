package mo.capture.webActivity.server.handler.behavior;

import com.sun.net.httpserver.HttpExchange;

public interface StartEndpoint extends LifecycleEndpoint {
    void start(HttpExchange exchange);
}
