package inf112.skeleton.app;

import inf112.skeleton.app.network.NetworkHost;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NetworkHostTest {

    @Test
    public void isGameServerTest() {
        NetworkHost gameServer = new NetworkHost();

        assertTrue(gameServer.isHost);


    }


}
