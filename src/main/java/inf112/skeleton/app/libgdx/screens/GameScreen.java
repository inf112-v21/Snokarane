package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    private RoboGame game;
    private Stage stage;

    Map map = new Map();

    // Layers of the map
    private TiledMapTileLayer playerLayer;

    // Flags on the map are stored here for easy access
    // TODO: this should really only useful in GameHost
    public List<Flag> flagPositions = new ArrayList<>();

    // Cells for each player state
    private TiledMapTileLayer.Cell playerNormal;
    private TiledMapTileLayer.Cell playerWon;

    /**
     * Client objects
     */
    // Handles all player based actions (picking cards, decks to send over network etc.)
    GamePlayer gamePlayer;
    // Handles all data transfers over internet
    Network network;

    public GameScreen(RoboGame game, boolean isHost, String ip, String playerName){
        this.game = game;
        stage = new Stage(new ScreenViewport());

        // Backwards capability for Game->GameScreen merge,
        // enables key press detection for testing other parts of game while cards haven't been implemented yet
        stage.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                GameScreen.this.keyUp(keycode);
                return true;
            }
        });

        Texture cardBackgroundTexture = new Texture(Gdx.files.internal("cards/cards-background.png"));
        Image cardBackground = new Image(cardBackgroundTexture);
        cardBackground.setPosition(0, 0);
        cardBackground.setSize(Gdx.graphics.getWidth(), 200);
        stage.addActor(cardBackground);

        create(isHost, ip, playerName);
    }
    /**
     * Initialize objects depending on host status
     * These methods are needed to start a game session to other players over network
     */
    // Function called regardless of host or player status, initializes network and asks for host/client role selection
    public void startGame(boolean isHost, String ip, String playerName){
        map.flagList = flagPositions;

        // Choose whether to host or connect
        network = Network.choseRole(isHost);
        // Initializes connections, ports and opens for sending and receiving data
        this.network.initialize();

        if (network.isHost)
            startHost(playerName);
        else
            startClient(ip, playerName);

    }
    // Start game as host
    private void startHost(String playerName){
        // Send prompt to all connected clients
        Network.prompt("All players connected.", null);
        // Start connection to current clients. This is to be able to accept data transfers from clients
        this.network.initConnections();
        // Starts GameHost session using network that was initialized
        gamePlayer = new GameHost((NetworkHost)network);
        gamePlayer.setMap(map);
        ((GameHost) gamePlayer).initializeHostPlayerToken(playerName);
        gamePlayer.drawCards();
    }
    // Start game as client
    private void startClient(String ip, String playerName){
        if (((NetworkClient) network).connectToServer(ip)){
            gamePlayer = new GameClient((NetworkClient)network, playerName);
            gamePlayer.setMap(map);
        } else {
            System.out.println("Failed to start client due to connection error.");
            System.exit(0);
        }
    }
    /**
     * Initialize all libgdx objects:
     *  Batch, font, input processor, textures, map layers, camera and renderer,
     * and Fetch flags from flag layer
     *
     * This function is called on libgdx startup
     */
    public void create(boolean isHost, String ip, String playerName) {

        // Load the map's layers
        loadMapLayers(game.tiledMap);

        // Initialize player textures from .png file
        loadPlayerTextures();

        // Start game/network objects
        startGame(isHost, ip, playerName);
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
     * This function is called by libgdx when a key is released.
     *
     * @return true if keyrelease was handled (per libgdx)
     */
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
    private boolean pickCardsOnKeyPress(int keyCode) {
        gamePlayer.chooseCards(keyCode-8); // Input.Keys.Num_1 starts at 8
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
    public void show(){
        Gdx.input.setInputProcessor(stage);
    }
    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        // Sends map to client of host, updates map in (this) if client
        updateMap();

        // Render current frame to screen
        game.renderer.render();

        // Draw current deck (has to be called after render to show correctly)
        drawDeck();
    }

    /**
     * Draw deck and selected cards on screen
     */
    private void drawDeck(){
        int baseX = 50;
        int baseY = 100; // Dont change me to something non divisible by two :)

        // Draw hand indicator
        game.batch.begin();
        game.font.setColor(255, 255, 255, 255);
        game.font.getData().setScale(2);
        game.font.draw(game.batch, "Hand:",baseX*5, baseY+50);
        game.font.getData().setScale(1);
        game.font.setColor(255, 255, 0, 255);

        // Initialize some helper variables
        int xPos = baseX;
        int yPos = baseY;
        int cardNum = 1;
        int lostCardsShown = 0;

        for (Card c : gamePlayer.hand){
            // If card was removed from hand and added to chosenCards, display them as green
            if (c == null) {
                game.font.setColor(0, 255, 0, 255);
                game.font.draw(game.batch, cardNum + ". " + gamePlayer.chosenCards.get(lostCardsShown).getCardType().toString(), xPos, yPos);
                game.font.setColor(255, 255, 0, 255);
                lostCardsShown++;
            }else {
                // Else display as yellow
                game.font.setColor(255, 255, 0, 255);
                game.font.draw(game.batch, cardNum +". " + c.getCardType().toString(), xPos, yPos);
                game.font.setColor(255, 255, 0, 255);
            }
            // Change positioning for next card
            cardNum++;
            xPos += 150;
            if (xPos >= 500){
                yPos -= 20;
                xPos = baseX;
            }
        }

        // Draw how many cards have been chosen so far
        game.font.draw(game.batch, "Deck:", baseX, baseY + baseY/4);

        if (lostCardsShown>=4){
            game.font.getData().setScale(1);
            game.font.setColor(0, 100, 200, 255);
            game.font.draw(game.batch, "Last card!", baseX, baseY + baseY/2);
        }

        game.font.setColor(255, 0, 0, 255);
        game.font.getData().setScale(2);
        game.font.draw(game.batch, Integer.toString(lostCardsShown),baseX*8, baseY+50);
        game.batch.end();
    }
    /**
     * Reset cell rotation on all cells in the map to 0
     */
    private void resetCellRotation(){
        for (int x = 0; x<playerLayer.getWidth(); x++){
            for (int y = 0; y<playerLayer.getHeight(); y++){
                TiledMapTileLayer.Cell cell = playerLayer.getCell(x, y);
                cell.setRotation(0);
                playerLayer.setCell(x, y, cell);
            }
        }
    }
    /**
     * Rotates cells according to location in map player layer directions
     */
    private void rotateCellsAccordingToDirection(){
        game.batch.begin();
        game.font.getData().setScale(1);
        for (int x = 0; x< map.playerLayer.length; x++){
            for (int y = 0; y< map.playerLayer[x].length; y++){
                if (map.playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE){
                    switch (map.playerLayer[x][y].dir){
                        case NORTH:
                            TiledMapTileLayer.Cell celln = playerLayer.getCell(x, y);
                            celln.setRotation(0);
                            playerLayer.setCell(x, y, celln);
                            break;
                        case EAST:
                            TiledMapTileLayer.Cell celle = playerLayer.getCell(x, y);
                            celle.setRotation(3);
                            playerLayer.setCell(x, y, celle);
                            break;
                        case SOUTH:
                            TiledMapTileLayer.Cell cells = playerLayer.getCell(x, y);
                            cells.setRotation(2);
                            playerLayer.setCell(x, y, cells);
                            break;
                        case WEST:
                            TiledMapTileLayer.Cell cellw = playerLayer.getCell(x, y);
                            cellw.setRotation(1);
                            playerLayer.setCell(x, y, cellw);
                            break;
                    }
                }
            }
        }
        game.batch.end();
    }
    /**
     * Query for map update in networks, and calls some methods to decode information from map sent over network
     */
    public void updateMap(){
        if (map != null){
            map = gamePlayer.updateMap(null);
            //
            if(network.isHost){
               if (((GameHost)gamePlayer).isShowingCards){
                   ((GameHost)gamePlayer).handleSingleCardRound();
                }
            }
            translatePlayerLayer();
            resetCellRotation();
            rotateCellsAccordingToDirection();
            // TODO: board and flag layer doesn't change as of this version
        }
    }
    /**
     * Gets player locations and states from map and sets tiledmaplayer cells to correct texture
     */
    public void translatePlayerLayer(){
        for (int x = 0; x< map.playerLayer.length; x++){
            for (int y = 0; y< map.playerLayer[x].length; y++){
                switch (map.playerLayer[x][y].state){
                    case PLAYERNORMAL:
                        playerLayer.setCell(x, y, playerNormal);
                        break;
                    case PLAYERWON:
                        playerLayer.setCell(x, y, playerWon);
                        break;
                    case PLAYERSELFNORMAL:
                        // todo we don't have a texture to show which player this player is, so using playerwon as filler
                        playerLayer.setCell(x, y, playerWon);
                        break;
                    case PLAYERSELFWON:
                        // todo
                        playerLayer.setCell(x, y, playerWon);
                        break;
                    case NONE:
                        // Clear cell if no players are found
                        playerLayer.setCell(x, y, new TiledMapTileLayer.Cell());
                        break;
                    default:
                        break;
                }
            }
        }
    }
    /**
     * Load all map layers into their own member variable
     */
    public void loadMapLayers(TiledMap tiledMap){
        // Separate each layer from the tiledMap
        playerLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Player");
        TiledMapTileLayer flagLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Flag");

        // Sneakily yoink the positions of the flags here, don't tell the OOP police
        getFlagPositionsFromLayer(flagLayer);
        getHolePositionsFromLayer((TiledMapTileLayer) tiledMap.getLayers().get("Hole"));
        getStartPositions((TiledMapTileLayer) tiledMap.getLayers().get("Spawn"));
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
    private void getStartPositions(TiledMapTileLayer startLayer) {
        for (int i = 0; i <= startLayer.getWidth(); i++){
            for (int j = 0; j <= startLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                if (startLayer.getCell(i, j) != null) {
                    map.spawnPoints.add(new GridPoint2(i, j));
                }
            }
        }
    }

    //TODO: FIX THIS TO MAKE IT MORE SEPARATED
    private void getHolePositionsFromLayer(TiledMapTileLayer holeLayer){
        for (int i = 0; i <= holeLayer.getWidth(); i++){
            for (int j = 0; j <= holeLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                if (holeLayer.getCell(i, j) != null) {
                    map.holeLayer[i][j] = true;
                }
            }
        }
    }
    /**
     * These functions are not currently in use, but inherited from superclass
     */
    @Override
    public void dispose() {
        game.batch.dispose();
        game.font.dispose();
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
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
