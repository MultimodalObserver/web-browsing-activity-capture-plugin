package mo.server.handlers.interfaces;

import mo.communication.streaming.capture.PluginCaptureListener;
import mo.plugin.WebBrowsingActivityRecorder;
import mo.server.controllers.ServerController;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;
import java.util.List;

public interface CaptureEndpoint {

    void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds, List<PluginCaptureListener> dataListeners,
                      String configurationId, WebBrowsingActivityRecorder recorder);
}
