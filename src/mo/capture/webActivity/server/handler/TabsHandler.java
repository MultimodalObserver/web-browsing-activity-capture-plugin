package mo.capture.webActivity.server.handler;

import com.sun.net.httpserver.HttpExchange;
import mo.capture.webActivity.plugin.model.OutputFile;
import mo.capture.webActivity.server.util.Response;

import java.io.FileOutputStream;

public class TabsHandler extends CaptureHandler{

    public TabsHandler(){
        this.handledDataType = "tabs";
    }

    @Override
    public void store(HttpExchange exchange, OutputFile[] outputFiles, long captureMilliseconds) {
        this.writeAndSendData(exchange.getRequestBody(), outputFiles, captureMilliseconds);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
