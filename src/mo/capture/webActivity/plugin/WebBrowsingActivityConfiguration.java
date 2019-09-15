package mo.capture.webActivity.plugin;

import mo.capture.webActivity.plugin.model.CaptureConfiguration;
import mo.capture.RecordableConfiguration;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.communication.streaming.capture.PluginCaptureSender;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebBrowsingActivityConfiguration implements RecordableConfiguration, PluginCaptureSender {

    private CaptureConfiguration temporalConfig;
    private WebBrowsingActivityRecorder webBrowsingActivityRecorder;
    private static final Logger LOGGER = Logger.getLogger(WebBrowsingActivityConfiguration.class.getName());
    public static final String PLUGIN_MESSAGE_KEY = "webActivity";

    public WebBrowsingActivityConfiguration(){

    }


    public WebBrowsingActivityConfiguration(CaptureConfiguration temporalConfig){
        this.temporalConfig = temporalConfig;
        this.webBrowsingActivityRecorder = null;
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
        return WebBrowsingActivityRecorder.class.getName();
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
            String childFileName = "web-browsing-activity_"+this.temporalConfig.getName()+".xml";
            File f = new File(parent, childFileName);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            JAXBContext jaxbContext = JAXBContext.newInstance(CaptureConfiguration.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(this.temporalConfig, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return f;
        } catch (IOException | JAXBException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CaptureConfiguration.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            CaptureConfiguration auxConfig = (CaptureConfiguration) unmarshaller.unmarshal(file);
            return new WebBrowsingActivityConfiguration(auxConfig);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }
}
