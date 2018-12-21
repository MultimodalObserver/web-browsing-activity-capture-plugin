package cl.informatica.usach.mo;

import cl.informatica.usach.mo.handlers.BaseHandler;
import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/* Clase Singleton que implementa funcionalidades de un router o enrutador. Su principal funcion es hacer un match entre
el recurso buscado en el servidor, mediante la URI consultada, especificamente el path y el manejador, controlador o handler
asociado a ese recurso mediante un mapa de llaves (Strings) y valores( mapa de tipo string , objetos RouteHandlerInfo).

 Se utiliza una clase POJO llamada RouteHandlerInfo, que guarda valores importantes para el correcto manejo de las rutas, como lo
 son:

 - Nombre completo de la clase que manejará la ruta (incluyendo el package).
 - Nombre del método de un objeto de la clase que será ejecutado. Los parametros que reciban estos metodos estan limitados
 por el tipo de peticion http que se realizo:

    - Para POST, los parametros son el http exchange (que contiene toda la info del intercambio cliente/servidor) y un timestamp
    de inicio de la captura, ya que solo con POST recibimos datos capturados por la extension.
    - Para GET u otros, el parametro es solo el exchange, ya que a partir de el se puede obtener toda la informacion.
    En caso de querer obtener los query params de un GET, por herencia, en los handlers está disponible el metodo
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
 */

public class Router {

    private static Router instance;
    private static final String BASE_PACKAGE = "cl.informatica.usach.mo.";
    private Map<String, Map<String, RouteHandlerInfo>> routes;

    private Router(){

        this.routes = new HashMap<>();

        RouteHandlerInfo keystrokesPostRouteHandlerInfo = new RouteHandlerInfo( BASE_PACKAGE + "handlers.KeystrokesHandler", "store");
        RouteHandlerInfo mouseUpsPostRouteHandlerInfo = new RouteHandlerInfo(BASE_PACKAGE + "handlers.MouseUpsHandler", "store");
        RouteHandlerInfo mouseMovesPostRouteHandlerInfo = new RouteHandlerInfo( BASE_PACKAGE + "handlers.MouseMovesHandler", "store");
        RouteHandlerInfo mouseClicksPostRouteHandlerInfo = new RouteHandlerInfo( BASE_PACKAGE + "handlers.MouseClicksHandler", "store");

        addRoute("/keystrokes","POST", keystrokesPostRouteHandlerInfo);
        addRoute("/mouseUps", "POST", mouseUpsPostRouteHandlerInfo);
        addRoute("/mouseMoves", "POST", mouseMovesPostRouteHandlerInfo);
        addRoute("/mouseClicks", "POST", mouseClicksPostRouteHandlerInfo);

    }

    public static Router getInstance(){
        if(instance == null){
            instance =  new  Router();
        }
        return instance;
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

    /* Principal metodo de esta clase, en donde mediante relfexion, se invoca el metodo de la clase
    registrados en las rutas.

    Se valida que exista la ruta que se quiere mapear, y además se valida que la ruta este registrada
    con el método http del request que esta accediendo a esa ruta, ya que una ruta puede ser manejada accedida
    con distintos metodos HTTP.
     */

    public void match(HttpExchange exchange, String captureInitTimestamp){
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String httpMethod = exchange.getRequestMethod();
        if(!this.routes.containsKey(path)){
            new BaseHandler().sendResponse("RouteHandlerInfo not defined in the server", 404, exchange);
            return;
        }
        else if(!this.routes.get(path).containsKey(httpMethod)){
            String response = "RouteHandlerInfo defined in the server, but not with "+ httpMethod + "http method";
            new BaseHandler().sendResponse(response, 404, exchange);
            return;
        }
        RouteHandlerInfo routeHandlerInfo = this.routes.get(path).get(httpMethod);
        Class handlerClass = null;
        try {
            handlerClass = Class.forName(routeHandlerInfo.getHandlerClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
            if(httpMethod.equals("POST")){
                Class[] parameterTypes = {HttpExchange.class, String.class};
                Method method = handlerClass.getMethod(routeHandlerInfo.getHandlerClassMethodName(), parameterTypes);
                method.invoke(instance,exchange, captureInitTimestamp);
            }
            else{
                Method method = handlerClass.getMethod(routeHandlerInfo.getHandlerClassMethodName(), HttpExchange.class);
                method.invoke(instance,exchange);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
