package mo.capture.webactivity.server.handlers.interfaces;

import mo.capture.webactivity.server.controllers.ServerController;
import com.sun.net.httpserver.HttpExchange;

public interface StartEndpoint {

    public void start(HttpExchange exchange, ServerController serverController);
}
