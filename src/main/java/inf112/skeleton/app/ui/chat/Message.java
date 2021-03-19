package inf112.skeleton.app.ui.chat;

public class Message {
    public String message;
    public IChatter sender;
    public int messageID;

    public Message(String message, IChatter sender){

    }

    // No args constructor required for kryo
    public Message(){}

    public IChatter getSender(){
        return sender;
    }

    public void assignID(int messageID){
        this.messageID = messageID;
    }
}
