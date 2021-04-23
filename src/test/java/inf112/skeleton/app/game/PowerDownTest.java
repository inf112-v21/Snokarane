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

public class PowerDownTest {
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
    public void powerDownHeals() {
        player.powerDown = true;
        player.damage = 5;
        host.endOfTurn();
        assertEquals(0, player.damage);
    }
}
