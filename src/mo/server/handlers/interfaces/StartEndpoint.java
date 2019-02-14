package mo.server.handlers.interfaces;

import mo.server.controllers.ServerController;
import com.sun.net.httpserver.HttpExchange;

public interface StartEndpoint {

    public void start(HttpExchange exchange, ServerController serverController);
}
