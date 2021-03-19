package inf112.skeleton.app.ui.chat;

import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat class for network host
 */
public class ChatManager implements IChatter{

    private NetworkHost network;
    private List<Message> messages = new ArrayList<>();
    private Message nextMessage;

    /**
     * Assign
     * @param network
     * class and add empty message to first element of list to avoid
     * indexoutofbounds exceptions
     */
    public ChatManager(NetworkHost network){
        this.network = network;

        // initialize first empty message with ID 0
        Message firstMessage = new Message("", this);
        firstMessage.assignID(0);
        messages.add(firstMessage);
    }

    /**
     * Adds message to messages list
     * @param message message to add
     */
    @Override
    public void sendMessage(String message) {
        Message m = new Message(message, this);
        m.assignID(getNextId());
        messages.add(m);
    }

    /**
     * Get next available ID to assign to a new message
     */
    private int getNextId(){
        return messages.get(messages.size()-1).messageID+1;
    }

    /**
     * Get all messages recieved from network
     */
    private void getAllMessages() {
        for (Message m : network.getMessages()){
            m.assignID(getNextId());
            messages.add(m);
        }
    }

    /**
     * Get messanger of sender with id
     * Returns null if no message with id found
     * @param messageID
     * @return sender as IChatter
     */
    public IChatter getMessenger(int messageID) {
        for (Message m : messages){
            if (m.messageID == messageID){
                return m.sender;
            }
        }
        return null;
    }
}
