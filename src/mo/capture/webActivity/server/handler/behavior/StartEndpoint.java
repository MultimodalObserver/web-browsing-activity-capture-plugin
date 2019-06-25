package mo.capture.webActivity.server.handler.behavior;

import mo.capture.webActivity.server.controller.ServerController;
import com.sun.net.httpserver.HttpExchange;

public interface StartEndpoint {

    public void start(HttpExchange exchange);
}
