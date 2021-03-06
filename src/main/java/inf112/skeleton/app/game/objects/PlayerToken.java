package inf112.skeleton.app.game.objects;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.libgdx.Game;

import java.util.*;

public class PlayerToken {

    // Player position initialized at 0, 0
    public final GridPoint2 position = new GridPoint2(0, 0);
    // All flags visited
    private final List<Flag> flagsVisited = new ArrayList<>();

    public CHARACTER_STATES charState = CHARACTER_STATES.NONE;

    public int ID;

    public int hp = 3;

    public boolean diedThisTurn = false;

    public String name;


    // What texture gets displayed on character
    public enum CHARACTER_STATES{
        PLAYERNORMAL,
        PLAYERWON,
        PLAYERSELFNORMAL,
        PLAYERSELFWON,
        NONE
    }

    //Current direction of player
    private Direction playerDirection = Direction.NORTH;

    /**
     * Use this method to move a player when the player is being pushed by something.
     * @param direction The direction you want to move, regardless of the players direction
     */
    public void move(Direction direction) {
        GridPoint2 wouldEndUpDirection = wouldEndUp(direction);
        if (wouldEndUpDirection.x < 0 || wouldEndUpDirection.x >= Game.BOARD_X || wouldEndUpDirection.y < 0 || wouldEndUpDirection.y >= Game.BOARD_Y){
            died();
        }
        else {
            moveDir(position, direction);
        }
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public void died() {
        position.x = 0;
        position.y = 0;
        diedThisTurn = true;
        hp --;
        playerDirection = Direction.NORTH;
     }


    public boolean isDead(){
        return hp < 1;
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
    public GridPoint2 wouldEndUp(Direction direction) {
        GridPoint2 newPos = new GridPoint2(position.x, position.y);
        moveDir(newPos, direction);
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
                position.y +=1;
                break;
            case SOUTH:
                position.y -=1;
                break;
            case WEST:
                position.x -= 1;
                break;
            case EAST:
                position.x += 1;
                break;
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
                break;
            case WEST:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.SOUTH;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.NORTH;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.EAST;
                break;
            case NORTH:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.WEST;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.EAST;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.SOUTH;
                break;
            case SOUTH:
                if (rotationDirection == CardType.TURNLEFT) playerDirection = Direction.EAST;
                else if (rotationDirection == CardType.TURNRIGHT) playerDirection = Direction.WEST;
                else if (rotationDirection == CardType.UTURN) playerDirection = Direction.NORTH;
                break;
        }
    }

    public enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
