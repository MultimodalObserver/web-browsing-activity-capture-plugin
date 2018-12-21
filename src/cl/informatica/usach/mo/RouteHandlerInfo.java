package cl.informatica.usach.mo;

public class RouteHandlerInfo {

    private String handlerClassName;
    private String handlerClassMethodName;

    public RouteHandlerInfo(String handlerClassName, String handlerClassMethodName) {

        if(handlerClassName.isEmpty()){
            System.out.println("RouteHandlerInfo handler classname must be present");
            System.exit(1);
        }
        else if(handlerClassMethodName.isEmpty()){
            System.out.println("RouteHandlerInfo handler method name must be present");
            System.exit(1);
        }
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
