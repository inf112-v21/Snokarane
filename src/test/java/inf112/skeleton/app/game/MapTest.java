package inf112.skeleton.app.game;

import inf112.skeleton.app.game.objects.Direction;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import org.junit.Before;
import org.junit.Test;

import inf112.skeleton.app.libgdx.Map;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MapTest {

    Map map;
    @Before
    public void makeMap() {
        this.map = new Map();
    }

    @Test
    public void TestBoundsFunction() {
        assertFalse(Map.isInBounds(-1, 5));
        assertFalse(Map.isInBounds(5, -1));
        assertFalse(Map.isInBounds(100, 5));
        assertFalse(Map.isInBounds(5, 100));
        assertTrue(Map.isInBounds(0, 0));

        assertTrue(Map.isInBounds(0, 1, Direction.NORTH));
        assertTrue(Map.isInBounds(0, 1, Direction.SOUTH));
        assertTrue(Map.isInBounds(1, 0, Direction.EAST));
        assertTrue(Map.isInBounds(1, 0, Direction.WEST));

        assertFalse(Map.isInBounds(0, Game.BOARD_Y-1, Direction.NORTH));
        assertFalse(Map.isInBounds(0, 0, Direction.SOUTH));
        assertFalse(Map.isInBounds(Game.BOARD_X-1, 0, Direction.EAST));
        assertFalse(Map.isInBounds(0, 0, Direction.WEST));
    }

    @Test
    public void CanNotGoThroughWalls(){
        map.wallLayer[5][5] = new boolean[]{true, true, true, true};
        assertFalse(map.canGo(5, 4, Direction.NORTH));

        map.wallLayer[3][3] = new boolean[]{true, false, false, false};
        assertTrue(map.canGo(3,3, Direction.SOUTH));
        assertFalse(map.canGo(3, 3, Direction.NORTH));
    }

    @Test
    public void LaserDoesNotShootThroughWallsAndPlayers() {
        map.laserShooters.add(new Map.LaserShooter(Direction.NORTH, 1, 0, 0));
        Map.PlayerRenderInformation player = new Map.PlayerRenderInformation();
        player.state = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;

        NetworkDataWrapper wrapper = new NetworkDataWrapper();
        wrapper.PlayerTokens = new ArrayList<>();

        map.shootLasers(wrapper);
        assertTrue(map.laserLayer[0][5][0] > 0);

        //Test that it doesn't shoot through a player
        map.playerLayer[0][4] = player;
        map.clearLasers();
        map.shootLasers(wrapper);
        assertFalse(map.laserLayer[0][5][0] > 0);

        //Only blocks the sideways directions, not north/south
        map.wallLayer[0][3] = new boolean[] {false, true, false, true};
        map.clearLasers();
        map.shootLasers(wrapper);
        assertTrue(map.laserLayer[0][4][0] > 0);

        //Blocks north
        map.wallLayer[0][3] = new boolean[] {true, false, false, false};
        map.clearLasers();
        map.shootLasers(wrapper);
        assertFalse(map.laserLayer[0][4][0] > 0);

        //Blocks south
        map.wallLayer[0][3] = new boolean[] {false, false, true, false};
        map.clearLasers();
        map.shootLasers(wrapper);
        assertFalse(map.laserLayer[0][4][0] > 0);

        //No obstacle should make it fire over the entire map
        map.laserShooters.add(new Map.LaserShooter(Direction.NORTH, 1, 0, 5));
        map.shootLasers(wrapper);
        assertTrue(map.laserLayer[0][Game.BOARD_Y-1][0] > 0);
    }
}
