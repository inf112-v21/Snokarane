package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles actions of the objects on the map
 */
public class Map {

    private static final int BOARD_X = 5;
    private static final int BOARD_Y = 5;

    // Layers of the map
    private TiledMapTileLayer boardLayer;
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer flagLayer;

    // Flags on the map are stored here for easy access
    List<Flag> flagPositions = new ArrayList<>();

    public Map(){
    }

    public int getBoardX(){
        return BOARD_X;
    }
    public int getBoardY(){
        return BOARD_Y;
    }

    /**
     * Load all map layers into their own member variable
     */
    public void loadMapLayers(TiledMap tiledMap){
        // Separate each layer from the tiledMap
        boardLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Board");
        playerLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Player");
        flagLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Flag");

        // Sneakily yoink the positions of the flags here, don't tell the OOP police
        getFlagPositionsFromLayer(flagLayer);
    }

    public TiledMapTileLayer getBoardLayer(){
        return boardLayer;
    }
    public TiledMapTileLayer getFlagLayer(){
        return flagLayer;
    }
    public TiledMapTileLayer getPlayerLayer(){
        return playerLayer;
    }

    /**
     * Get all flag positions in layer flag layer
     */
    private void getFlagPositionsFromLayer(TiledMapTileLayer flagLayer){
        List<Flag> flags = new ArrayList<>();

        for (int i = 0; i <= flagLayer.getWidth(); i++){
            for (int j = 0; j <= flagLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                if (flagLayer.getCell(i, j) != null){
                    flags.add(new Flag(i, j));
                }
            }
        }
        flagPositions.addAll(flags);
    }

    /**
     * Check if player moved on a flag
     */
    public Player checkForFlags(Player player){
        // Check if player moved onto a flag
        for (Flag f : flagPositions){
            if (f.getX() == player.getX() && f.getY() == player.getY()){
                player.visitFlag(f);
            }
        }
        return player;
    }

    /**
     * This function sets player win state to true if the visited flags amount equal all flags count in map
     */
    public Player checkIfPlayerWon(Player player){
        if (player.getVisitedFlags().size() == flagPositions.size()){
            player.isWinner = true;
        }
        return player;
    }

    /**
     * Clear cell at
     * @param player location (x, y)
     */
    public void clearCellAtPlayerPos(Player player){
        playerLayer.setCell(player.getX(), player.getY(), new TiledMapTileLayer.Cell());
    }

    /**
     * Set cell at
     * @param player location (x, y)
     */
    public void setCellAtPlayerPos(Player player, TiledMapTileLayer.Cell cell){
        playerLayer.setCell(player.getX(), player.getY(), cell);
    }

    /**
     * Get cell in either flag or board (whichever is currently placed at player pos) layer at
     * @param player location (x, y)
     */
    public TiledMapTileLayer.Cell getCellAtPlayerPos(Player player){
        int x = player.getX();
        int y = player.getY();
        // This should be reworked
        return (flagLayer.getCell(x, y) != null) ? flagLayer.getCell(x, y) : boardLayer.getCell(x, y);
    }
}
