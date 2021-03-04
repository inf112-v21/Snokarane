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

    public PlayerToken.CHARACTER_STATES [][] playerLayer = new PlayerToken.CHARACTER_STATES [BOARD_X][BOARD_Y];
    public Flag            [][] flagLayer      = new Flag          [BOARD_X][BOARD_Y];
    public BoardTileTypes  [][] boardLayer     = new BoardTileTypes[BOARD_X][BOARD_Y];

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
        return playerLayer[position.x][position.y] != PlayerToken.CHARACTER_STATES.NONE;
    }
    public void clearPlayerLayer() {
        for (int i = 0; i<BOARD_X; i++){
            for (int j = 0; j<BOARD_Y; j++){
                playerLayer[i][j] = PlayerToken.CHARACTER_STATES.NONE;
            }
        }
    }
    public void loadPlayers(NetworkDataWrapper wrapper) {
        clearPlayerLayer();
        for (int i = 0; i<wrapper.PlayerTokens.size(); i++){
            PlayerToken token = wrapper.PlayerTokens.get(i);
            if (token.ID == this.ID) {
                playerLayer[wrapper.PlayerTokens.get(i).getX()][wrapper.PlayerTokens.get(i).getY()] = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
            }
            else {
                playerLayer[wrapper.PlayerTokens.get(i).getX()][wrapper.PlayerTokens.get(i).getY()] = wrapper.PlayerTokens.get(i).charState;
            }
        }
    }

    public void clearCell(int playerX, int playerY) {
        playerLayer[playerX][playerY] = PlayerToken.CHARACTER_STATES.NONE;
    }

    public void setCell(int playerX, int playerY, PlayerToken token) {
        if (token.ID == this.ID) {
            playerLayer[playerX][playerY] = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
        }
        else {
            playerLayer[playerX][playerY] = token.charState;
        }
    }
}
