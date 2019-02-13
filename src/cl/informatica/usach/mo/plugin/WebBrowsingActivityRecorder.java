package cl.informatica.usach.mo.plugin;

import cl.informatica.usach.mo.plugin.views.ConnectionStatusDialog;
import cl.informatica.usach.mo.server.controllers.ServerController;
import cl.informatica.usach.mo.server.router.Router;
import cl.informatica.usach.mo.utilities.DateHelper;
import mo.communication.streaming.capture.CaptureConfig;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebBrowsingActivityRecorder {

    File stageFolder;
    ProjectOrganization projectOrganization;
    Participant participant;
    WebBrowsingActivityConfiguration webBrowsingActivityConfiguration;
    private List<PluginCaptureListener> dataListeners;
    public static final Logger LOGGER = Logger.getLogger(WebBrowsingActivityRecorder.class.getName());
    private ServerController serverController;


    public WebBrowsingActivityRecorder(File stageFolder, ProjectOrganization projectOrganization, Participant participant,
                                       WebBrowsingActivityConfiguration webBrowsingActivityConfiguration) {
        this.stageFolder = stageFolder;
        this.projectOrganization = projectOrganization;
        this.participant = participant;
        this.webBrowsingActivityConfiguration = webBrowsingActivityConfiguration;
        this.dataListeners = new ArrayList<>();
        //this.createOutputFile(stageFolder);
        this.serverController = new ServerController(stageFolder, webBrowsingActivityConfiguration);
    }

    /*
    private void createOutputFiles(File parent) {
        String reportDate = DateHelper.now();
        this.outputFile = new File(parent, reportDate + "_" + this.webBrowsingActivityConfiguration.getId() + OUTPUT_FILE_EXTENSION);
        try {
            this.outputFile.createNewFile();
            this.fileOutputStream = new FileOutputStream(outputFile);
            this.fileDescription = new FileDescription(outputFile, WebBrowsingActivityRecorder.class.getName());
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }
    */

    /*
    private void deleteOutputFiles(){
        for(File file : this.outputFiles){
            if(!file.isFile()){
                break;
            }
            file.delete();
        }
        for(FileDescription fileDescription : this.outputFileDescriptions){
            if(!fileDescription.getDescriptionFile().isFile()){
                break;
            }
            fileDescription.deleteFileDescription();
        }
    }
    */

    public void start(){
        String serverHost = this.webBrowsingActivityConfiguration.getTemporalConfig().getServerIp();
        String serverPort = this.webBrowsingActivityConfiguration.getTemporalConfig().getServerPort();
        int connectionStatus = this.serverController.startServer(serverHost, serverPort);
        /* Ver posibilidad de actualizar la configuracion desde el status dialog */
        new ConnectionStatusDialog(connectionStatus, serverHost, serverPort).showDialog();
        /* Validamos que se haya iniciado correctamente el servidor */
        if(connectionStatus != ServerController.CONNECTION_ESTABLISHED || this.serverController.getRouter() == null ||
        this.serverController.getInetSocketAddress() == null || this.serverController.getServer() == null){
            return;
        }
        /* Aqui ya hemos validado que el servidor este arriba, por tanto, ya esta el router disponible en estado de MOUNTED */
    }

    public void stop(){
        this.serverController.stopServer(0, false);
    }

    public void pause(){
        this.serverController.getRouter().setStatus(Router.PAUSED_STATUS);
    }

    public void resume(){
        this.serverController.getRouter().setStatus(Router.RESUMED_STATUS);
    }

    public void cancel(){
        this.serverController.stopServer(0, true);
    }

    public void subscribeListener(PluginCaptureListener pluginCaptureListener){
        if(this.dataListeners.contains(pluginCaptureListener)){
            return;
        }
        this.dataListeners.add(pluginCaptureListener);
        CaptureConfig initialRemoteCaptureConfiguration = new CaptureConfig(WebBrowsingActivityRecorder.class.getName(),
                this.webBrowsingActivityConfiguration.getId(), null);
        pluginCaptureListener.setInitConfiguration(this, initialRemoteCaptureConfiguration);
    }

    public void unsubscribeListener(PluginCaptureListener pluginCaptureListener){
        if(this.dataListeners.isEmpty() || !this.dataListeners.contains(pluginCaptureListener)){
            return;
        }
        this.dataListeners.remove(pluginCaptureListener);
    }
}
