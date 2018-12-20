package cl.informatica.usach.mo;

import cl.informatica.usach.mo.middleware.AllowedHttpMethods;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        HttpContext context = server.createContext("/", new MyHttpsHandler(now(), Router.getInstance()));
        context.getFilters().add(new AllowedHttpMethods());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Servidor montado en localhost:8000/");
    }

    public static String now(){
        Calendar calendar = Calendar.getInstance();
        Date date=calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(" dd_MM_YYYY_HH_mm_ss");
        return dateFormat.format(date);
    }
}
