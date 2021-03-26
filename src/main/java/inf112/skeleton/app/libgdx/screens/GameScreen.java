package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.PlayerConfig;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.cards.CardDisplay;
import inf112.skeleton.app.ui.chat.CommandParser;
import inf112.skeleton.app.ui.chat.managers.ChatClient;
import inf112.skeleton.app.ui.chat.managers.ChatManager;
import inf112.skeleton.app.ui.chat.managers.Chatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameScreen extends ScreenAdapter {
    private final RoboGame game;
    private final Stage stage;

    Map map = new Map();

    // Layers of the map
    private TiledMapTileLayer playerLayer;

    // Flags on the map are stored here for easy access
    // TODO: this should really only useful in GameHost
    public List<Flag> flagPositions = new ArrayList<>();

    // Cells for each player state
    private TiledMapTileLayer.Cell playerNormal;
    private TiledMapTileLayer.Cell playerWon;

    private TiledMapTileLayer.Cell singleHorizontal;
    private TiledMapTileLayer.Cell singleBoth;
    private TiledMapTileLayer.Cell singleVertical;
    private TiledMapTileLayer.Cell doubleBoth;
    private TiledMapTileLayer.Cell doubleVertical;
    private TiledMapTileLayer.Cell doubleHorizontal;

    /*
     * In order, index 0 to max is:
     * move 1, move 2, move 3, rotate left, rotate right, backup, uturn
     */
    private final HashMap<CardType, TextureRegion> cardTemplates = new HashMap<>();
    // Duplicate card types currently in deck (for use in rendering)
    private final HashMap<CardType, Integer> duplicates = new HashMap<>();

    /**
     * Client objects
     */
    // Handles all player based actions (picking cards, decks to send over network etc.)
    GamePlayer gamePlayer;
    // Handles all data transfers over internet
    Network network;
    // Chat handler
    Chatter chat;
    // To handle updates in the chat received from network
    int networkChatBacklogSize = 0;

    public GameScreen(RoboGame game, boolean isHost, String ip, String playerName) {
        this.game = game;
        stage = new Stage(new ScreenViewport());

        // Backwards capability for Game->GameScreen merge,
        // enables key press detection for testing other parts of game while cards haven't been implemented yet
        stage.addListener(new InputListener() {
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
    public void startGame(boolean isHost, String ip, String playerName) {
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
    private void startHost(String playerName) {

        // Starts GameHost session using network that was initialized
        gamePlayer = new GameHost((NetworkHost) network);
        gamePlayer.setMap(map);
        // Send prompt to all connected clients
        Network.prompt("All players connected.", null);
        // Start connection to current clients. This is to be able to accept data transfers from clients
        this.network.initConnections();


        ((GameHost) gamePlayer).initializeHostPlayerToken(playerName);
        gamePlayer.drawCards();
    }

    // Start game as client
    private void startClient(String ip, String playerName) {
        if (((NetworkClient) network).connectToServer(ip)) {
            gamePlayer = new GameClient((NetworkClient) network, playerName);
            gamePlayer.setMap(map);
        } else {
            System.out.println("Failed to start client due to connection error.");
            System.exit(0);
        }
    }

    /**
     * Initialize all libgdx objects:
     * Batch, font, input processor, textures, map layers, camera and renderer,
     * and Fetch flags from flag layer
     * <p>
     * This function is called on libgdx startup
     */
    public void create(boolean isHost, String ip, String playerName) {

        // Load the map's layers
        loadMapLayers(game.tiledMap);

        // Initialize player textures from .png file
        loadTextures();

        // Initialize card template textures
        loadCardTextures();

        // Start game/network objects
        startGame(isHost, ip, playerName);

        // Initialize chat variables and objects
        initializeChatObjects(playerName);

        // load list of players on map
        loadPlayerList();
    }

    // Starts chat depending on client or host
    private void initializeChatObjects(String playerName){
        chat = network.isHost ? new ChatManager((NetworkHost)network) : new ChatClient((NetworkClient)network);
        Color chatColor = new Color(1f, 1f, 1f, 1);

        int subMenuHeight = 200;
        int sideMenuWidth = 375; // TODO fix hardcoded values

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

                    if (inputBox.getText().length()>2){
                        // If chat is a command
                        if (inputBox.getText().substring(0, 2).equals("/c")){
                            CommandParser p = new CommandParser();
                            String commandContent = inputBox.getText().substring(3);
                            System.out.println("Chat command entered: " + commandContent);

                            // Get what command was input by user
                            CommandParser.Command command = p.parseCommand(p.getCmd(commandContent));

                            // Perform command
                            switch (command){
                                case SETNAME:
                                    chat.setName(p.getArgs(commandContent));
                                    break;
                                case SETCOLOR:
                                    switch (p.getArgs(commandContent)) {
                                        case "r":
                                            Color red = new Color(1, 0, 0, 1);
                                            chat.chat.setChatColour(red);
                                            break;
                                        case "g":
                                            Color green = new Color(0, 1, 0, 1);
                                            chat.chat.setChatColour(green);
                                            break;
                                        case "b":
                                            Color blue = new Color(0, 0, 1, 1);
                                            chat.chat.setChatColour(blue);
                                            break;
                                        case "black":
                                            Color black = new Color(1, 1, 1, 1);
                                            chat.chat.setChatColour(black);
                                            break;
                                        default:
                                            System.out.println("Invalid colour.");
                                            break;
                                    }
                                    break;
                                case SETFONTSTCALE:
                                    float scale = Float.parseFloat(p.getArgs(commandContent));
                                    chat.chat.setChatFontSize(scale);
                                    break;
                                case INVALID:
                                    chat.sendMessage("Entered invalid command.");
                                    break;
                                default:
                                    break;
                            }
                            // Send list of commands available if /h
                        }else if (inputBox.getText().substring(0, 2).equals("/h")){
                            chat.sendMessage("Commands:");
                            chat.sendMessage("/c set-name <name>");
                            chat.sendMessage("/c chat-color <r, g, b, black>");
                            chat.sendMessage("/c font-scale <font scale>");
                        }
                    }

                    // Send message
                    else {
                        chat.sendMessage(inputBox.getText());
                    }
                    updateChat();
                }
                return true;
            }
        });

        chatTable.add(inputBox).left().width(355).height(30).padTop(20f);
        chatTable.row();

        stage.addActor(chatTable);
    }

    /**
     * Load player texture and split into each player state
     */
    public void loadTextures() {
        // Load the entire player texture
        //Color playerColor = Color.RED;

        //load playercolor from file if possible
        PlayerConfig config = CharacterCustomizer.loadCharacterConfigFromFile();
        Color playerColor = config.getMainColor();
        String playerImage = config.getImage();

        Texture rawPlayerTexture = CharacterCustomizer.generatePlayerTexture(playerImage, playerColor);

        // Split player texture into seperate regions
        TextureRegion roboPlayerSplitTexture = new TextureRegion(rawPlayerTexture,300, 300);

        // Put the texture region into seperate tiles
        StaticTiledMapTile playerStaticTile = new StaticTiledMapTile(roboPlayerSplitTexture);

        // Set player state cells to corresponding tiles
        playerNormal = new TiledMapTileLayer.Cell().setTile(playerStaticTile);
        playerWon = new TiledMapTileLayer.Cell().setTile(playerStaticTile);

        Texture rawLaserTexture = new Texture("tiles.png");

        // Split player texture into seperate regions
        TextureRegion[][] splitLaserTextures = TextureRegion.split(rawLaserTexture, 300, 300);

        StaticTiledMapTile singleHorizontal = new StaticTiledMapTile(splitLaserTextures[4][6]);
        StaticTiledMapTile singleBoth = new StaticTiledMapTile(splitLaserTextures[4][7]);
        StaticTiledMapTile singleVertical = new StaticTiledMapTile(splitLaserTextures[5][6]);
        StaticTiledMapTile doubleBoth = new StaticTiledMapTile(splitLaserTextures[12][4]);
        StaticTiledMapTile doubleVertical = new StaticTiledMapTile(splitLaserTextures[12][5]);
        StaticTiledMapTile doubleHorizontal = new StaticTiledMapTile(splitLaserTextures[12][6]);

        this.singleHorizontal = new TiledMapTileLayer.Cell().setTile(singleHorizontal);
        this.singleBoth = new TiledMapTileLayer.Cell().setTile(singleBoth);
        this.singleVertical = new TiledMapTileLayer.Cell().setTile(singleVertical);
        this.doubleBoth = new TiledMapTileLayer.Cell().setTile(doubleBoth);
        this.doubleVertical = new TiledMapTileLayer.Cell().setTile(doubleVertical);
        this.doubleHorizontal = new TiledMapTileLayer.Cell().setTile(doubleHorizontal);
    }

    /**
     * Loads card images and adds event listeners.
     * --> Event listener only adds a card of the type pressed into gamePlayer's chosenCards
     * Adds cards into hashmap with corresponding card type
     */
    private void loadCardTextures() {
        Texture allCards = new Texture("cards/programmingcards.png");

        TextureRegion[][] splitTextures = TextureRegion.split(allCards, 250, 400);

        cardTemplates.put(CardType.FORWARDONE, splitTextures[0][0]);
        cardTemplates.put(CardType.FORWARDTWO, splitTextures[0][1]);
        cardTemplates.put(CardType.FORWARDTHREE, splitTextures[0][2]);
        cardTemplates.put(CardType.TURNLEFT, splitTextures[0][3]);
        cardTemplates.put(CardType.TURNRIGHT, splitTextures[0][4]);
        cardTemplates.put(CardType.BACK_UP, splitTextures[0][5]);
        cardTemplates.put(CardType.UTURN, splitTextures[0][6]);
    }

    /**
     * Clear current stage cards and add new actors to stage
     */
    private void loadCardDeck(){
        // Base for entire hand
        int baseX = 75;
        int baseY = 30;
        int perCardIncrementX = 110;

        getDuplicateCardsInHand();

        List<Image> displayDeck = new ArrayList<>();

        for (CardType t : duplicates.keySet()) {
            int duplicatesCount = duplicates.get(t);

            // Place every duplicate image next to each other with perCardIncrementX increments in distance
            for (int i = 0; i<duplicatesCount; i++){
                Image img = generateClickableCard(t, cardTemplates.get(t));

                img.setPosition(baseX, baseY);
                displayDeck.add(img);
                baseX += perCardIncrementX;
            }
        }
        // Add all images to stage
        displayDeck.forEach( (i) -> { stage.addActor(i); } );
    }

    /*
     * Helper for card image loading with touchup event
     */
    public Image generateClickableCard(CardType cardType, TextureRegion t){
        int cardW = 100;
        int cardH = 135;

        Image img = new Image(t);
        img.setSize(cardW, cardH);

        img.addListener(new ClickListener(){
            // Assign event handler to handle card choice on click
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS && gamePlayer.chosenCards.size()<5){
                    Card c = new Card();
                    c.setCardType(cardType);
                    System.out.println("Clicked card with move " + cardType);

                    // intellij complaining about get before ispresent check is incorrect
                    gamePlayer.chooseCards(gamePlayer.hand.indexOf(gamePlayer.hand.stream().anyMatch(card -> (card.getCardType() == cardType)) ? gamePlayer.hand.stream().filter(card -> (card.getCardType() == cardType)).findFirst().get() : new Card()));

                    // Give some green feedback on click
                    img.setColor(0.5f, 0.7f, 0.5f, 0.5f);

                    // Clear listener so it can't be clicked again
                    img.getListeners().clear();
                }
            }
        });
        return img;
    }

    /**
     * Back button
     */
    private void loadBackButton() {
        TextButton backButton = new TextButton("Back", game.skin, "small");
        backButton.setWidth(125);
        backButton.setPosition(Gdx.graphics.getWidth()-145f, 30);
        backButton.setColor(0.1f, 0, 0, 1);
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
     * finds duplicate cards in deck
     * HashMap used because its O(n) instead of O(n^2)
     */
    public void getDuplicateCardsInHand() {
        duplicates.clear();
        if (!gamePlayer.hand.isEmpty()) {
            for (Card c : gamePlayer.hand) {
                if (!duplicates.containsKey(c.getCardType())) {
                    duplicates.put(c.getCardType(), 1);
                } else {
                    duplicates.put(c.getCardType(), duplicates.get(c.getCardType()) + 1);
                }
            }
        }
    }
    /**
     * Send cards button
     */
    private void loadSendCardsButton(){
        TextButton sendCardsButton = new TextButton("Send cards", game.skin, "small");
        sendCardsButton.setWidth(125);
        sendCardsButton.setPosition(Gdx.graphics.getWidth()-145f, 105);
        sendCardsButton.setColor(0.1f, 0, 0, 1);
        sendCardsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (gamePlayer.chosenCards.size() >= 5) {
                    if (network.isHost) {
                        if (((GameHost) gamePlayer).allCardsReady()) {
                            System.out.println("Cards are being sent to processing. Stage size before deck clear: " + stage.getActors().size);
                            stage.clear();
                            gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
                            gamePlayer.registerChosenCards();
                            gamePlayer.drawCardsFromDeck();
                        } else {
                            System.out.println("Not all players have delivered their cards yet! Cannot process cards yet.");
                        }
                    } else {
                        System.out.println("Cards are being sent to processing. Stage size before deck clear: " + stage.getActors().size);
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
     * All player positions and directions
     */
    private void loadPlayerList(){
        Table tableList = new Table();
        tableList.top().left().pad(5).setSize(210, 145);
        tableList.setPosition(Gdx.graphics.getWidth()-375, 30);

        Label tIndicator = new Label("Player locations:", game.skin);
        tableList.add(tIndicator);
        tableList.row();

        int playNo = 1;
        boolean anyPlayers = false;
        for (int x = 0; x<map.playerLayer.length; x++){
            for (int y = 0; y<map.playerLayer[x].length; y++){
                if (map.playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE){
                    anyPlayers = true;
                    String str = "Player "+ playNo +" at " + x + ", " + y + " - Facing: " + map.playerLayer[x][y].dir.toString();
                    Label l = new Label(str, game.skin);
                    l.setColor(0.7588f, 0.3188f, 0.1960f, 1);
                    l.setAlignment(Align.left);
                    l.setFontScale(0.8f);
                    tableList.add(l);
                    tableList.row();
                    playNo++;
                }
            }
        }
        if (!anyPlayers){
            String str = "Waiting for first round to start.";
            Label l = new Label(str, game.skin);
            l.setColor(0.7588f, 0.3188f, 0.1960f, 1);
            l.setFontScale(0.8f);
            tableList.add(l);
            tableList.row();
        }
        stage.addActor(tableList);
    }

    /**
     * Decorative background for card deck
     */
    private void loadCardBackground(){
        // Simple border around cards
        Texture cardBackgroundTexture = new Texture(Gdx.files.internal("cards/bottom-border.png"));
        Image cardBackground = new Image(cardBackgroundTexture);
        cardBackground.setPosition(0, 0);
        cardBackground.setSize(Gdx.graphics.getWidth()-375, 200);
        stage.addActor(cardBackground);

        // Simple colour texture behind buttons
        Texture buttonBackgroundTexture = new Texture(Gdx.files.internal("cards/bottom-background-color.png"));
        Image buttonBackground = new Image(buttonBackgroundTexture);
        buttonBackground.setPosition(Gdx.graphics.getWidth()-375, 0);
        buttonBackground.setSize(375, 200);
        stage.addActor(buttonBackground);
    }

    /**
     * Load visual backgrounds first, then render important elements at the end
     * This needs to be called whenever stage is cleared
     */
    private void loadActorsInOrder(){
        loadCardBackground();
        loadBackButton();
        loadSendCardsButton();
        loadCardDeck();
        loadPlayerList();
        updateChat();
    }

    /**
     * This function is called by libgdx when a key is released.
     * TODO rework me
     *
     * @return true if keyrelease was handled (per libgdx)
     */
    public boolean keyUp(int keyCode) {
        if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS) {
            return keyCode >= Input.Keys.NUM_1 && keyCode <= Input.Keys.NUM_9;
        }
        return false;
    }

    /**
     * Render all objects and text to the screen
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        pollUiUpdates();

        stage.act();
        stage.draw();

        // Sends map to client of host, updates map in (this) if client
        updateMap();

        // Render current frame to screen
        game.renderer.render();
    }

    /**
     * Poll updates from the network client that needs to be updated to local session in real time
     */
    private void pollUiUpdates(){
        // force chat to update when receiving new messages in network
        if (networkChatBacklogSize < network.messagesRecived.size()){
            stage.clear();
            loadActorsInOrder();
            networkChatBacklogSize = network.messagesRecived.size();
        }

        // Force cards to update when new cards have been received
        if(gamePlayer.newCardsDelivered){
            stage.clear();

            System.out.println("Stage size after clearing hand: " + stage.getActors().size);

            loadActorsInOrder();

            // Check if any null actors are found, clear them if so
            try {
                stage.getActors().forEach((n) -> {
                    if (n == null) {
                        stage.getActors().removeValue(n, true);
                    }
                });
            } catch (Exception e) {
                System.out.println("Not able to remove null value from getActors, exception " + e);
            }

            System.out.println("Stage size after loading new hand: " + stage.getActors().size);
            gamePlayer.newCardsDelivered = false;
        }
    }

    /**
     * Reset cell rotation on all cells in the map to 0
     */
    private void resetCellRotation() {
        for (int x = 0; x < playerLayer.getWidth(); x++) {
            for (int y = 0; y < playerLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = playerLayer.getCell(x, y);
                cell.setRotation(0);
                playerLayer.setCell(x, y, cell);
            }
        }
    }

    /**
     * Rotates cells according to location in map player layer directions
     */
    private void rotateCellsAccordingToDirection() {
        game.batch.begin();
        game.font.getData().setScale(1);
        for (int x = 0; x < map.playerLayer.length; x++) {
            for (int y = 0; y < map.playerLayer[x].length; y++) {
                if (map.playerLayer[x][y].state != PlayerToken.CHARACTER_STATES.NONE) {
                    switch (map.playerLayer[x][y].dir) {
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
    public void updateMap() {
        if (map != null) {
            map = gamePlayer.updateMap(null);
            //
            if (network.isHost) {
                // TODO Also maybe fix this
                ((GameHost) gamePlayer).host.sendMapLayerWrapper(((GameHost) gamePlayer).wrapper());
                ((GameHost) gamePlayer).map.loadPlayers(((GameHost) gamePlayer).wrapper().PlayerTokens);
                if (((GameHost) gamePlayer).isShowingCards) {
                    ((GameHost) gamePlayer).handleSingleCardRound();
                }
            }
            translatePlayerLayer();
            resetCellRotation();
            rotateCellsAccordingToDirection();
            loadLasers();
            // TODO: board and flag layer doesn't change as of this version
        }
    }

    public void loadLasers() {
        for (int x = 0; x < Game.BOARD_X; x++) {
            for (int y = 0; y < Game.BOARD_Y; y++) {
                ((TiledMapTileLayer) game.tiledMap.getLayers().get("Laser")).setCell(x, y, laserToTile(x, y));
            }
        }
    }

    public TiledMapTileLayer.Cell laserToTile(int x, int y) {

        //TODO FIX THIS SHIT to add support for doubles
        if (map.laserLayer[x][y][0] == 1) return singleVertical;
        if (map.laserLayer[x][y][0] == 2) return doubleVertical;
        if (map.laserLayer[x][y][1] == 1) return singleHorizontal;
        if (map.laserLayer[x][y][1] == 2) return doubleHorizontal;
        if (map.laserLayer[x][y][2] == 1) return singleVertical;
        if (map.laserLayer[x][y][2] == 2) return doubleVertical;
        if (map.laserLayer[x][y][3] == 1) return singleHorizontal;
        if (map.laserLayer[x][y][3] == 2) return doubleHorizontal;
        else return null;
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
        TiledMapTileLayer repairLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Repair");
        for (int i = 0; i < holeLayer.getWidth(); i++){
            for (int j = 0; j < holeLayer.getHeight(); j++){
                // getCell returns null if nothing is found in the current cell in this layer
                map.holeLayer[i][j] = holeLayer.getCell(i, j) != null;
                map.repairLayer[i][j] = repairLayer.getCell(i, j) != null;
                if (wallLayer.getCell(i, j) != null){
                    setWallDirections(wallLayer.getCell(i, j), i, j);
                    //The wall layer contains information about laser shooters
                    setLaserDirection(wallLayer.getCell(i, j), i, j);
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

    private void setLaserDirection(TiledMapTileLayer.Cell laserCell, int i, int j) {
        // NORTH, EAST, SOUTH, WEST
        if (laserCell.getTile().getId() == 38) map.laserShooters.add(new Map.LaserShooter(PlayerToken.Direction.EAST, 1, i, j));
        if (laserCell.getTile().getId() == 46) map.laserShooters.add(new Map.LaserShooter(PlayerToken.Direction.WEST, 1, i, j));
        if (laserCell.getTile().getId() == 95) map.laserShooters.add(new Map.LaserShooter(PlayerToken.Direction.WEST, 2, i, j));
        if (laserCell.getTile().getId() == 93) map.laserShooters.add(new Map.LaserShooter(PlayerToken.Direction.EAST, 2, i, j));
    }

    private void setWallDirections(TiledMapTileLayer.Cell wallCell, int i, int j){
        //TODO a lot of these are lacking

        // NORTH, EAST, SOUTH, WEST. This is where the wall is placed
        //TODO redo all these?
        if (wallCell.getTile().getId() == 24) map.wallLayer[i][j] = new boolean[] {true, false, false, true};
        if (wallCell.getTile().getId() == 31) map.wallLayer[i][j] = new boolean[] {true, false, false, false};
        if (wallCell.getTile().getId() == 16) map.wallLayer[i][j] = new boolean[] {true, true, false, false};
        if (wallCell.getTile().getId() == 29) map.wallLayer[i][j] = new boolean[] {false, false, true, false};
        if (wallCell.getTile().getId() == 30) map.wallLayer[i][j] = new boolean[] {false, false, false, true};
        if (wallCell.getTile().getId() == 8) map.wallLayer[i][j] = new boolean[] {false, true, true, false};
        if (wallCell.getTile().getId() == 23) map.wallLayer[i][j] = new boolean[] {false, true, false, false};

        if (wallCell.getTile().getId() == 38) map.wallLayer[i][j] = new boolean[] {false, false, false, true};
        if (wallCell.getTile().getId() == 46) map.wallLayer[i][j] = new boolean[] {false, true, false, false};
        if (wallCell.getTile().getId() == 95) map.wallLayer[i][j] = new boolean[] {false, true, false, false};
        if (wallCell.getTile().getId() == 93) map.wallLayer[i][j] = new boolean[] {false, false, false, true};

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
        if (beltCell.getTile().getId() == 41) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, false, -1, PlayerToken.Direction.NORTH);
        if (beltCell.getTile().getId() == 52) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, false, 0);
        if (beltCell.getTile().getId() == 86) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.SOUTH, true, 1, PlayerToken.Direction.EAST);
        if (beltCell.getTile().getId() == 51) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, false, 0);
        if (beltCell.getTile().getId() == 22) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, true, 0);
        if (beltCell.getTile().getId() == 14) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.EAST, true, 0);
        if (beltCell.getTile().getId() == 13) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.NORTH, true, 0);
        if (beltCell.getTile().getId() == 77) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.NORTH, true, 1, PlayerToken.Direction.WEST);
        if (beltCell.getTile().getId() == 34) map.beltLayer[i][j] = new Map.BeltInformation(PlayerToken.Direction.WEST, false, -1, PlayerToken.Direction.SOUTH);
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
