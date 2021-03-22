package inf112.skeleton.app.ui.chat.managers;

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
    /**
     *  Set name to chatter
     */
    void setName(String name);
    /**
     * Set id to chatter
     */
    void setChatterID(int id);
    /**
     * Get Get chatter data
     */
    ChatterData getChatterData();
}
