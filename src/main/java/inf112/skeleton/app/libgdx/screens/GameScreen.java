package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import inf112.skeleton.app.ui.chat.managers.ChatClient;
import inf112.skeleton.app.ui.chat.managers.ChatManager;
import inf112.skeleton.app.ui.chat.managers.Chatter;

import java.util.ArrayList;
import java.util.HashMap;
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

    /*
    * In order, index 0 to max is:
    * move 1, move 2, move 3, rotate left, rotate right, backup, uturn
    */
    private HashMap<CardType, TextureRegion> cardTemplates = new HashMap<>();
    // Duplicate card types currently in deck (for use in rendering)
    private HashMap<CardType, Integer> duplicates = new HashMap<>();

    /**
     * Client objects
     */
    // Handles all player based actions (picking cards, decks to send over network etc.)
    GamePlayer gamePlayer;
    // Handles all data transfers over internet
    Network network;
    // Chat handler
    Chatter chat;

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
        loadCardBackground();
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

        // Starts GameHost session using network that was initialized
        gamePlayer = new GameHost((NetworkHost)network);
        gamePlayer.setMap(map);
        // Send prompt to all connected clients
        Network.prompt("All players connected.", null);
        // Start connection to current clients. This is to be able to accept data transfers from clients
        this.network.initConnections();


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

        // Initialize card template textures
        loadCardTextures();

        // Start game/network objects
        startGame(isHost, ip, playerName);

        // Initialize chat variables and objects
        initializeChatObjects(playerName);
    }
    // Starts chat depending on client or host
    private void initializeChatObjects(String playerName){
        chat = network.isHost ? new ChatManager((NetworkHost)network) : new ChatClient((NetworkClient)network);
        Color chatColor = new Color(1f, 1f, 1f, 1);

        int subMenuHeight = 200;
        int sideMenuWidth = 240; // TODO fix hardcoded values

        chat.initializeChat(game, 0.85f, chatColor, "", sideMenuWidth, Gdx.graphics.getHeight()-subMenuHeight,  Gdx.graphics.getWidth()-sideMenuWidth, subMenuHeight);
        chat.setName(playerName);
        updateChat();
    }
    /**
     * Get new messages that have been received from the network, get formatted table and add input box with listener.
     */
    private void updateChat(){
        chat.updateChat(network.messagesRecived);
        Table chatTable = chat.getChatAsTable();

        Color inputBoxColor = new Color(1f, 1f, 1f, 1);

        TextField inputBox = new TextField("", game.skin);
        inputBox.setColor(inputBoxColor);
        inputBox.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER){
                    chat.sendMessage(inputBox.getText());
                    updateChat();
                }
                return true;
            }
        });

        chatTable.add(inputBox).left().width(220).height(30).padTop(20f);
        chatTable.row();

        stage.addActor(chatTable);
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
    private void loadCardTextures(){
        Texture allCards = new Texture("cards/programmingcards.png");

        TextureRegion[][] splitTextures = TextureRegion.split(allCards, 250, 400);

        cardTemplates.put(CardType.FORWARDONE, splitTextures[0][0]);
        cardTemplates.put(CardType.FORWARDTWO, splitTextures[0][1]);
        cardTemplates.put(CardType.FORWARDTHREE,  splitTextures[0][2]);
        cardTemplates.put(CardType.TURNLEFT,  splitTextures[0][3]);
        cardTemplates.put(CardType.TURNRIGHT,  splitTextures[0][4]);
        cardTemplates.put(CardType.BACK_UP, splitTextures[0][5]);
        cardTemplates.put(CardType.UTURN, splitTextures[0][6]);
    }

    /*
    * Helper for card image loading with touchup event
     */
    private Image newClickableCard(CardType cardType, TextureRegion t){
        int cardW = 100;
        int cardH = 170;

        Image img = new Image(t);
        img.setSize(cardW, cardH);

        img.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS && gamePlayer.chosenCards.size()<5){
                    Card c = new Card();
                    c.setCardType(cardType);
                    System.out.println("Clicked card with move " + cardType);

                    int handsize = 0;
                    for (Card carrrrd : gamePlayer.hand){
                        handsize += (carrrrd.getCardType() != CardType.NONE) ? 1 : 0;
                    }
                    System.out.println("Hand size: "+handsize);
                    System.out.println("Chosen cards size: "+gamePlayer.chosenCards.size());

                    // whoever is trying to understand this line, enjoy
                    gamePlayer.chooseCards(gamePlayer.hand.indexOf(gamePlayer.hand.stream().anyMatch(card -> (card.getCardType() == cardType)) ? gamePlayer.hand.stream().filter(card -> (card.getCardType() == cardType)).findFirst().get() : new Card()));

                    img.setColor(0.5f, 0.7f, 0.5f, 0.5f);

                    // Clear listener so it can't be clicked again TODO This is temporary solution, should be able to click to remove card from selection
                    img.getListeners().clear();
                }
            }
        });
        return img;
    }
    /**
     * Clear current stage cards and add new actors to stage
     */
    private void loadCardDeck(){
        duplicates.clear();
        int baseX = 15;
        int baseY = 15;

        int perCardIncrementX = 105;

        getDuplicateCards();


        List<Image> displayDeck = new ArrayList<>();
        int cardsTotal = 0;

        for (CardType t : duplicates.keySet()){
            int duplicatesCount = duplicates.get(t);

            // TODO this doesn't seem to want to render duplicate images no matter what i try...
            for (int i = 0; i<duplicatesCount; i++){
                Image img = newClickableCard(t, cardTemplates.get(t));
                img.setPosition(baseX, baseY);
                displayDeck.add(img);
                baseX += perCardIncrementX;
                cardsTotal++;
            }
        }
        System.out.println("Cards being added to stage: "+cardsTotal);
        displayDeck.forEach( (i) -> { stage.addActor(i); } );
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
     * Back button
     */
    private void loadBackButton(){

        TextButton backButton = new TextButton("Back", game.skin, "small");
        backButton.setWidth(225);
        backButton.setPosition(Gdx.graphics.getWidth()-240f, 30);
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });
        stage.addActor(backButton);
    }
    /**
     * Send cards button
     */
    private void loadSendCardsButton(){

        TextButton sendCardsButton = new TextButton("Send cards", game.skin, "small");
        sendCardsButton.setWidth(225);
        sendCardsButton.setPosition(Gdx.graphics.getWidth()-240f, 100);
        sendCardsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (gamePlayer.chosenCards.size() >= 5){
                    if (network.isHost){
                        if(((GameHost)gamePlayer).allCardsReady()){
                            System.out.println("Cards are being sent to processing. Stage size before deck clear: "+ stage.getActors().size);
                            stage.clear();
                            gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
                            gamePlayer.registerChosenCards();
                            gamePlayer.drawCardsFromDeck();
                        } else {
                            System.out.println("Not all players have delivered their cards yet! Cannot process cards yet.");
                        }
                    }else {
                        System.out.println("Cards are being sent to processing. Stage size before deck clear: "+ stage.getActors().size);
                        stage.clear();
                        gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
                        gamePlayer.registerChosenCards();
                        gamePlayer.drawCardsFromDeck();
                    }
                }
                return true;
            }
        });

        stage.addActor(sendCardsButton);
    }
    /**
     * Decorative background for card deck
     */
    private void loadCardBackground(){
        Texture cardBackgroundTexture = new Texture(Gdx.files.internal("cards/cards-background.png"));
        Image cardBackground = new Image(cardBackgroundTexture);
        cardBackground.setPosition(0, 0);
        cardBackground.setSize(Gdx.graphics.getWidth(), 200);
        stage.addActor(cardBackground);
    }
    private void loadActorsInOrder(){
        loadCardBackground();
        loadBackButton();
        loadSendCardsButton();
        loadCardDeck(); //TODO this already gets loaded in render. loading this in render is a bad idea, should be done here exclusively but need to find way to load deck at start of game too.
        updateChat();
    }
    /**
     * Helper function for keyUp to pick cards for player
     *  TODO rename me
     * @param keyCode key pressed
     */
    private void pickCardsOnKeyPress(int keyCode) {
        gamePlayer.chooseCards(keyCode);
        if(gamePlayer.chosenCards.size() >= 5){
            gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
            gamePlayer.registerChosenCards();
        }
    }
    /**
     * This function is called by libgdx when a key is released.
     * TODO rework me
     * @return true if keyrelease was handled (per libgdx)
     */
    public boolean keyUp (int keyCode){
        if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS){
            if(keyCode >= Input.Keys.NUM_1 && keyCode <= Input.Keys.NUM_9){
                return true;
            }
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

        if(gamePlayer.newCardsDelivered){
            stage.clear();

            System.out.println("Stage size after clearing hand: "+ stage.getActors().size);

            loadActorsInOrder();

            // Check if any null actors are found, clear them if so
            try{
                stage.getActors().forEach( (n) -> { if (n == null) { stage.getActors().removeValue(n, true); }});
            }catch (Exception e){
                System.out.println("Not able to remove null value from getActors, exception " + e);
            }

            System.out.println("Stage size after loading new hand: "+ stage.getActors().size);
            gamePlayer.newCardsDelivered = false;
        }

        stage.act();
        stage.draw();

        // Sends map to client of host, updates map in (this) if client
        updateMap();

        // Render current frame to screen
        game.renderer.render();
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
                // TODO Also maybe fix this
                ((GameHost)gamePlayer).host.sendMapLayerWrapper(((GameHost)gamePlayer).wrapper());
                ((GameHost)gamePlayer).map.loadPlayers(((GameHost)gamePlayer).wrapper());
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
        getBoardElementPositionsFromLayer(tiledMap);
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
    private void getBoardElementPositionsFromLayer(TiledMap tiledMap){
        TiledMapTileLayer holeLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Hole");
        TiledMapTileLayer gearLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Gear");
        TiledMapTileLayer wallLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Wall");
        TiledMapTileLayer beltLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Belts");
        for (int i = 0; i < holeLayer.getWidth(); i++){
            for (int j = 0; j < holeLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                if (holeLayer.getCell(i, j) != null) {
                    map.holeLayer[i][j] = true;
                }
                if (wallLayer.getCell(i, j) != null){
                    setWallDirections(wallLayer.getCell(i, j), i, j);
                }
                if (beltLayer.getCell(i, j) != null){
                    setBeltInformation(beltLayer.getCell(i, j), i, j);
                }
                if (gearLayer.getCell(i, j) != null && gearLayer.getCell(i, j).getTile().getId() == 54) {
                    map.gearLayer[i][j] = 1;
                }
                else if (gearLayer.getCell(i, j) != null && gearLayer.getCell(i, j).getTile().getId() == 189) {
                    map.gearLayer[i][j] = 2;
                }
                else map.gearLayer[i][j] = 0;

            }
        }
    }

    private void setWallDirections(TiledMapTileLayer.Cell wallCell, int i, int j){
        //TODO a lot of these are lacking

        // NORTH, EAST, SOUTH, WEST
        if (wallCell.getTile().getId() == 24) map.wallLayer[i][j] = new boolean[] {true, false, false, true};
        if (wallCell.getTile().getId() == 31) map.wallLayer[i][j] = new boolean[] {true, false, false, false};
        if (wallCell.getTile().getId() == 16) map.wallLayer[i][j] = new boolean[] {true, true, false, false};
        if (wallCell.getTile().getId() == 29) map.wallLayer[i][j] = new boolean[] {false, false, true, false};
        if (wallCell.getTile().getId() == 30) map.wallLayer[i][j] = new boolean[] {false, false, false, true};
        if (wallCell.getTile().getId() == 8) map.wallLayer[i][j] = new boolean[] {false, true, true, false};
        if (wallCell.getTile().getId() == 23) map.wallLayer[i][j] = new boolean[] {true, true, false, false};
        if (wallCell.getTile().getId() == 12) {
            map.wallLayer[i][j] = new boolean[] {false, false, false, true};
            map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, false, 0);
        }
        if (wallCell.getTile().getId() == 11) {
            map.wallLayer[i][j] = new boolean[] {false, false, true, false};
            map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.NORTH, false, 0);
        }
        if (wallCell.getTile().getId() == 10) {
            map.wallLayer[i][j] = new boolean[] {false, true, false, false};
            map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, false, 0);
        }
        if (wallCell.getTile().getId() == 9) {
            map.wallLayer[i][j] = new boolean[] {true, false, false, false};
            map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.SOUTH, false, 0);
        }
        //if (wallCell.getTile().getId() == 11) map.wallLayer[i][j] = new boolean[] {true, false, false, true};

    }

    private void setBeltInformation(TiledMapTileLayer.Cell beltCell, int i, int j){
        if (beltCell.getTile().getId() == 50) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.SOUTH, false, 0);
        if (beltCell.getTile().getId() == 21) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.SOUTH, true, 0);
        if (beltCell.getTile().getId() == 49) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.NORTH, false, 0);
        if (beltCell.getTile().getId() == 41) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, false, -1);
        if (beltCell.getTile().getId() == 52) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, false, 0);
        if (beltCell.getTile().getId() == 86) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.SOUTH, true, 1);
        if (beltCell.getTile().getId() == 51) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, false, 0);
        if (beltCell.getTile().getId() == 22) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, true, 0);
        if (beltCell.getTile().getId() == 14) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, true, 0);
        if (beltCell.getTile().getId() == 13) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.NORTH, true, 0);
        if (beltCell.getTile().getId() == 77) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.NORTH, true, 1);
        if (beltCell.getTile().getId() == 34) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, false, -1);
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
