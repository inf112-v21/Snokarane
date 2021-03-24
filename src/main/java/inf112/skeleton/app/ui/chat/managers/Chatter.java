package inf112.skeleton.app.ui.chat.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.ui.chat.backend.ChatFormatter;
import inf112.skeleton.app.ui.chat.backend.ChatterData;
import inf112.skeleton.app.ui.chat.backend.Message;
import inf112.skeleton.app.ui.chat.display.Chat;

import java.util.List;

/**
 * Shared methods and variables between chat manager and client
 */
public abstract class Chatter {
    public Chat chat;
    public ChatterData cData = new ChatterData();

    public Chatter(){
        cData.setData("name", 0);
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
