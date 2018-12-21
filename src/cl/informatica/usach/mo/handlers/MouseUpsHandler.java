package cl.informatica.usach.mo.handlers;

import cl.informatica.usach.mo.interfaces.RouteHandle;
import com.sun.net.httpserver.HttpExchange;

public class MouseUpsHandler extends BaseHandler implements RouteHandle {

    @Override
    public void handle(HttpExchange exchange, String captureInitTimestamp) {
        this.captureInitTimestamp = captureInitTimestamp;
        this.outputFilePath = "mouseUps";
        this.writeCaptureFile(exchange.getRequestBody());
        String response = "Mensaje recibido";
        this.sendResponse(response,200, exchange);
    }

}
