package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.Direction;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains information about the map that is to be sent to all clients
 * This exists so we can nicely package all information about what
 * is place on the map to every client on the network.
 */
public class Map {

    private final int BOARD_X = Game.BOARD_X;
    private final int BOARD_Y = Game.BOARD_Y;
    private int ID;
    public List<Flag> flagList;

    /**
     * 2D map like structure contain information about all players in the game.
     */
    public PlayerRenderInformation [][] playerLayer = new PlayerRenderInformation [BOARD_X][BOARD_Y];
    public BeltInformation [][] beltLayer = new BeltInformation[BOARD_X][BOARD_Y];
    public boolean [][] holeLayer = new boolean[BOARD_X][BOARD_Y];
    public boolean [][][] wallLayer = new boolean[BOARD_X][BOARD_Y][4];
    public int [][] gearLayer = new int[BOARD_X][BOARD_Y];
    public boolean [][] repairLayer = new boolean[BOARD_X][BOARD_Y];
    public int[][][] laserLayer = new int [BOARD_X][BOARD_Y][4];
    public List<LaserShooter> laserShooters = new ArrayList<>();




    //TODO blir bare mellomlagret her, kanskje en dårlig ide?
    public List<GridPoint2> spawnPoints = new ArrayList<>();

    /**
     * Keeps track of all the information about our laser shooters
     */
    public static class LaserShooter{
        public Direction dir;
        int laserNum;
        int x;
        int y;
        boolean isPermanent;

        public LaserShooter(Direction dir, int laserNum, int x, int y, boolean isPermanent) {
            this.dir = dir;
            this.laserNum = laserNum;
            this.x = x;
            this.y = y;
            this.isPermanent = isPermanent;
        }
    }
    /**
     * This is a sort of replacement for tuples that java lack,
     * Only thing clients needs to render players is state and direction so this is wrapped
     * into this class.
     */
    public static class PlayerRenderInformation{
        public PlayerToken.CHARACTER_STATES state = PlayerToken.CHARACTER_STATES.NONE;
        public Direction dir = Direction.NORTH;
        public PlayerConfig config;
        public PlayerRenderInformation(){}
    }

    /**
     * Keeps track of all the information needed about a belt
     */
    public static class BeltInformation{
        //Which direction you need to enter the belt on in order to rotate
        public Direction beltRotationDirection = null;
        //Which direction the belt rotates the player, 0, 1, or -1
        public Direction beltDirection;
        public boolean isExpress;
        public int beltRotation;
        public BeltInformation(Direction direction, boolean isExpress, int beltRotation){
            beltDirection = direction;
            this.isExpress = isExpress;
            this.beltRotation = beltRotation;
        }
        public BeltInformation(Direction direction, boolean isExpress, int beltRotation, Direction beltRotationDirection){
            beltDirection = direction;
            this.isExpress = isExpress;
            this.beltRotation = beltRotation;
            this.beltRotationDirection = beltRotationDirection;
        }
    }

    // NOTE! No args constructor required so kryonet can serialize
    public Map(){
        clearPlayerLayer();
    }

    // Set map ID
    public void setID(int ID) {
        this.ID = ID;
    }

    // Sets entire player layer to default objects
    public void clearPlayerLayer() {
        for (int i = 0; i<BOARD_X; i++){
            for (int j = 0; j<BOARD_Y; j++){
                playerLayer[i][j] = new PlayerRenderInformation();
            }
        }
    }

    public void registerFlags(List<PlayerToken> players) {
        for (PlayerToken player : players) {
            for (Flag flag : flagList) {
                if (player.getX() == flag.getX() && player.getY() == flag.getY()) {
                    player.visitFlag(flag);
                }
            }
        }
    }

    /**
     *
     * @param x X position of the player
     * @param y y position of the player
     * @return true if the player would die making that move, false otherwise
     */
    public boolean wouldDie(int x, int y) {
        return (!isInBounds(x, y) || isHole(x, y));
    }

    /**
     *
     * @param x X position of the player
     * @param y y position of the player
     * @return true if the tile has a player, false otherwise
     */
    public boolean hasPlayer(int x, int y) {
        if (!isInBounds(x, y)) return false;
        return playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE;
    }

    /**
     * Checks if any players have won the current game. If multiple players win
     * only the first one is returned
     * @param players All the players playing the game
     * @return The first person in the list who has won.
     */
    public PlayerToken hasWon(List<PlayerToken> players) {
        for (PlayerToken player : players) {
            if (player.getVisitedFlags().size() == flagList.size())
                return player;
        }
        return null;
    }
    /**
     *
     * @param x X position of the tile
     * @param y y position of the tile
     * @return true if the tile has a hole, false otherwise
     */
    public boolean isHole(int x, int y) {
        return holeLayer[x][y];
    }
    /**
     *
     * @param x X position of the tile
     * @param y y position of the tile
     * @return true if the tile has a gear, false otherwise
     */
    public int isGear(int x, int y) {
        return gearLayer[x][y];
    }
    /**
     *
     * @param x X position of the tile
     * @param y y position of the tile
     * @return true if the tile has a wrench, false otherwise
     */
    public boolean isRepair(int x, int y){
        return repairLayer[x][y];
    }

