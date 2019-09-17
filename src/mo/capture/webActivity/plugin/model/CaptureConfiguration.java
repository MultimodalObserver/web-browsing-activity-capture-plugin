package mo.capture.webActivity.plugin.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CaptureConfiguration {

    private String name;
    private String serverIp;
    private String serverPort;
    private Boolean exportToCsv;

    public CaptureConfiguration(){

    }

    public CaptureConfiguration(String name, String serverIp, String serverPort, Boolean exportToCsv) {
        this.name = name;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.exportToCsv = exportToCsv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public Boolean getExportToCsv() {
        return exportToCsv;
    }

    public void setExportToCsv(Boolean exportToCsv) {
        this.exportToCsv = exportToCsv;
    }
}
