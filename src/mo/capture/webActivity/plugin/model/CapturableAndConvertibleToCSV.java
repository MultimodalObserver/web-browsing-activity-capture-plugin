package mo.capture.webActivity.plugin.model;

public interface CapturableAndConvertibleToCSV {

    String toCSV(String separator);
    void setCaptureMilliseconds(Long milliseconds);
    Long getCaptureMilliseconds();
}
