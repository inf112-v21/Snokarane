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
import inf112.skeleton.app.Map;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;

import java.util.List;

/**
 * Handles rendering, textures and event handling (key presses)
 * Currently this class also contains most of the game logic
 */
public class Game extends InputAdapter implements ApplicationListener {
    // For rendering text to screen
    private SpriteBatch batch;
    private BitmapFont font;

    // Entire map (graphic)
    private TiledMap tiledMap;

    int BOARD_X = 5;
    int BOARD_Y = 5;

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
     */
    public void startGame(){
        // Choose whether to host or connect
        network = this.network.choseRole();
        // Initializes connections, ports and opens for sending and receiving data
        this.network.initialize();

        if (network.isHost)
            startHost();
        else
            startClient();

    }

    private void startHost(){
        // Send prompt til all connected clients
        network.prompt("All players connected.", null);
        // Start connection to current clients. This is to be able to accept data transfers from clients
        this.network.initConnections();
        // Starts GameHost session using network that was initialized
        gamePlayer = new GameHost((NetworkHost)network);
        gamePlayer.drawCards();
    }
    private void startClient(){
        gamePlayer = new GameClient((NetworkClient)network);
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

        // Start game/network objects
        startGame();
        
        // Register input processor to handle key presses
        Gdx.input.setInputProcessor(this);

        // Load .tmx file from disk
        tiledMap = loadTileMapFromFile("test-map.tmx");

        // Initialize player textures from .png file
        loadPlayerTextures();

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
            // TODO: Draw cards from deck on all people
            while (!keyUp(keyCode)){
                gamePlayer.chooseCards(keyCode);
                if(gamePlayer.chosenCards.size() >= 5){
                    gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
                    gamePlayer.registerChosenCards();
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Render all objects and text to the screen
     */
    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);



        // Render current frame to screen
        renderer.render();
    }

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
