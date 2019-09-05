package mo.capture.webActivity.plugin;

import mo.capture.webActivity.plugin.model.CaptureConfiguration;
import mo.capture.RecordableConfiguration;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.communication.streaming.capture.PluginCaptureSender;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebBrowsingActivityConfiguration implements RecordableConfiguration, PluginCaptureSender {

    private CaptureConfiguration temporalConfig;
    private WebBrowsingActivityRecorder webBrowsingActivityRecorder;
    private static final Logger LOGGER = Logger.getLogger(WebBrowsingActivityConfiguration.class.getName());
    public static final String PLUGIN_MESSAGE_KEY = "webActivity";


    WebBrowsingActivityConfiguration(CaptureConfiguration temporalConfig){
        this.temporalConfig = temporalConfig;
        this.webBrowsingActivityRecorder = null;
    }

    /* Constructor que es utilizado para crear la configuración desde los archivos relacionados al plugin (que
   almacenan su info), luego de que
   MO ha sido cerrado.
   Esto es para que las configuraciones no se pierdan
    */
    WebBrowsingActivityConfiguration(File file){
        String fileName = file.getName();
        String configData = fileName.substring(0, fileName.lastIndexOf("."));
        String[] configElements = configData.split("_");
        /* El elemento 0 es la palabra web-browsing-activity*/
        String configurationName = configElements[1];
        String serverIp = configElements[2];
        String serverPort = configElements[3];
        this.temporalConfig =  new CaptureConfiguration(configurationName, serverIp, serverPort);
    }

    @Override
    public void setupRecording(File file, ProjectOrganization projectOrganization, Participant participant) {
        this.webBrowsingActivityRecorder = new WebBrowsingActivityRecorder(file, projectOrganization, participant,this);
    }

    @Override
    public void startRecording() {
        this.webBrowsingActivityRecorder.start();
    }

    @Override
    public void cancelRecording() {
        this.webBrowsingActivityRecorder.cancel();
    }

    @Override
    public void pauseRecording() {
        this.webBrowsingActivityRecorder.pause();
    }

    @Override
    public void resumeRecording() {
        this.webBrowsingActivityRecorder.resume();
    }

    @Override
    public void stopRecording() {
        this.webBrowsingActivityRecorder.stop();
    }

    @Override
    public void subscribeListener(PluginCaptureListener pluginCaptureListener) {
        this.webBrowsingActivityRecorder.subscribeListener(pluginCaptureListener);
    }

    @Override
    public void unsubscribeListener(PluginCaptureListener pluginCaptureListener) {
        this.webBrowsingActivityRecorder.unsubscribeListener(pluginCaptureListener);
    }

    @Override
    public String getCreator() {
        return WebBrowsingActivityConfiguration.class.getName();
    }

    @Override
    public void send25percent() {

    }

    @Override
    public void send50percent() {

    }

    @Override
    public void send75percent() {

    }

    @Override
    public void send100percent() {

    }

    @Override
    public String getId() {
        return this.temporalConfig.getName();
    }

    @Override
    public File toFile(File parent) {
        try {
            String childFileName = "web-browsing-activity_"+this.temporalConfig.getName()+
                    "_"+this.temporalConfig.getServerIp() + "_" + this.temporalConfig.getServerPort() + ".xml";
            File f = new File(parent, childFileName);
            f.createNewFile();
            return f;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();
        if(!fileName.contains("_") || !fileName.contains(".")){
            return null;
        }
        String configData = fileName.substring(0, fileName.lastIndexOf("."));
        String[] configElements = configData.split("_");
        String configurationName = configElements[0];
        String serverIp = configElements[1];
        String serverPort = configElements[2];
        CaptureConfiguration auxConfig = new CaptureConfiguration(configurationName, serverIp, serverPort);
        return new WebBrowsingActivityConfiguration(auxConfig);
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }
}
