package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void PlayerIsInitializedAtPosition00(){
        Player player = new Player();

        assertEquals(player.getX(), 0);
        assertEquals(player.getY(), 0);
    }

    @Test
    public void PlayerCoordinatesAreUpdatedWhenMoved(){
        Player player = new Player();

        player.move(1, 1);

        assertEquals(player.getX(), 1);
        assertEquals(player.getY(), 1);
    }
}
