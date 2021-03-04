package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains information about the map that is to be sent to all clients
 * This exists so we can nicely package all information about what
 * is place on the map to every client on the network.
 */
public class Map {

    int BOARD_X = Game.BOARD_X;
    int BOARD_Y = Game.BOARD_Y;
    int ID;

    public PlayerRenderInformation [][] playerLayer = new PlayerRenderInformation [BOARD_X][BOARD_Y];
    public Flag            [][] flagLayer      = new Flag          [BOARD_X][BOARD_Y];
    public BoardTileTypes  [][] boardLayer     = new BoardTileTypes[BOARD_X][BOARD_Y];

    class PlayerRenderInformation{
        public PlayerToken.CHARACTER_STATES state = PlayerToken.CHARACTER_STATES.NONE;
        public PlayerToken.Direction dir = PlayerToken.Direction.NORTH;
        public PlayerRenderInformation(){}
    }

    enum BoardTileTypes{
        Tile,
        Hole,
        Belt
    }

    // NOTE! No args constructor required so kryonet can serialize
    public Map(){
        clearPlayerLayer();
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean containsPlayer(GridPoint2 position) {
        return playerLayer[position.x][position.y].state != PlayerToken.CHARACTER_STATES.NONE;
    }
    public void clearPlayerLayer() {
        for (int i = 0; i<BOARD_X; i++){
            for (int j = 0; j<BOARD_Y; j++){
                playerLayer[i][j] = new PlayerRenderInformation();
            }
        }
    }
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
