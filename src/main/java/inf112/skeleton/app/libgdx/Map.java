package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains information about the map that is to be sent to all clients
 * This exists so we can nicely package all information about what
 * is place on the map to every client on the network.
 */
public class Map {

    private final int BOARD_X = Game.BOARD_X;
    private final int BOARD_Y = Game.BOARD_Y;
    private int ID;
    public List<Flag> flagList;

    /**
     * 2D map like structure contain information about all players in the game.
     */
    public PlayerRenderInformation [][] playerLayer = new PlayerRenderInformation [BOARD_X][BOARD_Y];
    public boolean [][] holeLayer = new boolean[BOARD_X][BOARD_Y];
    public int [][] gearLayer = new int[BOARD_X][BOARD_Y];

    //TODO blir bare mellomlagret her, kanskje en dårlig ide?
    public List<GridPoint2> spawnPoints = new ArrayList<>();

    /**
     * This is a sort of replacement for tuples that java lack,
     * Only thing clients needs to render players is state and direction so this is wrapped
     * into this class.
     */
    public static class PlayerRenderInformation{
        public PlayerToken.CHARACTER_STATES state = PlayerToken.CHARACTER_STATES.NONE;
        public PlayerToken.Direction dir = PlayerToken.Direction.NORTH;
        public PlayerRenderInformation(){}
    }

    // NOTE! No args constructor required so kryonet can serialize
    public Map(){
        clearPlayerLayer();
    }

    // Set map ID
    public void setID(int ID) {
        this.ID = ID;
    }

    // Sets entire player layer to default objects
    public void clearPlayerLayer() {
        for (int i = 0; i<BOARD_X; i++){
            for (int j = 0; j<BOARD_Y; j++){
                playerLayer[i][j] = new PlayerRenderInformation();
            }
        }
    }

    public void registerFlags(List<PlayerToken> players) {
        for (PlayerToken player : players) {
            for (Flag flag : flagList) {
                if (player.getX() == flag.getX() && player.getY() == flag.getY()) {
                    player.visitFlag(flag);
                }
            }
        }
    }
    /**
     * Checks if any players have won the current game. If multiple players win
     * only the first one is returned
     * @param players All the players playing the game
     * @return The first person in the list who has won.
     */
    public PlayerToken hasWon(List<PlayerToken> players) {
        for (PlayerToken player : players) {
            if (player.getVisitedFlags().size() == flagList.size())
                return player;
        }
        return null;
    }

    public boolean isHole(int x, int y) {
        return holeLayer[x][y];
    }

    public int isGear(int x, int y) {
        return gearLayer[x][y];
    }
    /**
     * Loads player from network into map
     * @param wrapper The NetworkDataWrapper that contains the players
     */
    public void loadPlayers(NetworkDataWrapper wrapper) {
        clearPlayerLayer();
        for (int i = 0; i<wrapper.PlayerTokens.size(); i++){
            PlayerToken token = wrapper.PlayerTokens.get(i);
            if (token.ID == this.ID) {
                playerLayer[token.getX()][token.getY()].state = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
            }
            else {
                playerLayer[token.getX()][token.getY()].state = token.charState;
            }
            playerLayer[token.getX()][token.getY()].dir = token.getDirection();
        }
    }
}
