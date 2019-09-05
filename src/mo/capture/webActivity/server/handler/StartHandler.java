package mo.capture.webActivity.server.handler;

import mo.capture.webActivity.server.controller.ServerController;
import mo.capture.webActivity.server.handler.behavior.StartEndpoint;
import mo.capture.webActivity.server.router.Router;
import mo.capture.webActivity.server.util.Response;
import mo.capture.webActivity.util.DateHelper;
import com.sun.net.httpserver.HttpExchange;

public class StartHandler implements StartEndpoint {

    @Override
    public void start(HttpExchange exchange) {
        ServerController.getInstance().getRouter().setStatus(Router.RUNNING_STATUS);
        ServerController.getInstance().setReportDate(DateHelper.now());
        String response = "Captura iniciada";
        Response.sendResponse(response,200, exchange);
    }

}
