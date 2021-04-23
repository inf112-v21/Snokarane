package inf112.skeleton.app.game;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.Direction;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.network.NetworkHost;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DamageCounterTest {

    GameHost host;
    PlayerToken player;

    @Before
    public void init() {
        NetworkHost network = new NetworkHost();
        host = new GameHost(network);
        host.map = new Map();
        player = new PlayerToken();
        player.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
        host.clientPlayers.put(0, player);
    }

    @Test
    public void PlayersDrawCorrectAmountOfCards() {
        hasCorrectAmountOfCards();
        for (int i = 0; i < 10; i++) {
            player.damage = i;
            hasCorrectAmountOfCards();
        }
    }

    @Test
    public void PlayersTakeDamage() {
        //As of this version, only lasers damage players
        assertEquals(0, player.damage);
        host.map.laserShooters.add(new Map.LaserShooter(Direction.SOUTH, 2, 0, 1, true));
        host.endOfTurn();
        assertEquals(2, player.damage);
    }

    @Test
    public void DyingClearsDamageAndTenDamageIsDeadly() {
        player.damage = 10;
        //Move the player out of the way from the laser
        player.position = new GridPoint2(2, 2);
        host.endOfTurn();
        assertTrue(player.diedThisTurn);
        assertEquals(0, player.damage);
    }

    public void hasCorrectAmountOfCards() {
        host.hand = new ArrayList<>();
        host.damageCounters = player.damage;
        host.drawCardsFromDeck();
        assertEquals(host.hand.size(), 9-player.damage);
    }
}
