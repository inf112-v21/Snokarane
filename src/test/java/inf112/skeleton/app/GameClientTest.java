package inf112.skeleton.app;

import org.junit.Test;

import static org.junit.Assert.*;

public class GameClientTest {


    @Test
    public void isGameClientTest(){
        GameClient GameClient = new GameClient();

        assertFalse(GameClient.isHost);


    }


}
