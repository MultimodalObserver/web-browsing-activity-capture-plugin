package mo.capture.webActivity.server.router;

import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.webActivity.plugin.WebBrowsingActivityRecorder;
import mo.capture.webActivity.server.controller.ServerController;
import mo.capture.webActivity.server.handler.behavior.CaptureEndpoint;
import mo.capture.webActivity.server.handler.behavior.StartEndpoint;
import mo.capture.webActivity.server.utilities.Response;
import mo.capture.webActivity.util.DateHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

/* Clase que implementa funcionalidades de un router o enrutador. Su principal funcion es hacer un match entre
el recurso buscado en el servidor, mediante la URI consultada, especificamente el path y el manejador, controlador o handler
asociado a ese recurso mediante un mapa de llaves (Strings) y valores( mapa de tipo string , objetos RouteHandlerInfo).

 Se utiliza una clase POJO llamada RouteHandlerInfo, que guarda valores importantes para el correcto manejo de las rutas, como lo
 son:

 - Nombre completo de la clase que manejará la ruta (incluyendo el package).
 - Nombre del método de un objeto de la clase que será ejecutado. Los parametros que reciban estos metodos estan limitados
 por el tipo de peticion http que se realizó:

    - Para POST, los parametros son el http exchange (que contiene toda la info del intercambio cliente/servidor) y un timestamp
    de inicio de la captura, ya que solo con POST recibimos datos capturados por la extension.
    - Para GET u otros, el parametro es solo el exchange, ya que a partir de el se puede obtener toda la informacion necesaria.
    En caso de querer obtener los query params de un GET, por herencia, en los handler está disponible el metodo
    parseQueryString que entrega un mapa de llaves (String) y valores(String) con los parametros del request tipo GET.

El mapa de rutas es inicializado en el constructor de esta clase, por tanto se recomienda agregar las rutas ahí.
De todas formas se disponibilizan los métodos deleteRoute y addRoute para modificar las rutas.

Conceptualmente la estructura del router (el atributo routes en especifico) es un mapa de mapas de la siguiente forma:
(usando la sintaxis de arrays asociativos de PHP)

[ ruta1 => [
        'POST' => objeto handlerInfo1
        'GET' => objeto handlerInfo2
    ],
  ruta2 => [
        'POST' => objeto HandlerInfo3
  ],
  ....
]

LO QUE PASA EN ESTA CLASE Y EN TODO LO QUE TIENE QUE VER CON EL PROCESAMIENTO DLE SERVIDOR, ES EN UN THREAD APARTE DEL
DE LA FUNCION PRINCIPAL DEL PROGRAMA!!.
 */

public class Router implements HttpHandler {

    public static final int MOUNTED_STATUS = 0;
    public static final int RUNNING_STATUS = 1;
    public static final int PAUSED_STATUS = 2;
    public static final int RESUMED_STATUS = 3;
    public static final int STOPPED_STATUS = 4;
    private static final String BASE_PACKAGE = "mo.capture.webActivity.server.";
    private long pauseTime;
    private long resumeTime;
    private Map<String, Map<String, RouteHandlerInfo>> routes;
    private int status;

    public Router(ServerController serverController){
        this.routes = new HashMap<>();
        this.pauseTime=0;
        this.resumeTime=0;

        RouteHandlerInfo keystrokesPostRouteHandlerInfo = new RouteHandlerInfo( BASE_PACKAGE + "handler.KeystrokesHandler", "store");
        RouteHandlerInfo mouseUpsPostRouteHandlerInfo = new RouteHandlerInfo(BASE_PACKAGE + "handler.MouseUpsHandler", "store");
        RouteHandlerInfo mouseMovesPostRouteHandlerInfo = new RouteHandlerInfo( BASE_PACKAGE + "handler.MouseMovesHandler", "store");
        RouteHandlerInfo mouseClicksPostRouteHandlerInfo = new RouteHandlerInfo( BASE_PACKAGE + "handler.MouseClicksHandler", "store");
        RouteHandlerInfo startPostHandlerInfo = new RouteHandlerInfo(BASE_PACKAGE + "handler.StartHandler", "start");

        this.addRoute("/keystrokes","POST", keystrokesPostRouteHandlerInfo);
        this.addRoute("/mouseUps", "POST", mouseUpsPostRouteHandlerInfo);
        this.addRoute("/mouseMoves", "POST", mouseMovesPostRouteHandlerInfo);
        this.addRoute("/mouseClicks", "POST", mouseClicksPostRouteHandlerInfo);
        this.addRoute("/start", "POST", startPostHandlerInfo);
    }

    public void addRoute(String path, String httpMethod, RouteHandlerInfo routeHandlerInfo){
        if(path.isEmpty()){
            System.out.println("To add a routeHandlerInfo, the path cannot be empty");
            return;
        }
        else if(this.routes.containsKey(path) && this.routes.get(path).containsValue(routeHandlerInfo)){
            System.out.println("RouteHandlerInfo " + path + " with method : "+ httpMethod+" added already");
            return;
        }
        Map<String, RouteHandlerInfo> map = new HashMap<>();
        map.put(httpMethod, routeHandlerInfo);
        this.routes.put(path, map);
    }

