package cl.informatica.usach.mo.middleware;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/* Esta clase es un filtro que actua como middleware, donde vemos que el metodo http del request sea
valido:

- Si no lo es, devolvemos una respuesta http con codigo 405, lo que quiere decir method not allowed,
por lo que terminamos la cadena de filtros, usando el exchange para devolver lo que se explica anteriormente.

- Si lo es, pasamos al siguiente filtro,
 */
public class AllowedHttpMethods extends Filter {

    private static final String[] REGISTERED_HTTP_METHODS = {"POST"};

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        boolean match = this.methodMatch(REGISTERED_HTTP_METHODS, requestMethod);
        if(!match){
            String response = "Method not allowed";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            exchange.close();
            return;
        }
        chain.doFilter(exchange);
        /* Si se quiere hacer un post-procesamiento del request, se hace despues de esta instruccion!!*/
    }

    @Override
    public String description() {
        return "Checks if the request method is allowed by the server";
    }

    private boolean methodMatch(String[] allowedMethods, String requestMethod){
        for(String method : allowedMethods){
            if(method.equals(requestMethod)){
                return true;
            }
        }
        return false;
    }
}
