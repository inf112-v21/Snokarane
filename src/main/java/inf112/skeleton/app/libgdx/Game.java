package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles rendering, textures and event handling (key presses)
 * Game(this class) runs client side on each players PC, so only objects and methods necessary to have client side should be used here
 */
public class Game extends InputAdapter implements ApplicationListener {
    // For rendering text to screen
    private SpriteBatch batch;
    private BitmapFont font;

    // Entire map (graphic)
    private TiledMap tiledMap;

    Map mlp;

    // Board dimensions
    public static int BOARD_X = 5;
    public static int BOARD_Y = 5;

    // Layers of the map
    private TiledMapTileLayer boardLayer;
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer flagLayer;

    // Flags on the map are stored here for easy access
    // TODO: this should really only useful in GameHost
    List<Flag> flagPositions = new ArrayList<>();

    // Cells for each player state
    private TiledMapTileLayer.Cell playerNormal;
    private TiledMapTileLayer.Cell playerWon;

    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    /**
     * Client objects
     */
    // Handles all player based actions (picking cards, decks to send over network etc.)
    GamePlayer gamePlayer;
    // Handles all data transfers over internet
    Network network;

    /**
     * Initialize objects depending on host status
     * These methods are needed to start a game session to other players over network
     */
    // Function called regardless of host or player status, initializes network and asks for host/client role selection
    public void startGame(){
        // Initialize mapLayerWrapper
        mlp = new Map();
        // Set MapLayerWrappers player state cells
        mlp.setPlayerCells(playerNormal, playerWon);
        // Load map layers into wrapper
        mlp.loadLayers(boardLayer, playerLayer, flagLayer);

        // Choose whether to host or connect
        network = Network.choseRole();
        // Initializes connections, ports and opens for sending and receiving data
        this.network.initialize();

        if (network.isHost)
            startHost();
        else
            startClient();

    }
    // Start game as host
    private void startHost(){
        // Send prompt til all connected clients
        Network.prompt("All players connected.", null);
        // Start connection to current clients. This is to be able to accept data transfers from clients
        this.network.initConnections();
        // Starts GameHost session using network that was initialized
        gamePlayer = new GameHost((NetworkHost)network);
        gamePlayer.getMap(mlp);
        gamePlayer.drawCards();
    }
    // Start game as client
    private void startClient(){
        gamePlayer = new GameClient((NetworkClient)network);
        gamePlayer.getMap(mlp);
    }

    /**
     * Initialize all libgdx objects:
     *  Batch, font, input processor, textures, map layers, camera and renderer,
     * and Fetch flags from flag layer
     *
     * This function is called on libgdx startup
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        
        // Register input processor to handle key presses
        Gdx.input.setInputProcessor(this);

        // Load .tmx file from disk
        tiledMap = loadTileMapFromFile("test-map.tmx");

        // Load the map's layers
        loadMapLayers(tiledMap);

        // Initialize player textures from .png file
        loadPlayerTextures();

        // Start game/network objects
        startGame();

        // Start camera/rendering
        initializeRendering();
    }

    /**
     * @return TiledMap object loaded from path
     * @param path path to .tmx file for map
     */
    public TiledMap loadTileMapFromFile(String path){
        return new TmxMapLoader().load(path);
    }

    /**
     * Load player texture and split into each player state
     */
    public void loadPlayerTextures(){
        // Load the entire player texture
        Texture rawPlayerTexture = new Texture("player.png");

        // Split player texture into seperate regions
        TextureRegion[][] splitTextures = TextureRegion.split(rawPlayerTexture,300, 300);

        // Put the texture regions into seperate tiles
        StaticTiledMapTile playerNormalStaticTile = new StaticTiledMapTile(splitTextures[0][0]);
        StaticTiledMapTile playerWonStaticTile = new StaticTiledMapTile(splitTextures[0][2]);

        // Set player state cells to corresponding tiles
        playerNormal = new TiledMapTileLayer.Cell().setTile(playerNormalStaticTile);
        playerWon = new TiledMapTileLayer.Cell().setTile(playerWonStaticTile);
    }

    /**
     * Initialize camera and renderer, sets view/renderer to tiledMap
     */
    private void initializeRendering(){
        // Initialize camera object
        camera = new OrthographicCamera();
        // Set camera to orthographic, size board dimensions
        camera.setToOrtho(false, BOARD_X, BOARD_Y);
        // Set camera X-position
        camera.position.x = 2.5F;
        camera.update();

        // Initialize renderer                                  v--- 300F is tile size
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1F/300F);
        // Set renderer to view camera
        renderer.setView(camera);
    }

    /**
     * This function is called by libgdx when a key is released.
     *
     * @return true if keyrelease was handled
     */
    @Override
    public boolean keyUp (int keyCode){
        if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS){
            if(keyCode >= Input.Keys.NUM_1 && keyCode <= Input.Keys.NUM_9){
                return pickCardsOnKeyPress(keyCode);
            }
        }
        return false;
    }

    /**
     * Helper function for keyUp to pick cards for player
     *
     * @param keyCode key pressed
     * @return if key was registered as correct and acted on
     */
    private boolean pickCardsOnKeyPress(int keyCode){
        gamePlayer.chooseCards(keyCode-7); // Input.Keys.Num_1 starts at 8
        if(gamePlayer.chosenCards.size() >= 5){
            gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
            gamePlayer.registerChosenCards();
            return true;
        }
        return false;
    }
    /**
     * Render all objects and text to the screen
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // Sends map to client of host, updates map in (this) if client
        updateMap();

        // Render current frame to screen
        renderer.render();
    }

    public void updateMap(){
        if (mlp != null){
            mlp = gamePlayer.updateMap(null);

            //TODO Check if this is correct
            if (mlp == null) return;

            this.playerLayer = mlp.getPlayerLayer();
            this.boardLayer = mlp.getBoardLayer();
            this.flagLayer = mlp.getFlagLayer();
        }
    }

    /**
     * Load all map layers into their own member variable
     */
    public void loadMapLayers(TiledMap tiledMap){
        // Separate each layer from the tiledMap
        boardLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Board");
        playerLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Player");
        flagLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Flag");

        // Sneakily yoink the positions of the flags here, don't tell the OOP police
        getFlagPositionsFromLayer(flagLayer);
    }
    /**
     * Get all flag positions in layer flag layer
     */
    private void getFlagPositionsFromLayer(TiledMapTileLayer flagLayer){
        List<Flag> flags = new ArrayList<>();

        for (int i = 0; i <= flagLayer.getWidth(); i++){
            for (int j = 0; j <= flagLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                if (flagLayer.getCell(i, j) != null){
                    flags.add(new Flag(i, j));
                }
            }
        }
        flagPositions.addAll(flags);
    }

    /**
     * These functions are not currently in use, but inherited from superclass
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
