package cl.informatica.usach.mo;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        server.createContext("/capture", new MyHttpsHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Servidor montado en localhost:8000/capture");
    }
}
