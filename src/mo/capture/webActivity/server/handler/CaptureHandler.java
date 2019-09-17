package mo.capture.webActivity.server.handler;

import com.google.gson.Gson;
import mo.capture.webActivity.plugin.model.*;
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


    void writeAndSendData(InputStream inputStream, OutputFile[] outputFiles, long captureTimestamp){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        Object[] jsonArray = bodyStream.toArray();
        /* DEBERIA SER UNA SOLA ITERACION EN TEORIA
        *
        *
        * VER LA FORMA
        */
        for(OutputFile outputFile : outputFiles){
            /* ESTO SE DEBE A QUE OUTPUT FILES ES UN ARREGLO Y ES RECORRIDO COMO SI FUERA UNA LISTA DINAMICA!!!*/
            if(outputFile == null){
                continue;
            }
            boolean toCSV = outputFile.getFormat().equals(Format.CSV.getValue());
            String elementSeparator = toCSV ? Separator.CSV_ROW_SEPARATOR.getValue() : Separator.JSON_SEPARATOR.getValue();
            for (Object line : jsonArray){
                String data = String.valueOf(line);
                CapturableAndConvertibleToCSV model = this.dataToModel(data, this.handledDataType, captureTimestamp);
                data = toCSV ? model.toCSV(Separator.CSV_COLUMN_SEPARATOR.getValue()) : gson.toJson(model);
                try {
                    outputFile.getOutputStream().write((data + elementSeparator).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                if(outputFile.getFormat().equals(Format.JSON.getValue())){
                    MessageSender.sendMessage(MESSAGE_CONTENT_KEY, gson.toJson(model), this.handledDataType);
                }
            }
        }
    }

    private CapturableAndConvertibleToCSV dataToModel(String data, String handledDataType, long captureTimestamp){
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
            model.setCaptureTimestamp(captureTimestamp);
        }
        return model;
    }
}
