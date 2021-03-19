package inf112.skeleton.app.ui.chat;

import java.util.List;

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
