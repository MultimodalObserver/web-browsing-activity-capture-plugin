package cl.informatica.usach.mo.server.utilities;

import cl.informatica.usach.mo.server.middleware.Cors;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class Response {

    public static  void sendResponse(String response,int status, HttpExchange exchange){
        exchange.getResponseHeaders().put("Access-Control-Allow-Origin", Cors.ALLOWED_ORIGINS);
        try {
            exchange.sendResponseHeaders(status, response.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream outputStream = exchange.getResponseBody();
        try {
            outputStream.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
