package inf112.skeleton.app;

import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NetworkClientTest {

    Network GameClient;

    @Before
    public void setUp() {
        GameClient = new NetworkClient();
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
