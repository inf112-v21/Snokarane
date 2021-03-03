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
    //TODO: THis is just a random default. Maybe it should depend on something
    private Direction playerDirection = Direction.NORTH;

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

    /**
     *
     * @return List of all the flags the player has visited
     */
    public List<Flag> getVisitedFlags() {
        return flagsVisited;
    }

    /**
     * @return The direction the player is facing
     */
    public Direction getDirection() {
        return playerDirection;
    }

    /**
     * @return The position where the player would end up if they move one step in the direction
     * they are currently facing.
     */
    public GridPoint2 wouldEndUp() {
        GridPoint2 newPos = new GridPoint2(position.x, position.y);
        moveDir(newPos, playerDirection);
        return newPos;
    }

    /**
     * Updates the position matrix to reflect on what it would be like if a player
     * moved one step in the given direction
     * @param position The position to update
     * @param direction The direction to move
     */
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

    /**
     * Rotates the player in the given direction
     * @param rotationDirection A CardType that is either TURNLEFT, TURNRIGHT, or UTURN
     */
    public void rotate(CardType rotationDirection) {
        switch (playerDirection) {
            case EAST:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.NORTH;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.SOUTH;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.WEST;
            case WEST:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.SOUTH;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.NORTH;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.EAST;
            case NORTH:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.WEST;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.EAST;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.SOUTH;
            case SOUTH:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.EAST;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.WEST;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.NORTH;
        }
    }

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
