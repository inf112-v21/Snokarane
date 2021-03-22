package inf112.skeleton.app.ui.chat.managers;

import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.ui.chat.backend.ChatterData;
import inf112.skeleton.app.ui.chat.backend.Message;

/**
 * Chat class for network client
 */
public class ChatClient implements IChatter {

    private Message previousMessage;
    private String chatterName;
    private ChatterData cData = new ChatterData();

    public ChatClient(){  }
    // No args constructor required for kryo

    /**
     * Sends message to manager
     * @param message
     */
    public void sendMessage(String message, NetworkClient client) {
        previousMessage = new Message(message, cData);
        client.sendMessage(previousMessage);
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void setName(String name){
        this.chatterName = name;
    }

    @Override
    public void setChatterID(int id){
        this.cData.setData(chatterName, id);
    }

    @Override
    public ChatterData getChatterData() {
        return cData;
    }
}
