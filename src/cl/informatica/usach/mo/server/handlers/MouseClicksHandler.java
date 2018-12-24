package cl.informatica.usach.mo.server.handlers;

import com.sun.net.httpserver.HttpExchange;

public class MouseClicksHandler extends BaseHandler{

    /* Accedido por una ruta tipo POST, por tanto recibe el exchange y el timestamp de captura*/
    public void store(HttpExchange exchange, String captureInitTimestamp) {
        this.captureInitTimestamp = captureInitTimestamp;
        this.outputFilePath = "mouseClicks";
        this.writeCaptureFile(exchange.getRequestBody());
        String response = "Mensaje recibido";
        this.sendResponse(response,200, exchange);
    }
}
