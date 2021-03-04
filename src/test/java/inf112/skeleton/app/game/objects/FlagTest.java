package inf112.skeleton.app.game.objects;

import inf112.skeleton.app.game.objects.Flag;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FlagTest {

    @Test
    public void FlagIsInitializedAtCorrectPosition(){
        Flag flag = new Flag(0, 1);

        assertEquals(flag.getX(), 0);
        assertEquals(flag.getY(), 1);
    }
}
