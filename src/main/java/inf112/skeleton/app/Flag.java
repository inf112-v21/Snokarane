package inf112.skeleton.app;

import com.badlogic.gdx.math.GridPoint2;

public class Flag {
    private GridPoint2 position;

    public Flag(int x, int y){
        position = new GridPoint2(x, y);
    }

    public int getX(){
        return position.x;
    }
    public int getY(){
        return position.y;
    }
}
