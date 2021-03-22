package inf112.skeleton.app.ui.chat.backend;

public class Message {
    public String message;
    public ChatterData sender;
    public int messageID;

    public Message(String message, ChatterData sender){
        this.message = message;
        this.sender = sender;
    }

    // No args constructor required for kryo
    public Message(){}

    public ChatterData getSender(){
        return sender;
    }

    public void assignID(int messageID){
        this.messageID = messageID;
    }
}
