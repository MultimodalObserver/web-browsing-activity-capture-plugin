package cl.informatica.usach.mo;

import cl.informatica.usach.mo.controllers.ServerController;

public class Main {

    public static void main(String[] args) {
        ServerController serverController = new ServerController();
        serverController.getServerConfiguration();
    }
}
