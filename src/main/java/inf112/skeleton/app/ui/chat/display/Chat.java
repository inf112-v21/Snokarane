package inf112.skeleton.app.ui.chat.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.app.ui.chat.backend.ChatFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class takes internal chat data and styling options
 * and returns a fully formatted libgdx table
 * that can be used to render the chat directly into the main game screen.
 */
public class Chat {
    public ChatFormatter chat = new ChatFormatter();
    private Table chatTable = new Table();
    private TextureRegionDrawable chatBackground;
    private float fontSize = 0.8f;
    private Color chatColor = new Color();
    private Skin chatSkin;
    private float w = 0f, h = 0f, x = 0f, y = 0f;

    private enum ChatOutputColors{
        RED,
        ORANGE,
        GREEN,
        DEFAULT
    }

    public Chat(Skin skin){
        this.chatSkin = skin;
    }

    public void setSize(float w, float h){
        this.w = w;
        this.h = h;
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void setChatFontSize(float scale){
        this.fontSize = scale;
    }

    public void setChatColour(Color color){
        this.chatColor = color;
    }

    public void setChatData(ChatFormatter chat){
        this.chat = chat;
    }

    public void setChatBackground(String texturePath){
        this.chatBackground = new TextureRegionDrawable(new Texture("chat/chat-background.png"));
    }

    public Table getChatAsTable(){
        chatTable = new Table();
        if (chatBackground != null){
            chatTable.setBackground(chatBackground);
        }else {
            System.out.println("Chat background is not set.");
        }
        chatTable.setSkin(chatSkin);
        chatTable.setSize(w, h);
        chatTable.setPosition(x, y);
        chatTable.setColor(chatColor);
        chatTable.padLeft(5);
        chatTable.padBottom(5);
        chatTable.bottom().left();

        List<HashMap<String, ChatOutputColors>> messages = new ArrayList<>();

        int lineBreakLimit = 40; // TODO fix hardcoded value

        for (HashMap<String, String> hss: chat.getNamesWithMessages()){
            for (String s : hss.keySet()){
                // Splits message into two lines if message length is over line break limit
                // Can be improved to include N amount of lines but that isn't a priority right now
                if (s.length()+hss.get(s).length() > lineBreakLimit){
                    int breakLoc = lineBreakLimit-s.length();
                    String mess1 = hss.get(s).substring(0, breakLoc);
                    String mess2 = hss.get(s).substring(breakLoc);
                    if (s.equals("")){
                        // internal message
                        HashMap<String, ChatOutputColors> messageI = new HashMap<>();
                        messageI.put(mess1, ChatOutputColors.GREEN);
                        messages.add(messageI);

                        HashMap<String, ChatOutputColors> messageI2 = new HashMap<>();
                        messageI2.put(s + ": " + mess2, ChatOutputColors.GREEN);
                        messages.add(messageI2);
                    }else{
                        HashMap<String, ChatOutputColors> messageI = new HashMap<>();
                        messageI.put(s + ": " + mess1, ChatOutputColors.DEFAULT);
                        messages.add(messageI);

                        HashMap<String, ChatOutputColors> messageI2 = new HashMap<>();
                        messageI2.put(s + ": " + mess2, ChatOutputColors.DEFAULT);
                        messages.add(messageI2);
                    }
                }else {
                    if (s.equals("")){
                        HashMap<String, ChatOutputColors> messageI = new HashMap<>();
                        messageI.put(hss.get(s), ChatOutputColors.GREEN);
                        messages.add(messageI);
                    }else{
                        HashMap<String, ChatOutputColors> messageI = new HashMap<>();
                        messageI.put(s + ": " + hss.get(s), ChatOutputColors.DEFAULT);
                        messages.add(messageI);
                    }
                }
            }
        }

        if (messages.size()>0){
            HashMap<String, ChatOutputColors> welcomeMess = new HashMap<>();
            welcomeMess.put((messages.size()==1 ? "Welcome to chat! /h for help" : ""), ChatOutputColors.DEFAULT);
            messages.set(0, welcomeMess);
        }
        for (HashMap<String, ChatOutputColors> s : messages){
            String messageString = s.keySet().toArray()[0].toString();
            ChatOutputColors col = s.get(messageString);

            if (col == ChatOutputColors.DEFAULT){
                Label mess = new Label(messageString, chatSkin);
                mess.setFontScale(this.fontSize);
                mess.setColor(chatColor);
                chatTable.add(mess).left();
                chatTable.row();
            }else if (col == ChatOutputColors.GREEN){
                Label mess = new Label(messageString, chatSkin);
                mess.setFontScale(this.fontSize);
                Color c = new Color(0.1f, 0.9f, 0.1f, 1);
                mess.setColor(c);
                chatTable.add(mess).left();
                chatTable.row();
            }


        }

        // TODO as of now an input box needs to be added in the gamescreen class because of event handler
        return chatTable;
    }
}
