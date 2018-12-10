package cl.informatica.usach.mo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

public class MyHttpsHandler implements HttpHandler {

    public static final String OUTPUT_FILE_PATH = "output";
    public static final String OUTPUT_FILE_EXTENSION = ".json";
    public String captureInitTimestamp;

    public MyHttpsHandler(String captureInitTimestamp){
        this.captureInitTimestamp = captureInitTimestamp;
    }

    @Override
    public void handle(HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        //print(inputStream);
        writeCaptureFile(inputStream);
        String response = "Mensaje recibido";
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        try {
            exchange.sendResponseHeaders(200, response.length());
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

    private void print(InputStream inputStream){
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Stream bodyStream =  bufferedReader.lines();
        bodyStream.forEach(line -> {
            System.out.println(line);
        });
    }

    private void writeCaptureFile(InputStream inputStream){
        String lineSeparator = System.getProperty("line.separator");
        String realOutputPath = OUTPUT_FILE_PATH + "_" + captureInitTimestamp + OUTPUT_FILE_EXTENSION;
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
}
