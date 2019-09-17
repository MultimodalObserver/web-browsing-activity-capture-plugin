package mo.capture.webActivity.server.handler.behavior;

import mo.capture.webActivity.plugin.model.OutputFile;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webActivity.plugin.WebBrowsingActivityRecorder;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;
import java.util.List;

public interface CaptureEndpoint {

    void store(HttpExchange exchange, OutputFile[] outputFiles, long captureTimestamp);
}
