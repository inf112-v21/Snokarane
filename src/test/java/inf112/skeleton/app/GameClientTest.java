package inf112.skeleton.app;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameClientTest {

    GameClient GameClient;

    @Before
    public void setUp() {
        GameClient = new GameClient();
    }

    @Test
    public void isGameClientTest() {
        assertFalse(GameClient.isHost);
    }

    @Test
    public void failToConnectTest() {
        assertFalse(GameClient.initialize());
    }

}
