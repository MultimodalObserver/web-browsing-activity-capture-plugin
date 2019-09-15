package mo.capture.webActivity.server.handler;

import mo.capture.webActivity.server.util.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;

public class MouseMovesHandler extends CaptureHandler{
    public MouseMovesHandler(){
        this.handledDataType = "mouseMoves";
    }
    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds, String outputFormat) {
        this.writeAndSendData(exchange.getRequestBody(), fileOutputStream, captureMilliseconds, outputFormat);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
