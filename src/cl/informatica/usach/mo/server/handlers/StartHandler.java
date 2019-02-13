package cl.informatica.usach.mo.server.handlers;

import cl.informatica.usach.mo.server.controllers.ServerController;
import cl.informatica.usach.mo.server.handlers.interfaces.StartEndpoint;
import cl.informatica.usach.mo.server.router.Router;
import cl.informatica.usach.mo.server.utilities.Response;
import cl.informatica.usach.mo.utilities.DateHelper;
import com.sun.net.httpserver.HttpExchange;

public class StartHandler implements StartEndpoint {

    @Override
    public void start(HttpExchange exchange, ServerController serverController) {
        serverController.getRouter().setStatus(Router.RUNNING_STATUS);
        String response = "Captura iniciada";
        Response.sendResponse(response,200, exchange);
    }

}
