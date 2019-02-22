package mo.capture.webactivity.server.handlers.interfaces;

import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webactivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webactivity.server.controllers.ServerController;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;
import java.util.List;

public interface CaptureEndpoint {

    void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds, List<PluginCaptureListener> dataListeners,
                      String configurationId, WebBrowsingActivityRecorder recorder);
}
