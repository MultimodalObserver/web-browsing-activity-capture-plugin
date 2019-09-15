package mo.capture.webActivity.plugin.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CaptureConfiguration {

    private String name;
    private String serverIp;
    private String serverPort;
    private String outputFormat;

    public CaptureConfiguration(){

    }

    public CaptureConfiguration(String name, String serverIp, String serverPort, String outputFormat) {
        this.name = name;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.outputFormat = outputFormat;
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

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
