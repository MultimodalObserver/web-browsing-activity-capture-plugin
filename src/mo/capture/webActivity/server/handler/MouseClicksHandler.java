package mo.capture.webActivity.server.handler;

import mo.capture.webActivity.server.util.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;

public class MouseClicksHandler extends CaptureHandler {

    public MouseClicksHandler(){
        this.handledDataType = "mouseClicks";
    }

    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds) {
        this.writeAndSendData(exchange.getRequestBody(), fileOutputStream, captureMilliseconds);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
