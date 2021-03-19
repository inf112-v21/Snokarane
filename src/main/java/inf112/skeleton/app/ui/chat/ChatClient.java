package inf112.skeleton.app.ui.chat;

import inf112.skeleton.app.network.NetworkClient;

/**
 * Chat class for network client
 */
public class ChatClient implements IChatter {

    private NetworkClient network;
    private Message previousMessage;

    public ChatClient(NetworkClient network){ this.network = network; }

    /**
     * Sends message to manager
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        previousMessage = new Message(message, this);
        network.sendMessage(previousMessage);
    }
}
