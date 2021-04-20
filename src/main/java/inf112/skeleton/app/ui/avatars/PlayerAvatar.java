package inf112.skeleton.app.ui.avatars;

import inf112.skeleton.app.libgdx.PlayerConfig;

/**
 * Contains info that is needed to show information about a player in the waiting room or end game screen
 *  Player nr, name, avatar etc.
 */
public class PlayerAvatar {
    public PlayerConfig playerConfig;
    public String playerName;
    // 1 to n in order of connected to host, first to last
    public int playerNumber;

    // Empty constructor for kryo
    public PlayerAvatar(){}

    public PlayerAvatar(PlayerConfig config, String name){
        this.playerConfig = config;
        this.playerName = name;
    }

    public void givePlayerNumber(int number){
        this.playerNumber = number;
    }
}