    /**
     *
     * @param x x position of the tile
     * @param y y position of the tile
     * @param direction the direction you want to check
     * @return Returns true if it is possible to move to the next tile, i.e there are no walls, and false otherwise
     */
    public boolean canGo(int x, int y, Direction direction){
        if (direction == Direction.NORTH){
            //If the next one is out of bounds, we won't disallow that!
            if (!isInBounds(x, y, direction) && !wallLayer[x][y][0]) return true;
            //Check that the next one is in bounds, and if so, that there's no wall on either tile
            return isInBounds(x, y, direction) && !wallLayer[x][y][0] && !wallLayer[x][y+1][2];
        }
        else if (direction == Direction.EAST) {
            if (!isInBounds(x, y, direction) && !wallLayer[x][y][1]) return true;
            return isInBounds(x, y, direction) && !wallLayer[x][y][1] && !wallLayer[x+1][y][3];
        }
        else if (direction == Direction.SOUTH) {
            if (!isInBounds(x, y, direction) && !wallLayer[x][y][2]) return true;
            return isInBounds(x, y, direction) && !wallLayer[x][y][2] && !wallLayer[x][y - 1][0];
        }
        else{
            if (!isInBounds(x, y, direction) && !wallLayer[x][y][3]) return true;
            return isInBounds(x, y, direction) && !wallLayer[x][y][3] && !wallLayer[x-1][y][1];
        }
    }
    //TODO add support for permamente lasers?

    /**
     * Clears all the lasers on the map for a new round
     */
    public void clearLasers() {
        laserLayer = new int [BOARD_X][BOARD_Y][4];
    }

    /**
     * Makes all permanent laser shooters as well as players shoot their lasers. For players, the lasers
     * start one tile ahead. The laser only stops at walls, players, or out of bounds.
     * @param wrapper a NetworkDataWrapper containing all the players. //TODO make this a list
     */
    public void shootLasers(NetworkDataWrapper wrapper) {
        List<LaserShooter> allLasers = new ArrayList<>(laserShooters);

        for (int i = 0; i<wrapper.PlayerTokens.size(); i++) {
            PlayerToken token = wrapper.PlayerTokens.get(i);
            //TODO as of now, dead people don't shoot lasers
            if (token.diedThisTurn) continue;
            System.out.println(token.name + " is dead? " + token.diedThisTurn);
            if (token.diedThisTurn) {
                continue;
            }
            allLasers.add(new LaserShooter(token.getDirection(), 1, token.getX(), token.getY(), false));
        }

        for (LaserShooter laser : allLasers) {
            int x = laser.x;
            int y = laser.y;
            //If the laser comes from a player, it starts one tile ahead
            int start = !laser.isPermanent ? 1 : 0;

            //If there is a wall directly in front of a player, or it's immediately out of bounds
            if (!laser.isPermanent && (!canGo(x, y, laser.dir) || !isInBounds(x, y, laser.dir))) continue;

            if (laser.dir == Direction.NORTH){
                for (int i = start; i < BOARD_X; i++) {
                    laserLayer[x][y+i][0] = laser.laserNum;
                    if (!isInBounds(x, y+i+1) || !canGo(x, y+i, laser.dir) || hasPlayer(x, y + i)) break;
                }
            }
            else if (laser.dir == Direction.EAST) {
                for (int i = start; i < BOARD_Y; i++) {
                    laserLayer[x+i][y][1] = laser.laserNum;
                    if (!isInBounds(x+i+1, y) || !canGo(x+i, y, laser.dir) || hasPlayer(x+i, y)) break;
                }
            }
            else if (laser.dir == Direction.SOUTH) {
                for (int i = start; i < BOARD_X; i++) {
                    laserLayer[x][y-i][2] = laser.laserNum;
                    if (!isInBounds(x, y-i-1) || !canGo(x, y-i, laser.dir) || hasPlayer(x, y-i)) break;
                }
            }
            else{
                for (int i = start; i < BOARD_Y; i++) {
                    laserLayer[x-i][y][3] = laser.laserNum;
                    if (!isInBounds(x-i-1, y) || !canGo(x-i, y, laser.dir) || hasPlayer(x- i, y)) break;
                }
            }
        }
    }

    /**
     * Loads player from network into map
     * @param players The list that contains the players
     */
    public void loadPlayers(List<PlayerToken> players) {
        clearPlayerLayer();
        for (PlayerToken token: players) {
            if (token.diedThisTurn) continue;
            if (token.ID == this.ID) {
                playerLayer[token.getX()][token.getY()].state = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
            } else {
                playerLayer[token.getX()][token.getY()].state = token.charState;
            }
            playerLayer[token.getX()][token.getY()].config = token.getConfig();
            playerLayer[token.getX()][token.getY()].dir = token.getDirection();
        }
    }

    /**
     * @param x X position of the tile
     * @param y Y position of the tile
     * @return true if the tile is on the map, false otherwise
     */
    public static boolean isInBounds(int x, int y) {
        return !(x < 0 || x >= Game.BOARD_X || y < 0 || y >= Game.BOARD_Y);
    }

    /**
     *
     * @param x the x position of the tile
     * @param y the y position of the tile
     * @param dir the direction you wish to check if is in bounds
     * @return true if the tile in the direction of the x and y is in bounds, false otherwise
     */
    public static boolean isInBounds(int x, int y, Direction dir) {
        if (dir == Direction.NORTH)
            return !(x < 0 || x >= Game.BOARD_X || y+1 < 0 || y+1 >= Game.BOARD_Y);
        if (dir == Direction.SOUTH)
            return !(x < 0 || x >= Game.BOARD_X || y-1 < 0 || y-1 >= Game.BOARD_Y);
        if (dir == Direction.EAST)
            return !(x+1 < 0 || x+1 >= Game.BOARD_X || y < 0 || y >= Game.BOARD_Y);
        else
            return !(x-1 < 0 || x-1 >= Game.BOARD_X || y < 0 || y >= Game.BOARD_Y);
    }

}
