package inf112.skeleton.app.ui.chat.display;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.app.ui.chat.backend.ChatFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class takes internal chat data and in styling options
 * and returns a fully formatted libgdx table
 * that can be used to render the chat directly into the main game screen.
 */
public class Chat {
    private ChatFormatter chat = new ChatFormatter();
    private Table chatTable = new Table();
    private TextureRegionDrawable chatBackground;
    private float fontSize = 1f;
    private Color chatColor = new Color();
    private Skin chatSkin;
    private float w = 0f, h = 0f , x = 0f, y = 0f;

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
        int subMenuHeight = 200;
        int sideMenuWidth = 279; // TODO fix hardcoded values
        int messageWidth = 200;

        Table chatTable = new Table();
        if (chatBackground != null){
            chatTable.setBackground(chatBackground);
        }else {
            System.out.println("Chat background is not set.");
        }
        chatTable.setSkin(chatSkin);
        chatTable.setSize(w, h);
        chatTable.setPosition(x, y);
        chatTable.setColor(chatColor);

        List<String> messages = new ArrayList<>();

        for (String s : chat.getNamesWithMessages().keySet()){
            // TODO need to fix this using messenger ID
            messages.add(chat.getNamesWithMessages().get(s) + ": " + s);
        }

        chatTable.padLeft(5);
        chatTable.padBottom(5);
        chatTable.bottom().left();

        for (String s : messages){
            Label mess = new Label(s, chatSkin);
            mess.setFontScale(this.fontSize);
            mess.setColor(chatColor);
            chatTable.add(mess).width(messageWidth);
            chatTable.row();
        }

        chatTable.setDebug(true);
        return chatTable;
    }
}
