package mo.capture.webActivity.server.controller;

import com.google.gson.Gson;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webActivity.plugin.WebBrowsingActivityConfiguration;
import mo.capture.webActivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webActivity.server.middleware.Cors;
import mo.capture.webActivity.server.router.Router;
import mo.capture.webActivity.server.views.ServerConfigurationView;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerController {

    private static ServerController instance = null;

    public static final int UNKNOWN_HOST = 1;
    public static final int PORT_NOT_AVAILABLE = 2;
    public static final int INVALID_PORT = 3;
    public static final int CONNECTION_ESTABLISHED = 4;
    private Router router;
    private HttpServer server;
    private InetSocketAddress inetSocketAddress;

    /*Para el manejo de almacenamiento de los datos capturados*/
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

    private ServerController(){
        //this.captureStageFolder = recorder.getStageFolder();
        router = null;
        server = null;
        this.inetSocketAddress = null;
        this.outputFilesMap = new HashMap<>();
        this.reportDate = null;
        this.reportFolder = null;
    }

    public static ServerController getInstance(){
        if(instance == null){
            instance = new ServerController();
        }
        return instance;
    }

    public void getServerConfiguration(){
        new ServerConfigurationView(this).show();
    }

    public int startServer(String serverIP, String serverPort){
        InetAddress address = null;
        try {
            address = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            //LOGGER.log(Level.SEVERE, null, e);
            return UNKNOWN_HOST;
        }
        try {
            this.inetSocketAddress = new InetSocketAddress(address, Integer.parseInt(serverPort));
            server = HttpServer.create(this.inetSocketAddress, 0);
        } catch (IOException e) {
            //LOGGER.log(Level.SEVERE, null, e);
            return PORT_NOT_AVAILABLE;
        }
        catch(NumberFormatException e){
            //LOGGER.log(Level.SEVERE, null, e);
            return INVALID_PORT;
        }
        router = new Router(this);
        HttpContext context = server.createContext("/", router);
        context.getFilters().add(new Cors());
        server.setExecutor(null);
        server.start();
        router.setStatus(Router.MOUNTED_STATUS);
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

    public FileOutputStream createOrGetOutputFile(String fileName) throws IOException {
        System.out.println("LLEGUE");
        String realFileName = this.reportDate + "_" + this.recorder.getWebBrowsingActivityConfiguration().getId() + fileName.replace("/", "")
                + OUTPUT_FILE_EXTENSION;
        System.out.println("LLEGUE1");
        /* Creamos un subdirectorio de nombre report date*/
        String parentPath = this.recorder.getStageFolder().getAbsolutePath();
        System.out.println("LLEGUE2");
        this.reportFolder = new File(parentPath + System.getProperty("file.separator") + reportDate +
                "_" + MAP_FILE_NAME);
        System.out.println("LLEGUE3");
        this.reportFolder.mkdir();
        /*if(!folderCreated){
            LOGGER.log(Level.SEVERE, "An error occurred while trying to create the: " + reportDate + " folder");
            return null;
        }
        */
        System.out.println(realFileName);
        System.out.println(parentPath);
        File outputFile = new File(this.reportFolder, realFileName);
        if(this.outputFilesMap.containsKey(fileName)){
            return (FileOutputStream) this.outputFilesMap.get(fileName).get(FILE_OUTPUT_STREAM_KEY);
        }
        System.out.println("WEBEO");
        outputFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        Map<String, Object> outputFileMap = new HashMap<>();
        outputFileMap.put(OUTPUT_FILE_KEY, outputFile);
        outputFileMap.put(FILE_OUTPUT_STREAM_KEY, fileOutputStream);
        this.outputFilesMap.put(fileName, outputFileMap);
        System.out.println("CREE EL OUTPUT FILE");
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
        if(this.reportFolder != null && this.outputFilesMap != null){
            this.reportFolder.delete();
            this.outputFilesMap.clear();
        }
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
        File mapFile = new File(this.recorder.getStageFolder(), mapFileName);
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

    public  void stopTestServer(){
        this.server.stop(0);
        this.server = null;
        this.router = null;
        instance = null;
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

    public void setRecorder(WebBrowsingActivityRecorder recorder){
        this.recorder = recorder;
    }
}
