package inf112.skeleton.app.ui.chat.backend;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Formats internal chat data to filter/sort out data
 * to contain information that is needed to render the chat.
 */
public class ChatFormatter {
    //              Name    Message
    public HashMap<String, String> messages = new HashMap<>();

    // TODO should use tuple instead here or something more similar to tuples than this
    public List<HashMap<String, String>> allMessages = new ArrayList<>();

    //              ID      Name
    public HashMap<Integer, String> chatters = new HashMap<>();

    /**
     * Formats messages into two hashmaps that contain message, messenger name and messenger ID
     */
    public void formatMessagesToDisplay(List<Message> messages){
        for (Message m : messages){
            chatters.put(m.sender.chatterID, m.sender.name);

            this.messages.put(m.sender.name, m.message);
            allMessages.add(this.messages);
            this.messages = new HashMap<>();
        }
    }

    /**
     * Returns list of key value pair of (name, message)
     */
    public List<HashMap<String, String>> getNamesWithMessages(){
        return this.allMessages;
    }
}
