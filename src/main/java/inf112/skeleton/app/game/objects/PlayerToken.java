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

    /**
     * Moves the player 1 step in the direction it is facing
     */
    public void move() {
        moveDir(position, playerDirection);
    }

    /**
     * Use this method to move a player when the player is being pushed by something.
     * @param direction The direction you want to move, regardless of the players direction
     */
    public void move(Direction direction) {
        moveDir(position, direction);
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

    public GridPoint2 wouldEndUp() {
        GridPoint2 newPos = new GridPoint2(position.x, position.y);
        moveDir(newPos, playerDirection);
        return newPos;
    }

    private void moveDir(GridPoint2 position, Direction direction) {
        switch (direction) {
            case NORTH:
                position.x +=1;
            case SOUTH:
                position.x -=1;
            case WEST:
                position.y -= 1;
            case EAST:
                position.y += 1;
        }
    }

    public void rotate(CardType rotationDirection) {
        switch (playerDirection) {
            case EAST:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.NORTH;
                if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.SOUTH;
                if (rotationDirection == CardType.UTURN) playerDirection = Direction.WEST;
            case WEST:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.SOUTH;
                if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.NORTH;
                if (rotationDirection == CardType.UTURN) playerDirection = Direction.EAST;
            case NORTH:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.WEST;
                if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.EAST;
                if (rotationDirection == CardType.UTURN) playerDirection = Direction.SOUTH;
            case SOUTH:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.EAST;
                if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.WEST;
                if (rotationDirection == CardType.UTURN) playerDirection = Direction.NORTH;
        }
    }

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
