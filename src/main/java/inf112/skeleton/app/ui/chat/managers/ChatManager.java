package inf112.skeleton.app.ui.chat.managers;

import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.chat.backend.Message;


/**
 * Chat class for network host
 */
public class ChatManager extends Chatter implements IChatter {

    private final NetworkHost host;

    /**
     * Assign
     * class and add empty message to first element of list to avoid
     * indexoutofbounds exceptions
     */
    public ChatManager(NetworkHost host){
        this.host = host;
        initializeMessageList();
    }

    // initialize first message as welcome message with ID 0
    public void initializeMessageList(){
        Message firstMessage = new Message("Welcome to the chat!", cData);
        firstMessage.assignID(0);
        host.sendMessageToAll(firstMessage);
    }

    /**
     * Adds message to messages list
     * @param message message to add
     */
    @Override
    public void sendMessage(String message) {
        Message m = new Message(message, cData);
        m.assignID(getNextId());
        host.sendMessageToAll(m);
    }

    /**
     * Get next available ID to assign to a new message
     */
    private int getNextId(){
        return host.messagesRecived.get(host.messagesRecived.size()-1).messageID+1;
    }
}
