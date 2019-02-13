package cl.informatica.usach.mo.server.handlers.interfaces;

import cl.informatica.usach.mo.server.controllers.ServerController;
import com.sun.net.httpserver.HttpExchange;

public interface StartEndpoint {

    public void start(HttpExchange exchange, ServerController serverController);
}
