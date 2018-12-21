package cl.informatica.usach.mo;

import cl.informatica.usach.mo.handlers.BaseHandler;
import cl.informatica.usach.mo.interfaces.RouteHandle;
import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Router {

    private static Router instance;
    private static final String BASE_PACKAGE = "cl.informatica.usach.mo.handlers.";
    private static final String ROUTE_HANDLER_INTERFACE_NAME = "RouteHandle";
    private Map<String, String> routes;

    private Router(){
        this.routes = new HashMap<>();
        this.routes.put("/keystrokes", BASE_PACKAGE + "KeystrokesHandler");
        this.routes.put("/mouseUps", BASE_PACKAGE + "MouseUpsHandler");
        this.routes.put("/mouseMoves", BASE_PACKAGE + "MouseMovesHandler");
        this.routes.put("/mouseClicks", BASE_PACKAGE + "MouseClicksHandler");
    }

    public static Router getInstance(){
        if(instance == null){
            instance =  new  Router();
        }
        return instance;
    }

    public void addRoute(String path, String className){
        if(path.isEmpty() || className.isEmpty()){
            return;
        }
        else if(this.routes.containsKey(path)){
            return;
        }
        this.routes.put(path, BASE_PACKAGE + className);
    }

    public void deleteRoute(String path){
        if(path.isEmpty()){
            return;
        }
        else if(!this.routes.containsKey(path)){
            return;
        }
        this.routes.remove(path);
    }

    public void match(String path, HttpExchange exchange, String captureInitTimestamp){
        if(path.isEmpty()){
            return;
        }
        else if(!this.routes.containsKey(path)){
            new BaseHandler().sendResponse("Invalid route", 404, exchange);
            return;
        }
        Class handlerClass = null;
        try {
            handlerClass = Class.forName(this.routes.get(path));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if(!this.implementsRouteHandleInterface(handlerClass)){
            return;
        }
        Object instance = null;
        try {
            instance = handlerClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        try {
            Class[] parameterTypes = {HttpExchange.class, String.class};
            Method method = handlerClass.getMethod("handle", parameterTypes);
            method.invoke(instance,exchange, captureInitTimestamp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private boolean implementsRouteHandleInterface(Class handlerClass){
        Class[] interfaces = handlerClass.getInterfaces();
        for (Class anInterface : interfaces) {
            if (anInterface.getSimpleName().equals(ROUTE_HANDLER_INTERFACE_NAME)) {
                return true;
            };
        }
        return false;
    }

}
