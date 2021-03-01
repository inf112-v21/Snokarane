package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class MapTest {

    PlayerToken player = new PlayerToken();
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

    @Test
    public void checkIfPlayerWinsWhenWinConditionsAreFulfilled(){
        // Win conditions as of writing this test is visiting all flags in the correct order
        movePlayerToFlag();
        map.checkForFlags(player);

        List<Flag> playerFlags = player.getVisitedFlags();
        List<Flag> mapFlags = map.flagPositions;

        // One flag only
        assertTrue(mapFlags.contains(playerFlags.get(0)));

        // Move player to other flag, otherwise the rest of these asserts won't pass
        player.move(3, 0);
        map.checkForFlags(player);

        // Correct size only
        assertEquals(playerFlags.size(), mapFlags.size());

        // Correct order and size
        assertEquals(playerFlags, mapFlags);
    }
}