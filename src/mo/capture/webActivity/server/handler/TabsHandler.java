package mo.capture.webActivity.server.handler;

import com.sun.net.httpserver.HttpExchange;
import mo.capture.webActivity.server.handler.behavior.CaptureEndpoint;
import mo.capture.webActivity.server.utilities.Response;

import java.io.FileOutputStream;

public class TabsHandler extends CaptureHandler implements CaptureEndpoint{

    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds) {
        this.writeAndSendData(exchange.getRequestBody(), fileOutputStream, captureMilliseconds);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
