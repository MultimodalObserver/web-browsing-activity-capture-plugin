package mo.capture.webActivity.server.handler;

import mo.capture.webActivity.plugin.model.OutputFile;
import mo.capture.webActivity.server.util.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;

public class MouseUpsHandler extends CaptureHandler{

    public MouseUpsHandler(){
        this.handledDataType = "mouseUps";
    }

    @Override
    public void store(HttpExchange exchange, OutputFile[] outputFiles, long captureTimestamp) {
        this.writeAndSendData(exchange.getRequestBody(), outputFiles, captureTimestamp);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
