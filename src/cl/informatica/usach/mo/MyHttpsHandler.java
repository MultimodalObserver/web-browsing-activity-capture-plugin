package cl.informatica.usach.mo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

public class MyHttpsHandler implements HttpHandler {

    private String captureInitTimestamp;
    private Router router;

    public MyHttpsHandler(Router router){
        this.captureInitTimestamp = now();
        this.router = router;
    }

    @Override
    public void handle(HttpExchange exchange) {
        String uri = exchange.getRequestURI().toString();
        this.router.match(uri, exchange, this.captureInitTimestamp);
    }

    private static String now(){
        Calendar calendar = Calendar.getInstance();
        Date date=calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(" dd_MM_YYYY_HH_mm_ss");
        return dateFormat.format(date);
    }

}
