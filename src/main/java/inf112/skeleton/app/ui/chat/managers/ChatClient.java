package inf112.skeleton.app.ui.chat.managers;

import com.badlogic.gdx.graphics.Color;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.ui.chat.backend.ChatterData;
import inf112.skeleton.app.ui.chat.backend.Message;
import inf112.skeleton.app.ui.chat.display.Chat;

/**
 * Chat class for network client
 */
public class ChatClient extends Chatter implements IChatter {

    private NetworkClient client;

    /**
     * @param client network client that sends messages to host
     */
    public ChatClient(NetworkClient client){
        this.client = client;
    }

    /**
     * Sends message to manager
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        client.sendMessage(new Message(message, this.cData));
    }

}