    public void deleteRoute(String path, String httpMethod){
        if(path.isEmpty()){
            System.out.println("To delete a route, the path cannot be empty");
            return;
        }
        else if(!this.routes.containsKey(path)){
            System.out.println("The given path to delete: " +path +" is not registered as a route");
            return;
        }
        else if(!this.routes.get(path).containsKey(httpMethod)){
            System.out.println("The given path to delete: "+path+ "is registered as a route, but not with "+ httpMethod + "http method");
        }
        this.routes.get(path).remove(httpMethod);
    }

    public void setStatus(int status) {
        if(status == PAUSED_STATUS){
            this.pauseTime = DateHelper.nowMilliseconds();
        } else if(status == RESUMED_STATUS){
            this.resumeTime = DateHelper.nowMilliseconds();
        }
        this.status = status;
    }

    public Map<String, Map<String, RouteHandlerInfo>> getRoutes() {
        return this.routes;
    }

    /* Principal metodo de esta clase, en donde mediante relfexion, se invoca el metodo de la clase
    registrado en las rutas.

    Se valida que exista la ruta que se quiere mapear, y además se valida que la ruta este registrada
    con el método http del request que esta accediendo a esa ruta, ya que una ruta puede ser manejada accedida
    con distintos metodos HTTP.

    Si el handler (clase) a instanciar es una instancia de la interfaz CaptureENdpoint, se le pasa todo lo necesario para poder
    realizar la captura del tipo de dato en cuestión. Además, se crea el archivo de captura referente a ese endpoint, en caso
    de que no se haya creado.

    Sino, si el handler a instanciar es una instancia de la interfaz StartEndpoint, se le pasa todo lo necesario para dar inicio
    real al proceso de captura, ya que esto es determinado por la extensión y no por el plugin de MO.

    */

    @Override
    public void handle(HttpExchange exchange){
        System.out.println(this.status);
        System.out.println("Peticion recibida");
        System.out.println(exchange.getRequestURI());
        if(this.status == PAUSED_STATUS){
            String response = "Server paused";
            Response.sendResponse(response, 404, exchange);
            return;
        }
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String httpMethod = exchange.getRequestMethod();
        if(this.status == MOUNTED_STATUS && (!path.equals("/start") || !httpMethod.equals("POST"))){
            String response = "Server mounted, waiting for the extension capture init";
            Response.sendResponse(response, 404, exchange);
            return;
        }
        else if(!this.routes.containsKey(path)){
            String response = "Route with path: "+path+" not defined in the server";
            Response.sendResponse(response, 404, exchange);
            return;
        }
        else if(!this.routes.get(path).containsKey(httpMethod)){
            String response = "Route with path "+path+" defined in the server, but not with "+ httpMethod + "http method";
            Response.sendResponse(response, 404, exchange);
            return;
        }
        System.out.println("PASE REGLAS DE RUTAS");
        RouteHandlerInfo routeHandlerInfo = this.routes.get(path).get(httpMethod);
        Class handlerClass = null;
        try {
            handlerClass = Class.forName(routeHandlerInfo.getHandlerClassName());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            Response.sendResponse("handler class not found for the " + path + " route", 500, exchange);
            return;
        }
        System.out.println("ENcontre clase");
        String handlerClassMethodName = routeHandlerInfo.getHandlerClassMethodName();
        Object instance = null;
        Constructor classConstructor = null;
        try {
            classConstructor = handlerClass.getConstructor();
            instance= classConstructor.newInstance();
            if(instance instanceof CaptureEndpoint){
                Method method = handlerClass.getMethod(handlerClassMethodName, HttpExchange.class, FileOutputStream.class,
                        long.class);
            System.out.println("AQUI");
                FileOutputStream fileOutputStream = ServerController.getInstance().createOrGetOutputFile(path);
                long now = DateHelper.nowMilliseconds();
                long resumedCaptureTime = this.pauseTime + (now - this.resumeTime);
                long captureMilliseconds = this.resumeTime == 0 ? now : resumedCaptureTime;
                System.out.println("HERE");
                method.invoke(instance,exchange, fileOutputStream, captureMilliseconds);
                System.out.println("PASE");
            }
            else if(instance instanceof StartEndpoint){
                Method method = handlerClass.getMethod(handlerClassMethodName, HttpExchange.class);
                method.invoke(instance,exchange);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            /* Loggear Error*/
            System.out.println(e.getMessage());
            Response.sendResponse("Error on the " + path + " handler", 500, exchange);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error al tratar de crear el archivo de:" + path);
            Response.sendResponse("Error while trying to store data for the" + path + " handler", 500, exchange);
        }
    }

}
