package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class MapTest {

    Player player = new Player();
    TiledMap tiledMap = new TiledMap();
    Map map = new Map();

    @Before
    public void init(){
        tiledMap = new TmxMapLoader().load("test-map.tmx");
        map.loadMapLayers(tiledMap);
    }

    @Test
    public void allMapLayersWereLoaded(){
        assertNotNull(map.getBoardLayer());
        assertNotNull(map.getFlagLayer());
        assertNotNull(map.getPlayerLayer());
    }

    @Test
    //Amazing test
    public void boardIs5Times5() {
        map = new Map();
        assertEquals(5, map.getBoardX());
        assertEquals(5, map.getBoardY());
    }

    @Test
    public void atLeastOneFlagIsLoaded(){
        assertNotEquals(0, map.flagPositions.size());
    }

    @Test
    public void twoFlagsAreLoaded(){
        assertEquals(2, map.flagPositions.size());
    }

    public void movePlayerToFlag(){
        // flag is at 4, 1
        int playerMoveX = 1;
        int playerMoveY = 4;

        player.move(playerMoveX, playerMoveY);
    }

    @Test
    public void checkIfPlayerFlagVisitsAreRegistered(){
        movePlayerToFlag();
        map.checkForFlags(player);

        assertEquals(1, player.getVisitedFlags().size());
    }

    @Test
    public void checkIfPlayerStandsOnCorrectCell(){
        movePlayerToFlag();
        map.checkForFlags(player);

        assertNotEquals(null, map.getCellAtPlayerPos(player));
    }
}