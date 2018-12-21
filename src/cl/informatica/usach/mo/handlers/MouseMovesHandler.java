package cl.informatica.usach.mo.handlers;

import com.sun.net.httpserver.HttpExchange;

public class MouseMovesHandler extends BaseHandler {

    /* Accedido por una ruta tipo POST, por tanto recibe el exchange y el timestamp de captura*/
    public void store(HttpExchange exchange, String captureInitTimestamp) {
        this.captureInitTimestamp = captureInitTimestamp;
        this.outputFilePath = "mouseMoves";
        this.writeCaptureFile(exchange.getRequestBody());
        String response = "Mensaje recibido";
        this.sendResponse(response,200, exchange);
    }
}
