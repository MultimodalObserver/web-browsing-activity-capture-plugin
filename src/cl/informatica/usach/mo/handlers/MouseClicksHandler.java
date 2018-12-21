package cl.informatica.usach.mo.handlers;

import cl.informatica.usach.mo.interfaces.RouteHandle;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class MouseClicksHandler extends BaseHandler implements RouteHandle {

    @Override
    public void handle(HttpExchange exchange, String captureInitTimestamp) {
        this.captureInitTimestamp = captureInitTimestamp;
        this.outputFilePath = "mouseClicks";
        this.writeCaptureFile(exchange.getRequestBody());
        String response = "Mensaje recibido";
        this.sendResponse(response,200, exchange);
    }
}
