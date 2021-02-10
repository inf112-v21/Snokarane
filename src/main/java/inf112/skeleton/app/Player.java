package inf112.skeleton.app;

import com.badlogic.gdx.math.GridPoint2;

public class Player {

    // Player position initialized at 0, 0
    private GridPoint2 position = new GridPoint2(0, 0);

    public void move(int x, int y) {
        position.x+=x;
        position.y+=y;
    }

    public int getX() { return position.x; }
    public int getY() { return position.y; }

}
