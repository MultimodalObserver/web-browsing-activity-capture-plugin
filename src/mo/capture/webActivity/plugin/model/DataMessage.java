package mo.capture.webActivity.plugin.model;

public class DataMessage {

    private String data;
    private Long captureMilliseconds;
    private String dataType;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getCaptureMilliseconds() {
        return captureMilliseconds;
    }

    public void setCaptureMilliseconds(Long captureMilliseconds) {
        this.captureMilliseconds = captureMilliseconds;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
