package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    private RoboGame game;
    private Stage stage;

    Map map = new Map();

    // Layers of the map
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer holeLayer;

    // Flags on the map are stored here for easy access
    // TODO: this should really only useful in GameHost
    public List<Flag> flagPositions = new ArrayList<>();

    // Cells for each player state
    private TiledMapTileLayer.Cell playerNormal;
    private TiledMapTileLayer.Cell playerWon;

    /*
    * In order, index 0 to max is:
    * move 1, move 2, move 3, rotate left, rotate right, backup, uturn
    */
    private HashMap<CardType, Image> cardTemplates = new HashMap();
    // Duplicate card types currently in deck (for use in rendering)
    private HashMap<CardType, Integer> duplicates = new HashMap<>();

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
        gamePlayer = new GameHost((NetworkHost)network, playerName);
        gamePlayer.setMap(map);
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

        // Initialize card template textures
        loadCardImagesWithEvents();

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
     * Loads card images and adds event listeners.
     * --> Event listener only adds a card of the type pressed into gamePlayer's chosenCards
     * Adds cards into hashmap with corresponding card type
     */
    private void loadCardImagesWithEvents(){
        int cardW = 125;
        int cardH = 200;

        Texture allCards = new Texture("cards/programmingcards.png");

        TextureRegion[][] splitTextures = TextureRegion.split(allCards, 250, 400);

        Image f1 = new Image(splitTextures[0][0]);
        f1.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                f1.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.FORWARDONE);
                gamePlayer.chosenCards.add(c);
            }
        });

        Image f2 = new Image(splitTextures[0][1]);
        f2.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                f2.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.FORWARDTWO);
                gamePlayer.chosenCards.add(c);
            }
        });

        Image f3= new Image(splitTextures[0][2]);
        f3.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                f3.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.FORWARDTHREE);
                gamePlayer.chosenCards.add(c);
            }
        });

        Image tl= new Image(splitTextures[0][3]);
        tl.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                tl.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.TURNLEFT);
                gamePlayer.chosenCards.add(c);
            }
        });


        Image tr= new Image(splitTextures[0][4]);
        tr.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                tr.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.TURNRIGHT);
                gamePlayer.chosenCards.add(c);
            }
        });

        Image bu= new Image(splitTextures[0][5]);
        bu.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                bu.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.BACK_UP);
                gamePlayer.chosenCards.add(c);
            }
        });

        Image ut= new Image(splitTextures[0][6]);
        ut.addListener(new InputListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ut.setColor(1, 1, 1, 0.5f);
                Card c = new Card();
                c.setCardType(CardType.UTURN);
                gamePlayer.chosenCards.add(c);
            }
        });

        cardTemplates.put(CardType.FORWARDONE, f1);
        cardTemplates.put(CardType.FORWARDTWO, f2);
        cardTemplates.put(CardType.FORWARDTHREE, f3);
        cardTemplates.put(CardType.TURNLEFT, tl);
        cardTemplates.put(CardType.TURNRIGHT, tr);
        cardTemplates.put(CardType.BACK_UP, bu);
        cardTemplates.put(CardType.UTURN, ut);

        for (Image i : cardTemplates.values()){
            i.setSize(cardW, cardH);
        }
    }

    /**
     * Clear current stage cards and add new actors to stage
     */
    private void loadCardDeck(){
        int baseX = 20;
        int baseY = 0;

        int perCardIncrementX = 130;

        getDuplicateCards();

        for (CardType t : duplicates.keySet()){
            for (int i = 0; i<duplicates.get(t); i++){
                Image img = cardTemplates.get(t);
                img.setPosition(baseX, baseY);

                baseX += perCardIncrementX;
                stage.addActor(img);
            }
        }
    }

    /**
     * finds duplicate cards in deck
     * HashMap used because its O(n) instead of O(n^2)
      */
    private void getDuplicateCards(){
        if (!gamePlayer.hand.isEmpty()){
            for (Card c : gamePlayer.hand){
                if (!duplicates.containsKey(c.getCardType())){
                    duplicates.put(c.getCardType(), 1);
                } else {
                    duplicates.put(c.getCardType(), duplicates.get(c.getCardType())+1);
                }
            }
        }
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
        loadCardDeck();
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
        holeLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Hole");
        TiledMapTileLayer flagLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Flag");

        // Sneakily yoink the positions of the flags here, don't tell the OOP police
        getFlagPositionsFromLayer(flagLayer);
        getHolePositionsFromLayer(holeLayer);
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
