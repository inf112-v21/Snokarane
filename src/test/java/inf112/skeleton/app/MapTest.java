package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MapTest {
    Map map;
    Player player;

    TiledMap tiledMap;



    @Before
    public void init(){
        TmxMapLoader mapLoader = new TmxMapLoader();
        tiledMap = mapLoader.load("test-map.tmx");
        map.loadMapLayers(tiledMap);
        map = new Map();
        player = new Player();
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
}