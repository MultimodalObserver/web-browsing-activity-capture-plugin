package mo.capture.webActivity.plugin.model;

public enum Format {
    JSON("json"),
    CSV("csv");

    private final String value;

    Format(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
