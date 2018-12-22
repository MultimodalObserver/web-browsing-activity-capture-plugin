package cl.informatica.usach.mo.router;

public class RouteHandlerInfo {

    private String handlerClassName;
    private String handlerClassMethodName;

    public RouteHandlerInfo(String handlerClassName, String handlerClassMethodName) {
        this.handlerClassName = handlerClassName;
        this.handlerClassMethodName = handlerClassMethodName;
    }

    public String getHandlerClassName() {
        return handlerClassName;
    }

    public void setHandlerClassName(String handlerClassName) {
        this.handlerClassName = handlerClassName;
    }

    public String getHandlerClassMethodName() {
        return handlerClassMethodName;
    }

    public void setHandlerClassMethodName(String handlerClassMethodName) {
        this.handlerClassMethodName = handlerClassMethodName;
    }
}
