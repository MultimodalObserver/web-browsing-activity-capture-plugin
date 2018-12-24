package cl.informatica.usach.mo.controllers;

import cl.informatica.usach.mo.server.middleware.Cors;
import cl.informatica.usach.mo.server.router.Router;
import cl.informatica.usach.mo.views.ServerConfigurationView;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ServerController {

    public static final int UNKNOWN_HOST = 1;
    public static final int PORT_NOT_AVAILABLE = 2;
    public static final int CONNECTION_ESTABLISHED = 3;
    private String serverHost;
    private String serverPort;

    public void getServerConfiguration(){
        new ServerConfigurationView(this).show();
    }

    public int startServer(String serverIP, String serverPort){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return UNKNOWN_HOST;
        }
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(address, Integer.parseInt(serverPort)), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return PORT_NOT_AVAILABLE;
        }
        HttpContext context = server.createContext("/", Router.getInstance());
        context.getFilters().add(new Cors());
        server.setExecutor(null);
        server.start();
        this.serverHost = serverIP;
        this.serverPort = serverPort;
        return CONNECTION_ESTABLISHED;
    }

}
