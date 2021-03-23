package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
    public int laserLayer [][][] = new int [BOARD_X][BOARD_Y][4];
    public List<LaserShooter> laserShooters = new ArrayList<>();




    //TODO blir bare mellomlagret her, kanskje en dårlig ide?
    public List<GridPoint2> spawnPoints = new ArrayList<>();


    public static class LaserShooter{
        public PlayerToken.Direction dir;
        int laserNum;
        int x;
        int y;

        public LaserShooter(PlayerToken.Direction dir, int laserNum, int x, int y) {
            this.dir = dir;
            this.laserNum = laserNum;
            this.x = x;
            this.y = y;
        }
    }
    /**
     * This is a sort of replacement for tuples that java lack,
     * Only thing clients needs to render players is state and direction so this is wrapped
     * into this class.
     */
    public static class PlayerRenderInformation{
        public PlayerToken.CHARACTER_STATES state = PlayerToken.CHARACTER_STATES.NONE;
        public PlayerToken.Direction dir = PlayerToken.Direction.NORTH;
        public PlayerRenderInformation(){}
    }

    public static class BeltInformation{
        public PlayerToken.Direction beltRotationDirection = null;
        public PlayerToken.Direction beltDirection;
        public boolean isExpress;
        public int beltRotation;
        public BeltInformation(PlayerToken.Direction direction, boolean isExpress, int beltRotation){
            beltDirection = direction;
            this.isExpress = isExpress;
            this.beltRotation = beltRotation;
        }
        public BeltInformation(PlayerToken.Direction direction, boolean isExpress, int beltRotation, PlayerToken.Direction beltRotationDirection){
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

    public boolean isHole(int x, int y) {
        return holeLayer[x][y];
    }

    public int isGear(int x, int y) {
        return gearLayer[x][y];
    }

    public boolean isRepair(int x, int y){
        return repairLayer [x][y];
    }

    public int isLaser(int x, int y, PlayerToken.Direction direction) {
        if (direction == PlayerToken.Direction.NORTH){
            return laserLayer[x][y][0];
        }
        else if (direction == PlayerToken.Direction.EAST) {
            return laserLayer[x][y][1];
        }
        else if (direction == PlayerToken.Direction.SOUTH) {
            return laserLayer[x][y][2];
        }
        else{
            return laserLayer[x][y][3];
        }
    }

    public boolean isWall(int x, int y, PlayerToken.Direction direction){
        if (direction == PlayerToken.Direction.NORTH){
            return wallLayer[x][y][0];
        }
        else if (direction == PlayerToken.Direction.EAST) {
            return wallLayer[x][y][1];
        }
        else if (direction == PlayerToken.Direction.SOUTH) {
            return wallLayer[x][y][2];
        }
        else{
            return wallLayer[x][y][3];
        }
    }
    public void clearLasers() {
        laserLayer = new int [BOARD_X][BOARD_Y][4];
    }

    public void shootLasers(NetworkDataWrapper wrapper) {
        List<LaserShooter> allLasers = new ArrayList<>();
        allLasers.addAll(laserShooters);

        for (int i = 0; i<wrapper.PlayerTokens.size(); i++) {
            PlayerToken token = wrapper.PlayerTokens.get(i);
            allLasers.add(new LaserShooter(token.getDirection(), 1, token.getX(), token.getY()));
        }
        for (LaserShooter laser : allLasers) {
            int x = laser.x;
            int y = laser.y;
            if (laser.dir == PlayerToken.Direction.NORTH){
                for (int i = 0; i < BOARD_X; i++) {
                    laserLayer[x][y+i][0] = laser.laserNum;
                    if (isWall(x, y, laser.dir) || playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE || y+i+1 == BOARD_Y) break;
                }
            }
            else if (laser.dir == PlayerToken.Direction.EAST) {
                for (int i = 0; i < BOARD_X; i++) {
                    laserLayer[x+i][y][1] = laser.laserNum;
                    if (isWall(x, y, laser.dir) || playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE || x+i+1 == BOARD_X) break;
                }
            }
            else if (laser.dir == PlayerToken.Direction.SOUTH) {
                for (int i = 0; i < BOARD_X; i++) {
                    laserLayer[x][y-i][2] = laser.laserNum;
                    if (isWall(x, y, laser.dir) || playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE || y-i-1 < 0) break;
                }
            }
            else{
                for (int i = 0; i < BOARD_X; i++) {
                    laserLayer[x-i][y][3] = laser.laserNum;
                    if (isWall(x, y, laser.dir) || playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE || x-i-1 < 0) break;
                }
            }
        }
    }

    /**
     * //TODO RENAME THIS
     * Loads player from network into map
     * @param wrapper The NetworkDataWrapper that contains the players
     */
    public void loadPlayers(NetworkDataWrapper wrapper) {
        clearPlayerLayer();
        for (int i = 0; i<wrapper.PlayerTokens.size(); i++){
            PlayerToken token = wrapper.PlayerTokens.get(i);
            if (token.ID == this.ID) {
                playerLayer[token.getX()][token.getY()].state = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
            }
            else {
                playerLayer[token.getX()][token.getY()].state = token.charState;
            }
            playerLayer[token.getX()][token.getY()].dir = token.getDirection();
        }
        shootLasers(wrapper);
    }
}
