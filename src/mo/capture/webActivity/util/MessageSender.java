package mo.capture.webActivity.util;

import mo.capture.webActivity.plugin.WebBrowsingActivityConfiguration;
import mo.communication.PetitionResponse;
import mo.communication.RemoteClient;
import mo.communication.ServerConnection;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageSender {
    public static void sendMessage(String messageType, String messageContent){
        HashMap<String, Object> data = new HashMap<>();
        data.put(messageType, messageContent);
        PetitionResponse petitionResponse = new PetitionResponse(WebBrowsingActivityConfiguration.PLUGIN_MESSAGE_KEY, data);
        ArrayList<RemoteClient> clients = ServerConnection.getInstance().getClients();
        if(clients == null || clients.size() == 0){
            return;
        }
        for(RemoteClient client : clients){
            client.send(petitionResponse);
        }
    }
}
