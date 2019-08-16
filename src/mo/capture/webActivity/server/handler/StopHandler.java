package mo.capture.webActivity.server.handler;

import com.sun.net.httpserver.HttpExchange;
import mo.capture.webActivity.server.controller.ServerController;
import mo.capture.webActivity.server.handler.behavior.StopEndpoint;
import mo.capture.webActivity.server.utilities.Response;

public class StopHandler implements StopEndpoint {

    @Override
    public void stop(HttpExchange httpExchange) {
        ServerController.getInstance().stopServer(0, false);
        /* como puedo detener la ejecucion del plugin con MO ??*/
        String response = "server stopped";
        Response.sendResponse(response, 200, httpExchange);
    }
}
