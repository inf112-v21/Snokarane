package inf112.skeleton.app.game;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.Direction;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import org.junit.Before;
import org.junit.Test;
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
    }

    private Card makeCard(CardType type) {
        Card card = new Card();
        card.setCardType(type);
        return card;
    }

    @Test
    public void RotateCardsRotatesPlayerCorrectly(){
        assertTrue(player.getX() == 0 && player.getY() == 0);
        assertTrue(player.getDirection() == Direction.NORTH);
        host.resolveCard(makeCard(CardType.TURNLEFT), player);
        assertTrue(player.getDirection() == Direction.WEST);
        host.resolveCard(makeCard(CardType.UTURN), player);
        assertTrue(player.getDirection() == Direction.EAST);
        host.resolveCard(makeCard(CardType.TURNRIGHT), player);
        assertTrue(player.getDirection() == Direction.SOUTH);
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
        host.clientPlayers.put(0, player);
        host.clientPlayers.put(1, player1);
        host.map.loadPlayers(host.wrapper().PlayerTokens);
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertEquals(3, player.getY());
        assertEquals(4, player1.getY());

        //Wall should block
        host.resolveCard(makeCard(CardType.FORWARDTHREE), player);
        assertEquals(4, player.getY());
        assertEquals(5, player1.getY());
    }


}
