package mo.capture.webActivity.server.handler;

import com.sun.net.httpserver.HttpExchange;
import mo.capture.webActivity.server.controller.ServerController;
import mo.capture.webActivity.server.handler.behavior.StopEndpoint;
import mo.capture.webActivity.server.utilities.Response;

public class StopHandler implements StopEndpoint {

    @Override
    public void stop(HttpExchange httpExchange) {
        ServerController.getInstance().stopCapture();
        String response = "Capture stopped";
        Response.sendResponse(response, 200, httpExchange);
    }
}
