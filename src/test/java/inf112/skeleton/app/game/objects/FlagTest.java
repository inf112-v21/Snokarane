package inf112.skeleton.app.game.objects;

import inf112.skeleton.app.game.objects.Flag;
import org.junit.Test;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class FlagTest {


    @Test
    public void FlagIsInitializedAtZeroZero(){
        Flag flag = new Flag(0, 0);

        assertEquals(flag.getX(), 0);
        assertEquals(flag.getY(), 0);
    }

    @Test
    public void FlagIsInitializedAtZeroOne(){
        Flag flag = new Flag(0, 1);

        assertEquals(flag.getX(), 0);
        assertEquals(flag.getY(), 1);
    }

    @Test
    public void FlagIsInitializedAtOneZero(){
        Flag flag = new Flag(1, 0);

        assertEquals(flag.getX(), 1);
        assertEquals(flag.getY(), 0);
    }

    @Test
    public void FlagIsInitializedAtOneOne(){
        Flag flag = new Flag(1, 1);

        assertEquals(flag.getX(), 1);
        assertEquals(flag.getY(), 1);
    }

    @Test
    public void FlagIsInitializedAtFiveThree(){
        Flag flag = new Flag(5, 3);

        assertEquals(flag.getX(), 5);
        assertEquals(flag.getY(), 3);
    }


    @Test
    public void FlagIsInitializedAtCorrectPosition(){
        int randomX = ThreadLocalRandom.current().nextInt(0, 20 + 1);
        int randomY = ThreadLocalRandom.current().nextInt(0, 20 + 1);

        Flag flag = new Flag(randomX, randomY);

        assertEquals(flag.getX(), randomX);
        assertEquals(flag.getY(), randomY);
    }

}
