package inf112.skeleton.app.game.objects;

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
    public void playerIsInitializedAtPosition00(){
        assertEquals(player.getX(), 0);
        assertEquals(player.getY(), 0);
    }

    @Test
    public void PlayerDiesOutOfBounds(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        player.move(Direction.SOUTH);
        assertTrue(player.diedThisTurn);
    }


    @Test
    public void playerMovesInTheCorrectDirection() {
        assertTrue(player.getX() == 0 && player.getY() == 0);
        player.move(Direction.EAST);
        assertEquals(1,player.getX());
        assertEquals(0, player.getY());

        player.move(Direction.NORTH);
        assertEquals(1,player.getX());
        assertEquals(1, player.getY());

        player.move(Direction.WEST);
        assertEquals(0,player.getX());
        assertEquals(1, player.getY());

        player.move(Direction.SOUTH);
        assertEquals(0,player.getX());
        assertEquals(0, player.getY());
    }

    @Test
    public void playerRotatesCorrectly() {
        // Default is NORTH
        player.rotate(CardType.TURNRIGHT);
        assertEquals(Direction.EAST, player.getDirection());

        player.rotate(CardType.UTURN);
        assertEquals(Direction.WEST, player.getDirection());

        player.rotate(CardType.TURNLEFT);
        assertEquals(Direction.SOUTH, player.getDirection());
    }

    @Test
    public void wouldEndUpDoesNotMeet() {
        assertTrue(player.getX() == 0 && player.getY() == 0);
        player.wouldEndUp(Direction.NORTH);
        assertEquals(0, player.getX());
        assertEquals(0, player.getY());

        player.wouldEndUp(Direction.SOUTH);
        assertEquals(0, player.getX());
        assertEquals(0, player.getY());

        player.wouldEndUp(Direction.WEST);
        assertEquals(0, player.getX());
        assertEquals(0, player.getY());

        player.wouldEndUp(Direction.EAST);
        assertEquals(0, player.getX());
        assertEquals(0, player.getY());
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
