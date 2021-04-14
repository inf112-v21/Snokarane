package inf112.skeleton.app.ui.chat.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.ui.chat.backend.ChatFormatter;
import inf112.skeleton.app.ui.chat.backend.ChatterData;
import inf112.skeleton.app.ui.chat.backend.Message;
import inf112.skeleton.app.ui.chat.display.Chat;

import java.util.HashMap;
import java.util.List;

/**
 * Shared methods and variables between chat manager and client
 */
public abstract class Chatter {
    public Chat chat;
    public ChatterData cData = new ChatterData();
    private ChatterData internalMessenger = new ChatterData();

    public Chatter(){
        cData.setData("name", 0);
        internalMessenger.setData("", -1);
    }

    public void initializeChat(RoboGame game) {
        chat = new Chat(game.skin);
    }

    public void initializeChat(RoboGame game, float fontScale, Color chatColor, String backgroundImagePath, float w, float h, float x, float y) {
        chat = new Chat(game.skin);
        setChatDisplay(fontScale, chatColor, backgroundImagePath);
        setChatScaling(w, h, x, y);
    }

    public void setChatDisplay(float fontScale, Color chatColor, String backgroundImagePath){
        this.chat.setChatFontSize(fontScale);
        this.chat.setChatColour(chatColor);
        this.chat.setChatBackground(backgroundImagePath);
    }

    public void setChatScaling(float w, float h, float x, float y){
        this.chat.setSize(w, h);
        this.chat.setPosition(x, y);
    }

    public abstract void sendMessage(String message);

    /**
     * Sends message that only displays on this client, sends without name
     * used for information, displaying help commands
     * @param message message to send internally
     * @param net network object to permanently store message in, as all other message storage structures get wiped and updated from network
     */
    public void sendInternalMessage(String message, Network net){
        net.messagesRecived.add(new Message(message, internalMessenger));
    }

    public void setName(String name){
        this.cData.name = name;
    }

    public void setChatterID(int id){
        this.cData.chatterID = id;
    }

    public ChatterData getChatterData() {
        return cData;
    }

    public void updateChat(List<Message> messages){
        ChatFormatter chatForm = new ChatFormatter();
        chatForm.formatMessagesToDisplay(messages);

        chat.setChatData(chatForm);
    }

    public Table getChatAsTable(){
        return chat.getChatAsTable();
    }
}
