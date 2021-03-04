package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.util.HashMap;
import java.util.List;

/**
 * Contains information about the map that is to be sent to all clients
 * This exists so we can nicely package all information about what
 * is place on the map to every client on the network.
 */
public class Map {

    // Layers of the map
    private TiledMapTileLayer boardLayer;
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer flagLayer;

    // Cells for each player state
    public TiledMapTileLayer.Cell playerNormal;
    public TiledMapTileLayer.Cell playerWon;
    public TiledMapTileLayer.Cell none = new TiledMapTileLayer.Cell();

    // NOTE! No args constructor required so kryonet can serialize
    public Map(){ }

    public void setPlayerCells(TiledMapTileLayer.Cell playerNormal, TiledMapTileLayer.Cell playerWon){
        this.playerNormal = playerNormal;
        this.playerWon = playerWon;
    }

    public boolean containsPlayer(GridPoint2 position) {
        return playerLayer.getCell(position.x, position.y) == playerNormal;
    }

    public boolean isEmpty(GridPoint2 position) {
        return playerLayer.getCell(position.x, position.y) == none;
    }

    public void loadLayers(TiledMapTileLayer board, TiledMapTileLayer player, TiledMapTileLayer flag){
        this.boardLayer = board;
        this.playerLayer = player;
        this.flagLayer = flag;
    }

    // Gets & Sets TODO: use getter setter things
    public TiledMapTileLayer getBoardLayer  (){ return boardLayer   ;}
    public TiledMapTileLayer getPlayerLayer (){ return playerLayer  ;}
    public TiledMapTileLayer getFlagLayer   (){ return flagLayer    ;}

    public void setBoardLayer  (TiledMapTileLayer boardLayer    ){ this.boardLayer  = boardLayer    ;}
    public void setPlayerLayer (TiledMapTileLayer playerLayer   ){ this.playerLayer = playerLayer   ;}
    public void setFlagLayer   (TiledMapTileLayer flagLayer     ){ this.flagLayer   = flagLayer     ;}

    public void clearCell(int playerX, int playerY) {
        playerLayer.setCell(playerX, playerY, none);
    }

    public void setCell(int playerX, int playerY, TiledMapTileLayer.Cell cellType) {
        playerLayer.setCell(playerX, playerY, cellType);
    }

    public MapLayerWrapper getWrapper() {
        MapLayerWrapper wrapper = new MapLayerWrapper();
        for (int x = 0; x < Game.BOARD_X; x++) {
            for (int y = 0; y < Game.BOARD_Y; y++) {
                wrapper.playerLayer[x][y] = this.playerLayer.getCell(x, y);
                wrapper.boardLayer[x][y] = this.boardLayer.getCell(x, y);
                wrapper.flagLayer[x][y] = this.flagLayer.getCell(x, y);
            }
        }
        return wrapper;
    }

    public void setCellsFromWrapper(MapLayerWrapper wrapper) {
        for (int x = 0; x < Game.BOARD_X; x++) {
            for (int y = 0; y < Game.BOARD_Y; y++) {
                this.playerLayer.setCell(x, y, wrapper.playerLayer[x][y]);
                this.boardLayer.setCell(x, y, wrapper.boardLayer[x][y]);
                this.flagLayer.setCell(x, y, wrapper.flagLayer[x][y]);
            }
        }
    }
}
