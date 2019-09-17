package mo.capture.webActivity.plugin.model;

public enum Separator {
    CSV_COLUMN_SEPARATOR(","),
    CSV_ROW_SEPARATOR(System.getProperty("line.separator")),
    JSON_SEPARATOR(",");

    private final String value;

    Separator(String value){
        this.value  = value;
    }

    public String getValue(){
        return this.value;
    }
}
