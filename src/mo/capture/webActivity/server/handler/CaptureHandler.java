package mo.capture.webActivity.server.handler;

import com.google.gson.Gson;
import mo.capture.webActivity.plugin.model.*;
import mo.capture.webActivity.server.controller.ServerController;
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
    private static final Gson gson = new Gson();
    public String handledDataType;


    void writeAndSendData(InputStream inputStream, FileOutputStream fileOutputStream, long captureMilliseconds,
                          String outputFormat){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        Object[] jsonArray = bodyStream.toArray();
        boolean toCSV = outputFormat.equals(Format.CSV.getValue());
        String elementSeparator = toCSV ? Separator.CSV_ROW_SEPARATOR.getValue() : Separator.JSON_SEPARATOR.getValue();
        /* DEBERIA SER UNA SOLA ITERACION EN TEORIA
        *
        */
        for (Object line : jsonArray){
            String data = String.valueOf(line);
            CapturableAndConvertibleToCSV model = this.dataToModel(data, this.handledDataType, captureMilliseconds);
            data = toCSV ? model.toCSV(Separator.CSV_COLUMN_SEPARATOR.getValue()) : gson.toJson(model);
            try {
                fileOutputStream.write((data + elementSeparator).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            MessageSender.sendMessage(MESSAGE_CONTENT_KEY, gson.toJson(model), this.handledDataType);
        }
    }

    private CapturableAndConvertibleToCSV dataToModel(String data, String handledDataType, long captureMilliseconds){
        CapturableAndConvertibleToCSV model;
        switch(handledDataType){
            case "keystrokes":
                model = gson.fromJson(data, Keystroke.class);
                break;
            case "mouseMoves":
                model = gson.fromJson(data, MouseMove.class);
                break;
            case "mouseClicks":
                model = gson.fromJson(data, MouseClick.class);
                break;
            case "mouseUps":
                model = gson.fromJson(data, MouseUp.class);
                break;
            case "searchs":
                model = gson.fromJson(data, SearchAction.class);
                break;
            case "tabs":
                model = gson.fromJson(data, TabAction.class);
                break;
            default:
                model = null;
        }
        if(model != null){
            model.setCaptureMilliseconds(captureMilliseconds);
        }
        return model;
    }
}
