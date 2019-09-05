package mo.capture.webActivity.plugin.model;

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


    public String getServerIp() {
        return serverIp;
    }


    public String getServerPort() {
        return serverPort;
    }

}
