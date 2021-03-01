package inf112.skeleton.app.game.objects;

import com.badlogic.gdx.math.GridPoint2;

import java.util.*;

public class PlayerToken {


    // Win/Lose state of player
    public boolean isWinner = false;
    // Player position initialized at 0, 0
    private final GridPoint2 position = new GridPoint2(0, 0);
    // All flags visited
    private final List<Flag> flagsVisited = new ArrayList<>();

    //Current direction of player
    private Direction playerDirection;

    public void move(int x, int y) {
        position.x += x;
        position.y += y;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    /**
     * @param flag flag to visist
     * @return true if flag hasn't been visisted before
     */
    public boolean visitFlag(Flag flag) {
        if (!flagsVisited.contains(flag)) {
            flagsVisited.add(flag);
            return true;
        }
        return false;
    }

    public List<Flag> getVisitedFlags() {
        return flagsVisited;
    }


    public Direction getDirection() {
        return playerDirection;
    }

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
