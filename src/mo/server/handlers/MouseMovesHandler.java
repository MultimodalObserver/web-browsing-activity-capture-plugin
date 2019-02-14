package mo.server.handlers;

import mo.communication.streaming.capture.PluginCaptureListener;
import mo.plugin.WebBrowsingActivityRecorder;
import mo.server.handlers.interfaces.CaptureEndpoint;
import mo.server.utilities.Response;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;
import java.util.List;

public class MouseMovesHandler extends CaptureHandler implements CaptureEndpoint {
    @Override
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds, List<PluginCaptureListener> dataListeners, String configurationId, WebBrowsingActivityRecorder recorder) {
        this.writeAndSendData(exchange.getRequestBody(), fileOutputStream, captureMilliseconds, dataListeners, configurationId, recorder);
        String response = "Mensaje recibido";
        Response.sendResponse(response,200, exchange);
    }
}
