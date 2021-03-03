package inf112.skeleton.app;

import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PlayerTokenTest {

    PlayerToken player;

    @Before
    public void init(){
        player = new PlayerToken();
    }

    @Test
    public void PlayerIsInitializedAtPosition00(){
        assertEquals(player.getX(), 0);
        assertEquals(player.getY(), 0);
    }


    @Test
    public void PlayerCoordinatesAreUpdatedWhenMoved(){
        /**
         * TODO: DEPRECATED
         * player.move(1, 1);
         */


        assertEquals(player.getX(), 1);
        assertEquals(player.getY(), 1);
    }

    @Test
    public void visitFlagThatIsNotAlreadyVisited(){
        Flag flag = new Flag(2, 2);

        assertTrue(player.visitFlag(flag));
        assertEquals(player.getVisitedFlags().get(0), flag);
    }

    @Test
    public void visitFlagThatHasBeenVisited(){
        Flag flag = new Flag(2, 2);
        player.visitFlag(flag);

        assertFalse(player.visitFlag(flag));
        assertEquals(player.getVisitedFlags().size(), 1);
    }

    @Test
    public void visitingAllFlagsRegardlessOfOrderWins(){
        PlayerToken player1 = new PlayerToken();
        PlayerToken player2 = new PlayerToken();
        List<Flag> flags = new ArrayList<>();

        Flag flag1 = new Flag(1, 1);
        Flag flag2 = new Flag(2, 2);

        flags.add(flag1);
        flags.add(flag2);

        player1.visitFlag(flag1);
        player1.visitFlag(flag2);
        player2.visitFlag(flag2);
        player2.visitFlag(flag1);

        assertEquals(player1.getVisitedFlags().size(), flags.size());
        assertEquals(player2.getVisitedFlags().size(), flags.size());

    }
}
