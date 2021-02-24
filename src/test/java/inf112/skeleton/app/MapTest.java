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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    public void twoFlagsAreLoadedFromTestMap(){
        assertEquals(2, map.flagPositions.size());
    }

    @Test
    public void checkIfPlayerCellIsSet(){
        // flag is at 4, 1
        int playerMoveX = 1;
        int playerMoveY = 4;

        player.move(playerMoveX, playerMoveY);
        map.checkForFlags(player);

        assertEquals(1, player.getVisitedFlags().size());
    }
}