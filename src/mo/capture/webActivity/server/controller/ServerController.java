package mo.capture.webActivity.server.controller;

import com.google.gson.Gson;
import mo.capture.webActivity.plugin.model.CSVHeader;
import mo.capture.webActivity.plugin.model.Format;
import mo.capture.webActivity.plugin.model.Separator;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webActivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webActivity.server.middleware.Cors;
import mo.capture.webActivity.server.router.Router;
import mo.capture.webActivity.server.view.ServerConfigurationView;
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

    private static final String INIT_JSON_ARRAY = "[";
    private static final String END_JSON_ARRAY = "]";

    public static final int UNKNOWN_HOST = 1;
    public static final int PORT_NOT_AVAILABLE = 2;
    public static final int INVALID_PORT = 3;
    public static final int CONNECTION_ESTABLISHED = 4;
    private Router router;
    private HttpServer server;
    private InetSocketAddress inetSocketAddress;

    /*Para el manejo de almacenamiento de los datos capturados*/
    private List<File> reportFolders;
    private List<FileDescription> reportFoldersDescriptions;
    private Map<String, Map<String, Object>> outputFilesMap;
    private String reportDate;
    private static final String OUTPUT_FILE_KEY = "outputFile";
    private static final String FILE_OUTPUT_STREAM_KEY = "fileOutputStream";
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
        this.reportFolders = new ArrayList<>();
        this.reportFoldersDescriptions = new ArrayList<>();
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

    /* HANDLED DATA TYPE ES UNA VARIABLE QUE IDENTIFICA UNICAMENTE EL TIPO DE DATO CAPTURADO

    POR EJ: TECLAS --> KEYSTROKES
            CLICKS DEL MOUSE --> MOUSECLICKS, ETC..

      DICHA VARIABLE ES DEFINIDA EN CADA UNO DE LOS HANDLERS Y ES INDEPENDIENTE DE LA RUTA ASOCIADA A EL.
     */
    public FileOutputStream createOrGetOutputFile(String handledDataType, String outputFormat) throws IOException {
        if(this.outputFilesMap.containsKey(handledDataType)){
            return (FileOutputStream) this.outputFilesMap.get(handledDataType).get(FILE_OUTPUT_STREAM_KEY);
        }
        String realFileName = this.reportDate + "_" + this.recorder.getWebBrowsingActivityConfiguration().getId() + "_" +
                handledDataType.replace("/", "") + "." + outputFormat;
        /* Creamos un subdirectorio de nombre report date*/
        String parentPath = this.recorder.getStageFolder().getAbsolutePath();
        /* Podemos tener varias de estas report folders a lo largo de la ejecucion del plugin, solo creamos una nueva
        cuando el mapa de output files esta vacio, lo que significa que recien se va a crear el primer archivo de un proceso
        de captura, y por tanto se crea el report folder antes

        Si no, se trae el ultimo report folder de la lista, que representa el report folder de la captura actual
         */
        File activeReportFolder;
        if(this.outputFilesMap.isEmpty()){
            activeReportFolder = new File(parentPath + System.getProperty("file.separator") + reportDate +
                    "_" + MAP_FILE_NAME);
            activeReportFolder.mkdir();
            this.reportFolders.add(activeReportFolder);
        }
        else{
            activeReportFolder = this.reportFolders.get(this.reportFolders.size()- 1);
        }
        File outputFile = new File(activeReportFolder, realFileName);
        outputFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        String outputFileExtension = this.recorder.getWebBrowsingActivityConfiguration().getTemporalConfig().getOutputFormat();
        if(outputFileExtension.equals(Format.CSV.getValue())){
            String firstLine = this.getCsvHeaders(handledDataType, Separator.CSV_COLUMN_SEPARATOR.getValue()) + Separator.CSV_ROW_SEPARATOR.getValue();
            fileOutputStream.write(firstLine.getBytes());
        }
        else if(outputFileExtension.equals(Format.JSON.getValue())){
            fileOutputStream.write(INIT_JSON_ARRAY.getBytes());
        }
        Map<String, Object> outputFileMap = new HashMap<>();
        outputFileMap.put(OUTPUT_FILE_KEY, outputFile);
        outputFileMap.put(FILE_OUTPUT_STREAM_KEY, fileOutputStream);
        this.outputFilesMap.put(handledDataType, outputFileMap);
        return fileOutputStream;
    }

    private String getCsvHeaders(String handledDataType, String separator) {
        String res = "";
        String[] headers = CSVHeader.HEADERS.get(handledDataType);
        for(int i = 0; i < headers.length; i++){
            boolean lastHeader = i == headers.length - 1;
            res = lastHeader ? res + headers[i] : res + headers[i] + separator;
        }
        return res;
    }

    private void deleteOutputFiles() {
        /* EL OUTPUFILESMAP SIEMPRE HACE REFERENCIA A LOS ARCHIVOS QUE VAN DENTRO DE LA CARPETA DE LA CAPTURA ACTUAL
        EJECUTANDOSE, LA QUE NO SE HA DETENIDO NI CANCELADO!!
         */
        for (Object key : this.outputFilesMap.keySet()) {
            Map<String, Object> subMap = this.outputFilesMap.get(key);
            File outputFile = (File) subMap.get(OUTPUT_FILE_KEY);
            if (!outputFile.isFile()) {
                continue;
            }
            FileOutputStream outputStream = (FileOutputStream) subMap.get(FILE_OUTPUT_STREAM_KEY);
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
                continue;
            }
            outputFile.delete();
        }
        this.outputFilesMap.clear();

        /* Esta parte elimina todos los archivos asociados a todos los report folders empleados durante la captura

        INCLUYE LOS QUE YA SE HABIAN GUARDADO!!!
         */
        if (this.reportFolders != null && !this.reportFolders.isEmpty() && this.reportFoldersDescriptions != null &&
            !this.reportFoldersDescriptions.isEmpty()) {
            for (File folder : this.reportFolders) {
                File[] files = folder.listFiles();
                for(File file : files){
                    file.delete();
                }
                folder.delete();
            }
            for(FileDescription description: this.reportFoldersDescriptions){
                description.getFile().delete();
                description.getDescriptionFile().delete();
            }
        }
    }

    private void closeFileOutputStreams(){
        for (Object key : this.outputFilesMap.keySet()) {
            Map<String, Object> subMap = this.outputFilesMap.get(key);
            FileOutputStream outputStream = (FileOutputStream) subMap.get(FILE_OUTPUT_STREAM_KEY);
            File outputFile = (File) subMap.get(OUTPUT_FILE_KEY);
            boolean csvFile = outputFile.getAbsolutePath().endsWith("." + Format.CSV.getValue());
            try {
                outputStream.flush();
                long channelSize = outputStream.getChannel().position();
                String lastChar = csvFile ? Separator.CSV_ROW_SEPARATOR.getValue() : Separator.JSON_SEPARATOR.getValue();
                long sizeWithoutLastChar = channelSize - lastChar.getBytes().length;
                outputStream.getChannel().truncate(sizeWithoutLastChar);
                if(!csvFile){
                    outputStream.write(END_JSON_ARRAY.getBytes());
                }
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
    }


    private void writeMapFile(){
        String mapFileName = this.reportDate + "_" + MAP_FILE_NAME + "." + Format.JSON.getValue();
        File mapFile = new File(this.recorder.getStageFolder(), mapFileName);
        FileOutputStream mapFileOutputStream = null;
        try {
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
            mapFileOutputStream.flush();
            mapFileOutputStream.close();
            this.reportFoldersDescriptions.add(new FileDescription(mapFile, this.recorder.getClass().getName()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    public void stopServer(int delay, boolean isCancelAction){
        /* AQUI hay que cerrar todos los archivos que se hayan abierto*/
        if(isCancelAction){
            this.deleteOutputFiles();
        }
        else{
            this.closeFileOutputStreams();
            /* Una vez cerramos todos los streamos de los archivos, creamos el verdadero archivo que será utilizado por MO
            el que contiene un mapa que nos indica donde estan almacenados los archivos correspondientes a cada captura.

            Luego, cuando otro plugin requiera usar la info de estos archivo, solo debera acceder a este archivo final
            para encontrar y abrir los archivos que contienen los datos correspondientes.

            Esto debido a que MO solo permite abrir un archivo al mismo tiempo (en el caso de visualización)
            */
            /* Solo escribimos el map file si nuestro report date no es nulo, eso implica que no se inicio una nueva captura
             * luego de detener una anterior*/
            if(this.reportDate == null){
                return;
            }
            this.writeMapFile();
        }
        this.server.stop(delay);
        this.server = null;
        this.router = null;
        instance = null;
    }

    /*  Metodo que permite terminar la captura actual,
        cerrando streams y archivos para permitir realizar otra captura
        sin detener realmente el servidor.
        Esto se logra volviendo el enrutador a su estado "montado".
    */
    public void stopCapture(){
        this.closeFileOutputStreams();
        this.writeMapFile();
        this.outputFilesMap.clear();
        this.reportDate = null;
        this.router.setStatus(Router.MOUNTED_STATUS);
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

    public void setRecorder(WebBrowsingActivityRecorder recorder){
        this.recorder = recorder;
    }
}
