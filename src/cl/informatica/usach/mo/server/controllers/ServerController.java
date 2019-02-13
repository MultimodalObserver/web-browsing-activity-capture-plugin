package cl.informatica.usach.mo.server.controllers;

import cl.informatica.usach.mo.plugin.WebBrowsingActivityConfiguration;
import cl.informatica.usach.mo.plugin.WebBrowsingActivityRecorder;
import cl.informatica.usach.mo.server.middleware.Cors;
import cl.informatica.usach.mo.server.router.Router;
import cl.informatica.usach.mo.server.views.ServerConfigurationView;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import mo.organization.FileDescription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

public class ServerController {

    public static final int UNKNOWN_HOST = 1;
    public static final int PORT_NOT_AVAILABLE = 2;
    public static final int CONNECTION_ESTABLISHED = 3;
    private Router router;
    private HttpServer server;
    private InetSocketAddress inetSocketAddress;

    /*Para el manejo de almacenamiento de los datos capturados*/
    private File captureStageFolder;
    private WebBrowsingActivityConfiguration configuration;
    private Map<String, Map<String, Object>> outputFilesMap;
    private static final String OUTPUT_FILE_EXTENSION = ".json";
    private String reportDate;
    private static final Class CREATOR_CLASS = WebBrowsingActivityRecorder.class;
    public static final String OUTPUT_FILE_KEY = "outputFile";
    public static final String OUTPUT_FILE_DESCRIPTION_KEY = "outputFileDescription";
    public static final String FILE_OUTPUT_STREAM_KEY = "fileOutputStream";

    public ServerController(File captureStageFolder, WebBrowsingActivityConfiguration configuration){
        this.captureStageFolder = captureStageFolder;
        this.configuration = configuration;
        this.router = null;
        this.server = null;
        this.inetSocketAddress = null;
        this.outputFilesMap = new HashMap<>();
        this.reportDate = null;
    }

    public void getServerConfiguration(){
        new ServerConfigurationView(this).show();
    }

    public int startServer(String serverIP, String serverPort){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return UNKNOWN_HOST;
        }
        try {
            this.inetSocketAddress = new InetSocketAddress(address, Integer.parseInt(serverPort));
            this.server = HttpServer.create(this.inetSocketAddress, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return PORT_NOT_AVAILABLE;
        }
        this.router = new Router(this);
        HttpContext context = server.createContext("/", this.router);
        context.getFilters().add(new Cors());
        this.server.setExecutor(null);
        this.server.start();
        this.router.setStatus(Router.MOUNTED_STATUS);
        return CONNECTION_ESTABLISHED;
    }

    public Router getRouter() {
        return router;
    }

    public HttpServer getServer() {
        return server;
    }

    public void setServer(HttpServer server) {
        this.server = server;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setReportDate(String reportDate){
        this.reportDate = reportDate;
    }
    public FileOutputStream createOrGetOutputFile(String fileName){
        String realFileName = this.reportDate + "_" + this.configuration.getId() + fileName + OUTPUT_FILE_EXTENSION;
        File outputFile = new File(this.captureStageFolder, realFileName);
        FileDescription fileDescription = null;
        FileOutputStream fileOutputStream = null;
        if(this.outputFilesMap.containsKey(fileName)){
            return (FileOutputStream) this.outputFilesMap.get(fileName).get(FILE_OUTPUT_STREAM_KEY);
        }
        try {
            outputFile.createNewFile();
            fileDescription = new FileDescription(outputFile, CREATOR_CLASS.getName());
            fileOutputStream = new FileOutputStream(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Map<String, Object> outputFileMap = new HashMap<>();
        outputFileMap.put(OUTPUT_FILE_KEY, outputFile);
        outputFileMap.put(OUTPUT_FILE_DESCRIPTION_KEY, fileDescription);
        outputFileMap.put(FILE_OUTPUT_STREAM_KEY, fileOutputStream);
        this.outputFilesMap.put(fileName, outputFileMap);
        return fileOutputStream;
    }

    private void deleteOutputFiles(){
        for (Object key : this.outputFilesMap.keySet()) {
            Map<String, Object> subMap = this.outputFilesMap.get(key);
                File outputFile = (File) subMap.get(OUTPUT_FILE_KEY);
                if (!outputFile.isFile()) {
                    break;
                }
                outputFile.delete();
                FileDescription outputFileDescription = (FileDescription) subMap.get(OUTPUT_FILE_DESCRIPTION_KEY);
                if (!outputFileDescription.getDescriptionFile().isFile()) {
                    break;
                }
                outputFileDescription.deleteFileDescription();
            }
        this.outputFilesMap.clear();
    }

    private void closeFileOutputStreams(){
        for (Object key : this.outputFilesMap.keySet()) {
            Map<String, Object> subMap = this.outputFilesMap.get(key);
                FileOutputStream outputFile = (FileOutputStream) subMap.get(FILE_OUTPUT_STREAM_KEY);
            try {
                outputFile.close();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void stopServer(int delay, boolean isCancelAction){
        /* AQUI hay que cerrar todos los archivos que se hayan abierto*/
        if(!isCancelAction){
            this.closeFileOutputStreams();
        }
        else{
            this.deleteOutputFiles();
        }
        this.server.stop(delay);
    }
}
