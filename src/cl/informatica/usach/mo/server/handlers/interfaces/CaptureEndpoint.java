package cl.informatica.usach.mo.server.handlers.interfaces;

import cl.informatica.usach.mo.server.controllers.ServerController;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileOutputStream;

public interface CaptureEndpoint {
    public void store(HttpExchange exchange, FileOutputStream fileOutputStream, long captureMilliseconds);
}
