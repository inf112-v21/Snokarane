package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void visitFlagThatIsNotAlreadyVisited(){
        Player player = new Player();
        Flag flag = new Flag(2, 2);

        assertTrue(player.visitFlag(flag));
        assertEquals(player.getVisitedFlags().get(0), flag);
    }

    @Test
    public void visitFlagThatHasBeenVisited(){
        Player player = new Player();
        Flag flag = new Flag(2, 2);
        player.visitFlag(flag);

        assertFalse(player.visitFlag(flag));
        assertEquals(player.getVisitedFlags().size(), 1);
    }
}
