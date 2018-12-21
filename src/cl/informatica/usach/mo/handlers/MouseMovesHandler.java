package cl.informatica.usach.mo.handlers;

import cl.informatica.usach.mo.interfaces.RouteHandle;
import com.sun.net.httpserver.HttpExchange;

public class MouseMovesHandler extends BaseHandler implements RouteHandle {

    @Override
    public void handle(HttpExchange exchange, String captureInitTimestamp) {
        this.captureInitTimestamp = captureInitTimestamp;
        this.outputFilePath = "mouseMoves";
        this.writeCaptureFile(exchange.getRequestBody());
        String response = "Mensaje recibido";
        this.sendResponse(response,200, exchange);
    }
}
