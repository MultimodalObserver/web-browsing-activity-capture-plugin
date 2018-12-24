package cl.informatica.usach.mo.server.handlers;

import cl.informatica.usach.mo.server.middleware.Cors;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/* Clase que implementa funcionalidades básicas para los demás handlers, por medio de herencia, como lo son:

    - Escritura de archivo de salida en base al contenido de una petición http valida de captura (POST).
    - Envío de respuesta HTTP al cliente (INCLUYE SETEO DE HEADER CORS!!).
    - Print de contenido recibido.
    - Obtención de query params de una petición HTTP.

    NO ES ABSTRACTA, YA QUE SE INSTANCIA Y UTILIZA PARA RESPONDER EN CASO DE ERROR DE RUTAS EN EL ROUTER.
 */
public class BaseHandler {

    String outputFilePath = "output"; //Este atributo es sobreescrito en clases hijas
    String captureInitTimestamp = ""; //Este atributo es sobreescrito en clases hijas
    private String outputFileExtension = ".json";

    void writeCaptureFile(InputStream inputStream){
        String lineSeparator = System.getProperty("line.separator");
        String realOutputPath = this.outputFilePath + "_" + this.captureInitTimestamp + this.outputFileExtension;
        File outputFile = new File(realOutputPath);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(outputFile, true);
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al crear wl writer");
            return;
        }
        bufferedWriter = new BufferedWriter(fileWriter);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        Object[] jsonArray = bodyStream.toArray();
        for (Object line : jsonArray){
            try {
                System.out.println("Voy a escribir: "+ String.valueOf(line));
                bufferedWriter.write(String.valueOf(line) + lineSeparator);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al escribir la linea:");
                System.out.println(line);
            }
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar el buffered writer");
            return;
        }
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar el file writer");
            return;
        }

        System.out.println("Archivo de salida actualizado correctamente");
    }

    private void print(InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        bodyStream.forEach(line -> {
            System.out.println(line);
        });
    }

    public void sendResponse(String response,int status, HttpExchange exchange){
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

    public static Map<String, String> parseQueryString(String qs) {
        Map<String, String> result = new HashMap<>();
        if (qs == null)
            return result;

        int last = 0, next, l = qs.length();
        while (last < l) {
            next = qs.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = qs.indexOf('=', last);
                try {
                    if (eqPos < 0 || eqPos > next)
                        result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                    else
                        result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e); // will never happen, utf-8 support is mandatory for java
                }
            }
            last = next + 1;
        }
        return result;
    }
}
