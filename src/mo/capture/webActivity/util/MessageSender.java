package mo.capture.webActivity.util;

import com.google.gson.Gson;
import mo.capture.webActivity.plugin.WebBrowsingActivityConfiguration;
import mo.capture.webActivity.plugin.model.DataMessage;
import mo.communication.PetitionResponse;
import mo.communication.RemoteClient;
import mo.communication.ServerConnection;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageSender {

    public static void sendMessage(String messageType, String messageData){
        ArrayList<RemoteClient> clients = ServerConnection.getInstance().getClients();
        if(clients == null || clients.size() == 0){
            return;
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put(messageType, messageData);
        PetitionResponse petitionResponse = new PetitionResponse(WebBrowsingActivityConfiguration.PLUGIN_MESSAGE_KEY, data);
        for(RemoteClient client : clients){
            client.send(petitionResponse);
        }
    }


}
