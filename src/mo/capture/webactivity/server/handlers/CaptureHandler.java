package mo.capture.webactivity.server.handlers;

import com.google.gson.Gson;
import mo.communication.streaming.capture.CaptureEvent;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webactivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webactivity.server.middleware.Cors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/* Clase que implementa funcionalidades básicas para los demás handlers, por medio de herencia, como lo son:

    - Escritura de archivo de salida en base al contenido de una petición http valida de captura (POST).
    - Envío de respuesta HTTP al cliente (INCLUYE SETEO DE HEADER CORS!!).
    - Print de contenido recibido.
    - Obtención de query params de una petición HTTP.
 */
public abstract class CaptureHandler {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String CAPTURE_MILLISECONDS_KEY = "captureMilliseconds";


    void writeAndSendData(InputStream inputStream, FileOutputStream fileOutputStream, long captureMilliseconds,
                          List<PluginCaptureListener> dataListeners, String configurationId,
                          WebBrowsingActivityRecorder recorder){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        Object[] jsonArray = bodyStream.toArray();
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        for (Object line : jsonArray){
            try {
                //System.out.println("Voy a escribir: "+ String.valueOf(line));
                JsonObject jsonObject = parser.parse(String.valueOf(line)).getAsJsonObject();
                jsonObject.add(CAPTURE_MILLISECONDS_KEY, new JsonPrimitive(captureMilliseconds));
                String realLine = gson.toJson(jsonObject) + LINE_SEPARATOR;
                fileOutputStream.write(realLine.getBytes());
                if(dataListeners == null){
                    return;
                }
                CaptureEvent captureEvent = new CaptureEvent(configurationId, recorder.getClass().getName(), realLine);
                for(PluginCaptureListener dataListener : dataListeners){
                    dataListener.onDataReceived(recorder, captureEvent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("Error al escribir la linea:");
                //System.out.println(line);
            }
        }
        //System.out.println("Archivo de salida cerrado correctamente");
    }

    private void print(InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        bodyStream.forEach(line -> {
            System.out.println(line);
        });
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
