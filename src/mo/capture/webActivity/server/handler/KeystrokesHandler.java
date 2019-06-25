package mo.capture.webActivity.server.handler;

import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webActivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webActivity.server.handler.behavior.CaptureEndpoint;
import mo.capture.webActivity.server.utilities.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;
import java.util.List;

public class KeystrokesHandler extends CaptureHandler implements CaptureEndpoint {
    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds) {
        this.writeAndSendData(exchange.getRequestBody(), fileOutputStream, captureMilliseconds);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
