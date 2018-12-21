package cl.informatica.usach.mo.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.util.stream.Stream;

public class BaseHandler {

    String outputFilePath = "output";
    String captureInitTimestamp = "";
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
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
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
}
