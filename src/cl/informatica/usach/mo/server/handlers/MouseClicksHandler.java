package cl.informatica.usach.mo.server.handlers;

import cl.informatica.usach.mo.server.handlers.interfaces.CaptureEndpoint;
import cl.informatica.usach.mo.server.utilities.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;

public class MouseClicksHandler extends CaptureHandler implements CaptureEndpoint {

    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds) {
        this.writeCaptureFile(exchange.getRequestBody(), fileOutputStream, captureMilliseconds);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
