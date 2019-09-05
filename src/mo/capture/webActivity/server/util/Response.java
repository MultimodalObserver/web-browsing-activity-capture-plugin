package mo.capture.webActivity.server.util;

import mo.capture.webActivity.server.middleware.Cors;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class Response {

    public static  void sendResponse(String response,int status, HttpExchange exchange){
        exchange.getResponseHeaders().put("Access-Control-Allow-Origin", Cors.ALLOWED_ORIGINS);
        try {
            exchange.sendResponseHeaders(status, response.length());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        OutputStream outputStream = exchange.getResponseBody();
        try {
            outputStream.write(response.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return;
        }
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
