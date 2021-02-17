package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.ArrayList;
import java.util.List;

public class Map {

    private List<Flag> flags = new ArrayList<>();
    private List<Player> players = new ArrayList<>();

    private int BOARD_X = 5;
    private int BOARD_Y = 5;

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
     * @return TiledMap object loaded from path
     * @param path path to .tmx file for map
     */
    public TiledMap loadTileMapFromFile(String path){
        return new TmxMapLoader().load(path);
    }

    /**
     * Get all flag positions in layer flag layer
     */
    public void getFlagPositionsFromLayer(TiledMapTileLayer flagLayer){
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
}
