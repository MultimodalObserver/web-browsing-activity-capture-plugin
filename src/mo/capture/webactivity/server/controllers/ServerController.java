package mo.capture.webactivity.server.controllers;

import com.google.gson.Gson;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webactivity.plugin.WebBrowsingActivityConfiguration;
import mo.capture.webactivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webactivity.server.middleware.Cors;
import mo.capture.webactivity.server.router.Router;
import mo.capture.webactivity.server.views.ServerConfigurationView;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import mo.organization.FileDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerController {

    public static final int UNKNOWN_HOST = 1;
    public static final int PORT_NOT_AVAILABLE = 2;
    public static final int CONNECTION_ESTABLISHED = 3;
    private Router router;
    private HttpServer server;
    private InetSocketAddress inetSocketAddress;

    /*Para el manejo de almacenamiento de los datos capturados*/
    private File captureStageFolder;
    private File reportFolder;
    private WebBrowsingActivityConfiguration configuration;
    private Map<String, Map<String, Object>> outputFilesMap;
    private static final String OUTPUT_FILE_EXTENSION = ".json";
    private String reportDate;
    private static final Class CREATOR_CLASS = WebBrowsingActivityRecorder.class;
    public static final String OUTPUT_FILE_KEY = "outputFile";
    public static final String OUTPUT_FILE_DESCRIPTION_KEY = "outputFileDescription";
    public static final String FILE_OUTPUT_STREAM_KEY = "fileOutputStream";
    private static final String MAP_FILE_NAME = "web_activity";

    /*Para el manejo de la transmision de los datos capturados*/
    private List<PluginCaptureListener> dataListeners;
    private WebBrowsingActivityRecorder recorder;
    private static final Logger LOGGER = Logger.getLogger(ServerController.class.getName());

    public ServerController(WebBrowsingActivityRecorder recorder){
        this.captureStageFolder = recorder.getStageFolder();
        this.dataListeners = recorder.getDataListeners();
        this.configuration = recorder.getWebBrowsingActivityConfiguration();
        this.recorder = recorder;
        this.router = null;
        this.server = null;
        this.inetSocketAddress = null;
        this.outputFilesMap = new HashMap<>();
        this.reportDate = null;
        this.reportFolder = null;
    }

    public void getServerConfiguration(){
        new ServerConfigurationView(this).show();
    }

    public int startServer(String serverIP, String serverPort){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return UNKNOWN_HOST;
        }
        try {
            this.inetSocketAddress = new InetSocketAddress(address, Integer.parseInt(serverPort));
            this.server = HttpServer.create(this.inetSocketAddress, 0);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
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
        String realFileName = this.reportDate + "_" + this.configuration.getId() + fileName.replace("/", "")
                + OUTPUT_FILE_EXTENSION;
        /* Creamos un subdirectorio de nombre report date*/
        String parentPath = this.captureStageFolder.getAbsolutePath();
        this.reportFolder = new File(parentPath + System.getProperty("file.separator") + reportDate +
                "_" + MAP_FILE_NAME);
        this.reportFolder.mkdir();
        /*if(!folderCreated){
            LOGGER.log(Level.SEVERE, "An error occurred while trying to create the: " + reportDate + " folder");
            return null;
        }
        */
        File outputFile = new File(this.reportFolder, realFileName);
        FileOutputStream fileOutputStream = null;
        if(this.outputFilesMap.containsKey(fileName)){
            return (FileOutputStream) this.outputFilesMap.get(fileName).get(FILE_OUTPUT_STREAM_KEY);
        }
        try {
            outputFile.createNewFile();
            fileOutputStream = new FileOutputStream(outputFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return null;
        }
        Map<String, Object> outputFileMap = new HashMap<>();
        outputFileMap.put(OUTPUT_FILE_KEY, outputFile);
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
                FileOutputStream outputStream = (FileOutputStream) subMap.get(FILE_OUTPUT_STREAM_KEY);
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    break;
                }
                outputFile.delete();
        }
        this.reportFolder.delete();
        this.outputFilesMap.clear();
    }

    private void closeFileOutputStreams(){
        for (Object key : this.outputFilesMap.keySet()) {
            Map<String, Object> subMap = this.outputFilesMap.get(key);
            FileOutputStream outputStream = (FileOutputStream) subMap.get(FILE_OUTPUT_STREAM_KEY);
            try {
                outputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
                break;
            }
        }
    }


    private void writeMapFile(){
        String mapFileName = this.reportDate + "_" + MAP_FILE_NAME + OUTPUT_FILE_EXTENSION;
        File mapFile = new File(this.captureStageFolder, mapFileName);
        FileOutputStream mapFileOutputStream = null;
        try {
            mapFile.createNewFile();
            mapFileOutputStream = new FileOutputStream(mapFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "", e);
            return;
        }
        Map <String, String> map = new HashMap<>();
        for (Object key : this.outputFilesMap.keySet()){
            String auxKey = (String) key;
            Map<String, Object> subMap = this.outputFilesMap.get(key);
            File outputFile = (File) subMap.get(OUTPUT_FILE_KEY);
            map.put( auxKey.replace("/", ""), outputFile.getAbsolutePath());
        }
        Gson gson = new Gson();
        String mapString = gson.toJson(map);
        try {
            mapFileOutputStream.write(mapString.getBytes());
            mapFileOutputStream.close();
            new FileDescription(mapFile, this.recorder.getClass().getName());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    public void stopServer(int delay, boolean isCancelAction){
        /* AQUI hay que cerrar todos los archivos que se hayan abierto*/
        if(!isCancelAction){
            this.closeFileOutputStreams();
            /* Una vez cerramos todos los streamos de los archivos, creamos el verdadero archivo que será utilizado por MO
            el que contiene un mapa que nos indica donde estan almacenados los archivos correspondientes a cada captura.

            Luego, cuando otro plugin requiera usar la info de estos archivo, solo debera acceder a este archivo final
            para encontrar y abrir los archivos que contienen los datos correspondientes.

            Esto debido a que MO solo permite abrir un archivo al mismo tiempo (en el caso de visualización)
            */
            this.writeMapFile();
        }
        else{
            this.deleteOutputFiles();
        }
        this.server.stop(delay);
    }

    public List<PluginCaptureListener> getDataListeners() {
        return this.dataListeners;
    }

    public WebBrowsingActivityRecorder getRecorder(){
        return this.recorder;
    }

    public String getConfigurationId(){
        return this.configuration.getId();
    }
}
