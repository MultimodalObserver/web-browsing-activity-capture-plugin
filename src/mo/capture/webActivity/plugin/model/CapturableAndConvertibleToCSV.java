package mo.capture.webActivity.plugin.model;

public interface CapturableAndConvertibleToCSV {

    String toCSV(String separator);
    void setCaptureTimestamp(Long milliseconds);
    Long getCaptureTimestamp();
}
