package inf112.skeleton.app;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MapTest {
    Map map;

    @Test
    //Amazing test
    public void boardIs5Times5() {
        map = new Map();
        assertEquals(5, map.getBoardX());
        assertEquals(5, map.getBoardY());
    }
}