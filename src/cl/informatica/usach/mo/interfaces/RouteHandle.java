package cl.informatica.usach.mo.interfaces;

import com.sun.net.httpserver.HttpExchange;

public interface RouteHandle{

    public void handle(HttpExchange exchange, String captureInitTimestamp);

}