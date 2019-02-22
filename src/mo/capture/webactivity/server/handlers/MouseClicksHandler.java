package mo.capture.webactivity.server.handlers;

import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webactivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webactivity.server.handlers.interfaces.CaptureEndpoint;
import mo.capture.webactivity.server.utilities.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;
import java.util.List;

public class MouseClicksHandler extends CaptureHandler implements CaptureEndpoint {

    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds, List<PluginCaptureListener> dataListeners, String configurationId, WebBrowsingActivityRecorder recorder) {
        this.writeAndSendData(exchange.getRequestBody(), fileOutputStream, captureMilliseconds, dataListeners, configurationId, recorder);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
