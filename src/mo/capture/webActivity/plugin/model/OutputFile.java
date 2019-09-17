package mo.capture.webActivity.plugin.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class OutputFile {

    private String format;
    private FileOutputStream outputStream;
    private File file;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public FileOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
