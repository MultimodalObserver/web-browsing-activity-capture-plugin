package mo.capture.webActivity.server.controller;

import com.google.gson.Gson;
import mo.capture.webActivity.plugin.model.CSVHeader;
import mo.capture.webActivity.plugin.model.Format;
import mo.capture.webActivity.plugin.model.OutputFile;
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
    private Map<String, OutputFile[]> dataTypesOutputsMap;
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
        this.dataTypesOutputsMap = new HashMap<>();
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
            LOGGER.log(Level.SEVERE, null, e);
            return UNKNOWN_HOST;
        }
        try {
            this.inetSocketAddress = new InetSocketAddress(address, Integer.parseInt(serverPort));
            server = HttpServer.create(this.inetSocketAddress, 0);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return PORT_NOT_AVAILABLE;
        }
        catch(NumberFormatException e){
            LOGGER.log(Level.SEVERE, null, e);
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
    public OutputFile[] createOrGetOutputFile(String handledDataType, boolean exportToCsv) throws IOException {
        if(this.dataTypesOutputsMap.containsKey(handledDataType)){
            return this.dataTypesOutputsMap.get(handledDataType);
        }

        String baseFileName = this.reportDate + "_" + this.recorder.getWebBrowsingActivityConfiguration().getId() + "_" +
                handledDataType.replace("/", "");

        /* Creamos un subdirectorio de nombre report date*/
        String parentPath = this.recorder.getStageFolder().getAbsolutePath();
        /* Podemos tener varias de estas report folders a lo largo de la ejecucion del plugin, solo creamos una nueva
        cuando el mapa de output files esta vacio, lo que significa que recien se va a crear el primer archivo de un proceso
        de captura, y por tanto se crea el report folder antes

        Si no, se trae el ultimo report folder de la lista, que representa el report folder de la captura actual
         */
        File activeReportFolder;
        if(this.dataTypesOutputsMap.isEmpty()){
            activeReportFolder = new File(parentPath + System.getProperty("file.separator") + reportDate +
                    "_" + MAP_FILE_NAME);
            activeReportFolder.mkdir();
            this.reportFolders.add(activeReportFolder);
        }
        else{
            activeReportFolder = this.reportFolders.get(this.reportFolders.size()- 1);
        }

        /* Creamos los archivos y los asociamos al mapa
        *
        * Si o si se debe crear el json
        *
        * EL csv es opcional en caso de que se haya seleccionado la opcion de exportar a CSV
        *
        * */
        OutputFile[] outputFiles = new OutputFile[2];
        File jsonFile = new File(activeReportFolder, baseFileName + "." + Format.JSON.getValue());
        FileOutputStream jsonOutputStream = new FileOutputStream(jsonFile);
        jsonOutputStream.write(INIT_JSON_ARRAY.getBytes());
        OutputFile jsonOutputFile = new OutputFile();
        jsonOutputFile.setFormat(Format.JSON.getValue());
        jsonOutputFile.setOutputStream(jsonOutputStream);
        jsonOutputFile.setFile(jsonFile);
        outputFiles[0] = jsonOutputFile;
        if(exportToCsv){
            File csvFile = new File(activeReportFolder, baseFileName + "." + Format.CSV.getValue());
            FileOutputStream csvOutputStream = new FileOutputStream(csvFile);
            String headers = this.getCsvHeaders(handledDataType, Separator.CSV_COLUMN_SEPARATOR.getValue()) + Separator.CSV_ROW_SEPARATOR.getValue();
            csvOutputStream.write(headers.getBytes());
            OutputFile csvOutputFile = new OutputFile();
            csvOutputFile.setFormat(Format.CSV.getValue());
            csvOutputFile.setOutputStream(csvOutputStream);
            csvOutputFile.setFile(csvFile);
            outputFiles[1] = csvOutputFile;
        }
        this.dataTypesOutputsMap.put(handledDataType, outputFiles);
        return outputFiles;
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
       for (Object key : this.dataTypesOutputsMap.keySet()) {
            OutputFile[] outputFiles = this.dataTypesOutputsMap.get(key);
            for(OutputFile outputFile : outputFiles){
                if(outputFile == null){
                    continue;
                }
                try {
                    outputFile.getOutputStream().flush();
                    outputFile.getOutputStream().close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    continue;
                }
                outputFile.getFile().delete();
            }
        }
        this.dataTypesOutputsMap.clear();

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
        for (Object key : this.dataTypesOutputsMap.keySet()) {
            OutputFile[] outputFiles = this.dataTypesOutputsMap.get(key);
            for(OutputFile outputFile : outputFiles){
                if(outputFile == null){
                    continue;
                }
                boolean isCsvFile = outputFile.getFormat().equals(Format.CSV.getValue());
                try {
                    outputFile.getOutputStream().flush();
                    long channelSize = outputFile.getOutputStream().getChannel().position();
                    String lastChar = isCsvFile ? Separator.CSV_ROW_SEPARATOR.getValue() : Separator.JSON_SEPARATOR.getValue();
                    long sizeWithoutLastChar = channelSize - lastChar.getBytes().length;
                    outputFile.getOutputStream().getChannel().truncate(sizeWithoutLastChar);
                    if(!isCsvFile){
                        outputFile.getOutputStream().write(END_JSON_ARRAY.getBytes());
                    }
                    outputFile.getOutputStream().flush();
                    outputFile.getOutputStream().close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
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
        for (Object key : this.dataTypesOutputsMap.keySet()){
            String auxKey = (String) key;
            /* SOLO JSON*/
            OutputFile[] outputFiles = this.dataTypesOutputsMap.get(key);
            for(OutputFile outputFile : outputFiles){
                if(outputFile != null && outputFile.getFormat().equals(Format.JSON.getValue())){
                    map.put( auxKey.replace("/", ""), outputFile.getFile().getAbsolutePath());
                }
            }
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
        this.dataTypesOutputsMap.clear();
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
