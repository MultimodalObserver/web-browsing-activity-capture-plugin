package cl.informatica.usach.mo.plugin;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import cl.informatica.usach.mo.plugin.views.ConfigurationDialog;
import mo.capture.CaptureProvider;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.core.I18n;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension(
        xtends = {
                @Extends(extensionPointId = "mo.capture.CaptureProvider")
        }
)

public class WebBrowsingActivityCapturePlugin implements CaptureProvider {

    private static final Logger LOGGER = Logger.getLogger(WebBrowsingActivityCapturePlugin.class.getName());
    private I18n i18n;
    List<Configuration> configurations;
    List<PluginCaptureListener> dataListeners;

    public WebBrowsingActivityCapturePlugin(){
        this.configurations = new ArrayList<>();
        this.i18n = new I18n(WebBrowsingActivityCapturePlugin.class);
        this.dataListeners = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.i18n.s("webBrowsingActivityCapturePluginDisplayedName");
    }

    @Override
    public Configuration initNewConfiguration(ProjectOrganization projectOrganization) {
        ConfigurationDialog configDialog = new ConfigurationDialog();
        configDialog.showDialog();
        if(!configDialog.isAccepted()){
            return null;
        }
        WebBrowsingActivityConfiguration configuration = new WebBrowsingActivityConfiguration(configDialog.getTemporalConfig());
        this.configurations.add(configuration);
        return configuration;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return this.configurations;
    }

    @Override
    public StagePlugin fromFile(File file) {
        if(!file.isFile()){
            return null;
        }
        XElement root = null;
        try {
            root = XIO.readUTF(new FileInputStream(file));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
        if(root == null){
            return null;
        }
        WebBrowsingActivityCapturePlugin webBrowsingActivityCapturePlugin = new WebBrowsingActivityCapturePlugin();
        XElement[] pathsX = root.getElements("path");
        for (XElement pathX : pathsX) {
            String path = pathX.getString();
            File archive = new File(file.getParentFile(), path);
            Configuration config = new WebBrowsingActivityConfiguration(archive);
            webBrowsingActivityCapturePlugin.configurations.add(config);
        }
        return webBrowsingActivityCapturePlugin;
    }

    @Override
    public File toFile(File parent) {
        File file = new File(parent, "web-browsing-activity-capture.xml");
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        XElement root = new XElement("capturers");
        for (Configuration config : configurations) {
            File p = new File(parent, "web-browsing-activity-capture");
            p.mkdirs();
            File f = config.toFile(p);

            XElement path = new XElement("path");
            Path parentPath = parent.toPath();
            Path configPath = f.toPath();
            path.setString(parentPath.relativize(configPath).toString());
            root.addElement(path);
        }
        try {
            XIO.writeUTF(root, new FileOutputStream(file));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return file;
    }
}
