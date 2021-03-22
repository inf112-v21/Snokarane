package inf112.skeleton.app.ui.chat.managers;

import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.chat.backend.ChatterData;
import inf112.skeleton.app.ui.chat.backend.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat class for network host
 */
public class ChatManager implements IChatter {

    private ChatterData cData = new ChatterData();
    private String chatterName;
    public List<Message> messages = new ArrayList<>();

    /**
     * Assign
     * class and add empty message to first element of list to avoid
     * indexoutofbounds exceptions
     */
    public ChatManager(){
    }

    // initialize first message as welcome message with ID 0
    public void initializeMessageList(){
        Message firstMessage = new Message("Welcome to the chat!", cData);
        firstMessage.assignID(0);
        messages.add(firstMessage);
    }

    /**
     * Adds message to messages list
     * @param message message to add
     */
    @Override
    public void sendMessage(String message) {
        Message m = new Message(message, cData);
        m.assignID(getNextId());
        messages.add(m);
    }

    @Override
    public void setName(String name) {
        this.chatterName = name;
    }

    @Override
    public void setChatterID(int id) {
        this.cData.setData(chatterName, id);
    }

    @Override
    public ChatterData getChatterData() {
        return cData;
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
    public void getAllMessages(NetworkHost host) {
        List<Message> messagesToAdd = new ArrayList<>();
        for (Message m : host.getMessages()){
            m.assignID(getNextId());
            messagesToAdd.add(m);
        }
        messages.addAll(messagesToAdd);
    }

    /**
     * Get messanger of sender with id
     * Returns null if no message with id found
     * @param messageID
     * @return sender as IChatter
     */
    public ChatterData getMessenger(int messageID) {
        for (Message m : messages){
            if (m.messageID == messageID){
                return m.sender;
            }
        }
        return null;
    }
}
