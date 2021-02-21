package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GameServerTest {

    @Test
    public void isGameServerTest() {
        GameServer gameServer = new GameServer();

        assertTrue(gameServer.isHost);


    }


}
