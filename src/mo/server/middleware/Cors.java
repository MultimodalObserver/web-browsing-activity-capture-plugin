package mo.server.middleware;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/* Clase que actua como filtro que responde las solicitudes de verificación de Cors antes de que lleguen al Router
*
*  Las peticiones de verificaciones son detectadas ya que usan el metodo http OPTIONS y contienen dos headers en específico.
*
*  El servidor solo responde con la info de CORS disponible:
*
*   - origenes aceptados.
*   - metodos http disponibles.
*   - headers 'extra' o custom  permitidos (solo Content-Type, ya que al ser su valor application/json,
*   los navegadores automáticamente implementan cors de verificación)
*
*  Luego de que responde el servidor, el navegador envía la verdadera petición http.
*
* Fuente: https://developer.mozilla.org/es/docs/Web/HTTP/Access_control_CORS#Solicitudes_Verificadas
*
* */
public class Cors extends Filter {

    private static final String DESCRIPTION = "Filter that handles OPTIONS request according to CORS verified requests";
    private static final String CORS_REQUEST_METHOD_HEADER = "Access-Control-Request-Method";
    private static final String CORS_REQUEST_HEADERS_HEADER = "Access-Control-Request-Headers";
    private static final List<String> ALLOWED_HTTP_METHODS = Arrays.asList("GET", "POST", "OPTIONS");
    private static final String ALLOWED_HTTP_CUSTOM_HEADERS = "Content-Type";
    public static final List<String> ALLOWED_ORIGINS = Arrays.asList("*"); /* PENSAR EN MECANISMO DE ALMACENAMIENTO
    DE IDS DE EXTENSIONES, PARA SOLO PERMITIRLAS A ELLAS COMO ORIGENES--> PROPERTIES??*/
    private String[] aux1;
    private String[] aux2;

    public Cors(){
        this.aux1 = new String[4];
        this.aux1[0] = CORS_REQUEST_METHOD_HEADER;
        this.aux1[1] = CORS_REQUEST_METHOD_HEADER.toLowerCase();
        this.aux1[2] = CORS_REQUEST_METHOD_HEADER.toUpperCase();
        this.aux1[3] = CORS_REQUEST_METHOD_HEADER.substring(0,1).toUpperCase() + CORS_REQUEST_METHOD_HEADER.substring(1);

        this.aux2 = new String[4];
        this.aux2[0] = CORS_REQUEST_HEADERS_HEADER;
        this.aux2[1] = CORS_REQUEST_HEADERS_HEADER.toLowerCase();
        this.aux2[2] = CORS_REQUEST_HEADERS_HEADER.toUpperCase();
        this.aux2[3] = CORS_REQUEST_HEADERS_HEADER.substring(0,1).toUpperCase() + CORS_REQUEST_HEADERS_HEADER.substring(1);
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {

        String httpMethod = exchange.getRequestMethod();
        Headers headers = exchange.getRequestHeaders();

        /* Validamos que sea una peticion de validacion CORS:

        * - Si no lo es, pasamos el control al siguiente filtro, que en este caso, como no hay mas,  llegará al
        * HTTPHandler, que es el Router.
        *
        * - Si lo es, respondemos con headers propios de CORS, para que el navegador cliente envie la verdadera peticion.*/

        if(!httpMethod.equals("OPTIONS") && !containsCORSValidationRequestHeaders(headers)){
            chain.doFilter(exchange);
            return;
        }

        String response = "OK";
        exchange.getResponseHeaders().put("Access-Control-Allow-Origin", ALLOWED_ORIGINS);
        exchange.getResponseHeaders().put("Access-Control-Allow-Methods", ALLOWED_HTTP_METHODS);
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", ALLOWED_HTTP_CUSTOM_HEADERS);
        exchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        exchange.close();
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    private boolean containsCORSValidationRequestHeaders(Headers headers){
        boolean contains1 = false;
        boolean contains2 = false;
        for(String string : this.aux1){
            if(headers.containsKey(string)){
                contains1 = true;
                break;
            }
        }
        for(String string: this.aux2){
            if(headers.containsKey(string)){
                contains2 = true;
                break;
            }
        }
        return contains1 && contains2;
    }
}
