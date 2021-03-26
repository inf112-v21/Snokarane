package inf112.skeleton.app.game;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.*;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class GameHostTest {
    GameHost host;
    PlayerToken player;

    @Before
    public void init() {
        NetworkHost network = new NetworkHost();
        host = new GameHost(network);
        host.map = new Map();
        player = new PlayerToken();
        host.clientPlayers.put(0, player);
    }

    private Card makeCard(CardType type) {
        Card card = new Card();
        card.setCardType(type);
        return card;
    }

    @Test
    public void RotateCardsRotatesPlayerCorrectly(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        assertSame(player.getDirection(), Direction.NORTH);
        host.resolveCard(makeCard(CardType.TURNLEFT), player);
        assertSame(player.getDirection(), Direction.WEST);
        host.resolveCard(makeCard(CardType.UTURN), player);
        assertSame(player.getDirection(), Direction.EAST);
        host.resolveCard(makeCard(CardType.TURNRIGHT), player);
        assertSame(player.getDirection(), Direction.SOUTH);
    }

    @Test
    public void MoveCardsWorkCorrectly(){
        assertEquals(0, player.getY());
        host.resolveCard(makeCard(CardType.FORWARDONE), player);
        assertEquals(1, player.getY());
        host.resolveCard(makeCard(CardType.BACK_UP), player);
        assertEquals(0, player.getY());
        host.resolveCard(makeCard(CardType.FORWARDTWO), player);
        assertEquals(2, player.getY());
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertEquals(5, player.getY());

        host.map.wallLayer[0][5] = new boolean[] {true, false, false, false};
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertEquals(5, player.getY());

        host.map.holeLayer[1][5] = true;
        host.resolveCard(makeCard(CardType.TURNRIGHT), player);
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertTrue(player.diedThisTurn);
        player.diedThisTurn = false;

        assertEquals(0, player.getY());
        assertEquals(0, player.getX());
        assertTrue(player.getDirection() == Direction.NORTH);
        PlayerToken player1 = new PlayerToken();
        player1.position = new GridPoint2(0, 1);
        host.clientPlayers.put(1, player1);
        host.map.loadPlayers(host.wrapper().PlayerTokens);
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertEquals(3, player.getY());
        assertEquals(4, player1.getY());

        //Wall should block
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertEquals(4, player.getY());
        assertEquals(5, player1.getY());

        host.map.wallLayer[0][5] = new boolean[] {false, false, false, false};
        host.map.holeLayer[0][6] = true;
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);

        assertTrue(player.diedThisTurn);
        assertTrue(player1.diedThisTurn);
    }

    @Test
    public void repairTilesRepair(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        int hp = player.hp;
        host.map.loadPlayers(host.wrapper().PlayerTokens);
        host.map.repairLayer[0][0] = true;
        host.endOfTurn();
        assertEquals(hp+1, player.hp);
    }

    @Test
    public void gearTilesRotate(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        assertEquals(Direction.NORTH, player.getDirection());

        host.map.gearLayer[0][0] = 1;
        host.endOfTurn();
        assertEquals(Direction.EAST, player.getDirection());

        host.map.gearLayer[0][0] = 2;
        host.endOfTurn();
        assertEquals(Direction.NORTH, player.getDirection());
    }

    @Test
    public void beltsMovePlayersRotateAndDontPush(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        host.map.beltLayer[0][0] = new Map.BeltInformation(Direction.NORTH, false, 0);
        host.map.beltLayer[0][1] = new Map.BeltInformation(Direction.NORTH, true, 0);
        host.map.beltLayer[0][2] = new Map.BeltInformation(Direction.NORTH, true, 1, Direction.NORTH);
        host.map.beltLayer[0][3] = new Map.BeltInformation(Direction.NORTH, false, 0);

        host.endOfTurn();
        assertEquals(1, player.getY());
        assertEquals(Direction.NORTH, player.getDirection());
        host.endOfTurn();
        assertEquals(3, player.getY());
        assertEquals(Direction.EAST, player.getDirection());

        PlayerToken player1 = new PlayerToken();
        player1.position = new GridPoint2(0, 4);
        host.clientPlayers.put(1, player1);
        host.map.loadPlayers(host.wrapper().PlayerTokens);
        host.endOfTurn();
        assertEquals(3, player.getY());
        assertEquals(4, player1.getY());
    }

    @Test
    public void playerVisitsFlags(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        host.map.flagList = new ArrayList<>();
        host.map.flagList.add(new Flag(0, 1));
        host.map.flagList.add(new Flag(0, 2));
        host.resolveCard(makeCard(CardType.FORWARDONE), player);
        //This is exactly what resetPlayerTokens does
        host.map.registerFlags(host.wrapper().PlayerTokens);
        assertEquals(1, player.getVisitedFlags().size());
        assertTrue(host.map.hasWon(host.wrapper().PlayerTokens) == null);
        host.resolveCard(makeCard(CardType.FORWARDONE), player);
        //This is exactly what resetPlayerTokens does
        host.map.registerFlags(host.wrapper().PlayerTokens);
        assertTrue(host.map.hasWon(host.wrapper().PlayerTokens) == player);

    }


}
