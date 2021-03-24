package inf112.skeleton.app.ui.chat.managers;

import com.badlogic.gdx.graphics.Color;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.ui.chat.backend.ChatterData;


/**
 * Interface for a chat contender (chatter)
 * You know... someone that can chat
 */
public interface IChatter {
    /**
     * Sends message to chat manager
     */
    void sendMessage(String message);
}
