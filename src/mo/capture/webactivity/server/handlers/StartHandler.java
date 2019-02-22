package mo.capture.webactivity.server.handlers;

import mo.capture.webactivity.server.controllers.ServerController;
import mo.capture.webactivity.server.handlers.interfaces.StartEndpoint;
import mo.capture.webactivity.server.router.Router;
import mo.capture.webactivity.server.utilities.Response;
import mo.capture.webactivity.utilities.DateHelper;
import com.sun.net.httpserver.HttpExchange;

import java.util.Date;

public class StartHandler implements StartEndpoint {

    @Override
    public void start(HttpExchange exchange, ServerController serverController) {
        serverController.getRouter().setStatus(Router.RUNNING_STATUS);
        serverController.setReportDate(DateHelper.now());
        String response = "Captura iniciada";
        Response.sendResponse(response,200, exchange);
    }

}
