package mo.capture.webActivity.plugin;

import mo.capture.webActivity.plugin.view.ConnectionSuccessDialog;
import mo.capture.webActivity.server.controller.ServerController;
import mo.capture.webActivity.server.router.Router;
import mo.communication.streaming.capture.CaptureConfig;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.core.I18n;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WebBrowsingActivityRecorder {

    private File stageFolder;
    private ProjectOrganization projectOrganization;
    private Participant participant;
    private WebBrowsingActivityConfiguration webBrowsingActivityConfiguration;
    private List<PluginCaptureListener> dataListeners;
    private I18n i18n;
    public static final Logger LOGGER = Logger.getLogger(WebBrowsingActivityRecorder.class.getName());


    public WebBrowsingActivityRecorder(File stageFolder, ProjectOrganization projectOrganization, Participant participant,
                                       WebBrowsingActivityConfiguration webBrowsingActivityConfiguration) {
        this.stageFolder = stageFolder;
        this.projectOrganization = projectOrganization;
        this.participant = participant;
        this.webBrowsingActivityConfiguration = webBrowsingActivityConfiguration;
        this.dataListeners = new ArrayList<>();
        this.i18n = new I18n(WebBrowsingActivityRecorder.class);
    }

    public void start(){
        String serverHost = this.getWebBrowsingActivityConfiguration().getTemporalConfig().getServerIp();
        String serverPort = this.getWebBrowsingActivityConfiguration().getTemporalConfig().getServerPort();
        int serverStatus = ServerController.getInstance().startServer(serverHost, serverPort);
        ConnectionSuccessDialog connectionSuccessDialog = new ConnectionSuccessDialog();
        String message = null;
        if(serverStatus == ServerController.CONNECTION_ESTABLISHED){
            message = this.i18n.s("connectionSuccessMessage") + "http://"+serverHost+":"+serverPort;
            ServerController.getInstance().setRecorder(this);
        }
        else if(serverStatus == ServerController.UNKNOWN_HOST){
            message = this.i18n.s("unknownHostErrorMessage");
        }
        else if(serverStatus == ServerController.INVALID_PORT){
            message = this.i18n.s("invalidPortErrorMessage");
        }
        else if(serverStatus == ServerController.PORT_NOT_AVAILABLE){
            message = this.i18n.s("portNotAvailableErrorMessage");
        }
        connectionSuccessDialog.setSuccessStatus(message);
        connectionSuccessDialog.showDialog();
    }

    public void stop(){
        ServerController.getInstance().stopServer(0, false);
    }

    public void pause(){
        ServerController.getInstance().getRouter().setStatus(Router.PAUSED_STATUS);
    }

    public void resume(){
        ServerController.getInstance().getRouter().setStatus(Router.RESUMED_STATUS);
    }

    public void cancel(){
        ServerController.getInstance().stopServer(0, true);
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

    public File getStageFolder() {
        return this.stageFolder;
    }

    public WebBrowsingActivityConfiguration getWebBrowsingActivityConfiguration() {
        return this.webBrowsingActivityConfiguration;
    }

}
