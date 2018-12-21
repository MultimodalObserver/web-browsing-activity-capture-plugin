package cl.informatica.usach.mo.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class KeystrokesHandler extends BaseHandler {

    /* Accedido por una ruta tipo POST, por tanto recibe el exchange y el timestamp de captura*/
    public void store(HttpExchange exchange, String captureInitTimestamp) {
        this.captureInitTimestamp = captureInitTimestamp;
        this.outputFilePath = "keystrokes";
        this.writeCaptureFile(exchange.getRequestBody());
        String response = "Mensaje recibido";
        this.sendResponse(response,200, exchange);
    }

}
