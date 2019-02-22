package mo.capture.webactivity.plugin.models;

public class CaptureConfiguration {

    private String name;
    private String serverIp;
    private String serverPort;

    public CaptureConfiguration(String name, String serverIp, String serverPort) {
        this.name = name;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
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
}
