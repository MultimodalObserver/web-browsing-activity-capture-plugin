package mo.capture.webActivity.server.handler;

import com.sun.net.httpserver.HttpExchange;
import mo.capture.webActivity.plugin.model.OutputFile;
import mo.capture.webActivity.server.util.Response;

import java.io.FileOutputStream;

public class SearchsHandler extends CaptureHandler{

    public SearchsHandler(){
        this.handledDataType = "searchs";
    }

    @Override
    public void store(HttpExchange exchange, OutputFile[] outputFiles, long captureTimestamp) {
        this.writeAndSendData(exchange.getRequestBody(), outputFiles, captureTimestamp);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
