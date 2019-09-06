package mo.capture.webActivity.server.handler;

import com.google.gson.Gson;
import mo.capture.webActivity.plugin.model.DataMessage;
import mo.capture.webActivity.server.handler.behavior.CaptureEndpoint;
import mo.capture.webActivity.util.MessageSender;

import java.io.*;
import java.util.stream.Stream;

/* Clase que implementa funcionalidades básicas para los demás handler, por medio de herencia, como lo son:

    - Escritura de archivo de salida en base al contenido de una petición http valida de captura (POST).
    - Envío de respuesta HTTP al cliente (INCLUYE SETEO DE HEADER CORS!!).
    - Print de contenido recibido.
    - Obtención de query params de una petición HTTP.
 */
public abstract class CaptureHandler implements CaptureEndpoint {

    private static final String MESSAGE_CONTENT_KEY = "data";
    public static final String COMMA_SEPARATOR = ",";
    private static final Gson gson = new Gson();
    public String handledDataType;


    void writeAndSendData(InputStream inputStream, FileOutputStream fileOutputStream, long captureMilliseconds){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        Object[] jsonArray = bodyStream.toArray();
        for (Object line : jsonArray){
            try {
                //System.out.println("Voy a escribir: "+ String.valueOf(line));
                DataMessage dataMessage = new DataMessage();
                dataMessage.setCaptureMilliseconds(captureMilliseconds);
                dataMessage.setData(String.valueOf(line));
                fileOutputStream.write((gson.toJson(dataMessage) + COMMA_SEPARATOR).getBytes());
                dataMessage.setDataType(this.handledDataType);
                MessageSender.sendMessage(MESSAGE_CONTENT_KEY, gson.toJson(dataMessage));
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("Error al escribir la linea:");
                //System.out.println(line);
            }
        }
        //System.out.println("Archivo de salida cerrado correctamente");
    }
}
