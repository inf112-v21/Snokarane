package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.*;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.PlayerConfig;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.chat.CommandParser;
import inf112.skeleton.app.ui.chat.backend.Uwufier;
import inf112.skeleton.app.ui.chat.managers.ChatClient;
import inf112.skeleton.app.ui.chat.managers.ChatManager;
import inf112.skeleton.app.ui.chat.managers.Chatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Controls most events that happen in game
 * Every local change starts inside this class, as it contains the network that fetches updates
 *
 * WARNING
 * This class is pretty large, but methods and variables have been moved so methods that are related are placed closeby
 * Recommended way of reading this class is using collapse all, then exoand doc comments only:
 * Can be done in intellij with (depending on keybinds):
 *  1. CTRL+SHIT+A -> "Collapse All" -> CTRL+SHIFT+A -> "Expand Doc Comments
 *  2. CTRL+SHIFT+NUMPAD_MINUS -> CTRL+SHIFT+A -> "Expand Doc Comments"
 *
 */
public class GameScreen extends ScreenAdapter {
    private final RoboGame game;
    private final Stage stage;


    /**--------- ------------------ ------------------ ------------------ ---------
     /* --------- ------------------  Internal map vars ------------------ ---------
     /* --------- ------------------ ------------------ ------------------ ---------
     */
    Map map = new Map();
    // Flags on the map are stored here for easy access
    public List<Flag> flagPositions = new ArrayList<>();


    /**--------- ------------------ ------------------ ------------------ ---------
     /* --------- ------------------  Tiled map info    ------------------ ---------
     /* --------- ------------------ ------------------ ------------------ ---------
     */
    // Layers of the map
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer.Cell singleHorizontal;
    private TiledMapTileLayer.Cell singleVertical;
    private TiledMapTileLayer.Cell doubleVertical;
    private TiledMapTileLayer.Cell doubleHorizontal;


    /**--------- ------------------ ------------------ ------------------ ---------
     /* --------- ------------------   Card helpers     ------------------ ---------
     /* --------- ------------------ ------------------ ------------------ ---------
     */
    /*
     * In order, index 0 to max is:
     * move 1, move 2, move 3, rotate left, rotate right, backup, uturn
     */
    private final HashMap<CardType, TextureRegion> cardTemplates = new HashMap<>();
    // Duplicate card types currently in deck (for use in rendering)
    private final HashMap<CardType, Integer> duplicates = new HashMap<>();



    /**--------- ------------------ ------------------ ------------------ ---------
     /* --------- ------------------Client-host helpers ------------------ ---------
     /* --------- ------------------ ------------------ ------------------ ---------
     */
    // Handles all player based actions (picking cards, decks to send over network etc.)
    GamePlayer gamePlayer;
    // Handles all data transfers over internet
    Network network;
    // Chat handler
    Chatter chat;
    // To handle updates in the chat received from network
    int networkChatBacklogSize = 0;
    // Grace time to give clients for responding to name request
    // If client ping is greater than this grace time, the players token won't be initialized
    int clientResponseGraceTime = 500; // ping, in ms

    public GameScreen(RoboGame game, String playerName, Network net) {
        this.game = game;
        this.network = net;
        stage = new Stage(new ScreenViewport());

        game.stopMenuMusic();
        game.startGameMusic();

        loadCardBackground();
        create(playerName);
    }





    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------ Class setup methods--------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */

    /**
     * Initialize objects depending on host status
     * These methods are needed to start a game session to other players over network
     * Function called regardless of host or player status, initializes network and asks for host/client role selection
     */
    public void startGame(String playerName) {
        map.flagList = flagPositions;

        if (network.isHost)
            startHost(playerName);
        else
            startClient(playerName);

    }
    /**
     * Start game as host
     * @param playerName player name ID to identify player in game
     */
    private void startHost(String playerName) {
        // Starts GameHost session using network that was initialized
        gamePlayer = new GameHost((NetworkHost) network);
        gamePlayer.setMap(map);

        // Assign ID's to client maps
        ((NetworkHost)network).sendIDs();
        // Assign ID's to client maps
        ((NetworkHost)network).requestNames();

        // This wait time is to give clients some grace time to respond to name requests
        // If this is removed, the host won't receive client names in time, and as such clients will
        // Start generating cards and map from objects that haven't been initialized yet (like NetworkClient.map and NetworkClient.gameClient)
        if (((NetworkHost) network).clientsRegistered > 0){
            System.out.println(((NetworkHost) network).clientsRegistered + " clients registeered.");
            long time = System.currentTimeMillis();
            while (System.currentTimeMillis() < time+clientResponseGraceTime);
        }

        ((GameHost) gamePlayer).initializeHostPlayerToken(playerName);

        gamePlayer.drawCards();
    }
    /**
     *
     * @param playerName player name ID to identify player in game
     */
    private void startClient(String playerName) {
        gamePlayer = new GameClient((NetworkClient) network, playerName);
        gamePlayer.setMap(map);
    }
    /**
     * Initialize all libgdx objects:
     * Batch, font, input processor, textures, map layers, camera and renderer,
     * and Fetch flags from flag layer
     * <p>
     * This function is called on libgdx startup
     */
    public void create(String playerName) {

        // Load the map's layers
        loadMapLayers(game.tiledMap);

        // Initialize game-element textures
        loadTextures();

        // Initialize card template textures
        loadCardTextures();

        // Start game/network objects
        startGame(playerName);

        // Initialize chat variables and objects
        initializeChatObjects(playerName);

        // load list of players on map
        loadPlayerList();
    }
    /**
     * Starts chat depending on client or host
     * @param playerName player name used in chat, prepends every message with name
     */
    private void initializeChatObjects(String playerName){
        chat = network.isHost ? new ChatManager((NetworkHost)network) : new ChatClient((NetworkClient)network);
        Color chatColor = new Color(1f, 1f, 1f, 1);

        int subMenuHeight = 200;
        int sideMenuWidth = 375; // TODO fix hardcoded values

        chat.initializeChat(game, 0.85f, chatColor, sideMenuWidth, Gdx.graphics.getHeight()-subMenuHeight,  Gdx.graphics.getWidth()-sideMenuWidth, subMenuHeight);
        chat.setName(playerName);
        Table emptyChat = chat.getChatAsTable();
        emptyChat.setName("chat");
        stage.addActor(emptyChat);
        updateChat();
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







    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------   Texture handling --------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */

    public void loadTextures() {

        Texture rawLaserTexture = new Texture("tiles.png");

        // Split player texture into seperate regions
        TextureRegion[][] splitLaserTextures = TextureRegion.split(rawLaserTexture, 300, 300);

        StaticTiledMapTile singleHorizontal = new StaticTiledMapTile(splitLaserTextures[4][6]);
        StaticTiledMapTile singleVertical = new StaticTiledMapTile(splitLaserTextures[5][6]);
        StaticTiledMapTile doubleVertical = new StaticTiledMapTile(splitLaserTextures[12][5]);
        StaticTiledMapTile doubleHorizontal = new StaticTiledMapTile(splitLaserTextures[12][6]);

        this.singleHorizontal = new TiledMapTileLayer.Cell().setTile(singleHorizontal);
        this.singleVertical = new TiledMapTileLayer.Cell().setTile(singleVertical);
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
     * Helper for card image loading with touchup event
     */
    public Image generateClickableCard(CardType cardType, TextureRegion t, boolean picked){
        int cardW = 100;
        int cardH = 135;

        Image img = new Image(t);
        img.setSize(cardW, cardH);

        if (picked){
            img.setColor(0.5f, 0.7f, 0.5f, 0.5f);
        }

        img.addListener(new ClickListener(){
            // Assign event handler to handle card choice on click
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS && gamePlayer.chosenCards.size()<5){
                    Card c = new Card();
                    c.setCardType(cardType);
                    System.out.println("Clicked card with move " + cardType);

                    /*
                    // intellij complaining about get before ispresent check is incorrect
                    gamePlayer.chooseCards(gamePlayer.hand.indexOf(gamePlayer.hand.stream().anyMatch(card -> (card.getCardType() == cardType)) ? gamePlayer.hand.stream().filter(card -> (card.getCardType() == cardType)).findFirst().get() : new Card()));
                    */
                    gamePlayer.chooseCards(
                            gamePlayer.hand.indexOf(
                                    gamePlayer.hand.stream().anyMatch(
                                            card -> (card.getCardType() == cardType))
                                            ? gamePlayer.hand.stream().filter(card -> (card.getCardType() == cardType)).findFirst().get()
                                            : new Card()));
                    c.picked = true;
                    gamePlayer.chosenCards.add(c);

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
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------   Stage loading    --------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
    /**
     * Load visual backgrounds first, then render important elements at the end
     * This needs to be called whenever stage is cleared
     */
    private void loadActorsInOrder(){
        loadCardBackground();
        loadBackButton();
        loadSendCardsButton();
        loadResetCardChoicesButton();
        loadCardDeck();
        loadPlayerList();
        loadPowerDown();
        loadChatInputBox();
        updateChat();
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
        cardBackground.setName("card-background");
        stage.addActor(cardBackground);

        // Simple colour texture behind buttons
        Texture buttonBackgroundTexture = new Texture(Gdx.files.internal("cards/bottom-background-color.png"));
        Image buttonBackground = new Image(buttonBackgroundTexture);
        buttonBackground.setPosition(Gdx.graphics.getWidth()-375, 0);
        buttonBackground.setSize(375, 200);
        buttonBackground.setName("button-background");
        stage.addActor(buttonBackground);
    }
    /**
     * Back button
     */
    private void loadBackButton() {
        TextButton backButton = new TextButton("Back", game.skin, "small");
        backButton.setWidth(125);
        backButton.setPosition(Gdx.graphics.getWidth()-145f, 5);
        backButton.setColor(0.1f, 0, 0, 1);
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.stopGameMusic();
                game.startMenuMusic();
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });
        backButton.setName("back-button");
        stage.addActor(backButton);
    }
    /**
     * Reset card choices button
     */
    private void loadResetCardChoicesButton(){
        TextButton resetCardChoicesButton = new TextButton("Reset cards", game.skin, "small");
        resetCardChoicesButton.setWidth(125);
        resetCardChoicesButton.setPosition(Gdx.graphics.getWidth()-145f, 65);
        resetCardChoicesButton.setColor(0.1f, 0, 0, 1);
        resetCardChoicesButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetCardChoices();
                return true;
            }
        });
        resetCardChoicesButton.setName("reset-card-choices-button");
        stage.addActor(resetCardChoicesButton);
    }
    /**
     * Send cards button
     */
    private void loadSendCardsButton(){
        TextButton sendCardsButton = new TextButton("Send cards", game.skin, "small");
        sendCardsButton.setWidth(125);
        sendCardsButton.setPosition(Gdx.graphics.getWidth()-145f, 130);
        sendCardsButton.setColor(0.1f, 0, 0, 1);
        sendCardsButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sendCardsIfPossible();
                return true;
            }
        });

        sendCardsButton.setName("send-cards-button");
        stage.addActor(sendCardsButton);
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


        game.batch.begin();
        game.font.setColor(0.5f, 0.5f, 1, 1);
        game.font.getData().setScale(2);
        List<Card> cardsToDisplay = gamePlayer.hand;
        cardsToDisplay.sort(new Card.cardComparator());
        List<Image> displayDeck = new ArrayList<>();

        Table cardDeck = new Table();
        cardDeck.setPosition(0, 0);
        cardDeck.setSize(perCardIncrementX*cardsToDisplay.size(), 135);

        for (Card c : cardsToDisplay){
            Image img = generateClickableCard(c.getCardType(), cardTemplates.get(c.getCardType()), c.picked);
            img.setPosition(baseX, baseY);
            displayDeck.add(img);
            String prioText = "Priority: " + c.getPriority();
            game.font.draw(game.batch, prioText, (float)baseX, (float)baseY-20);
            baseX += perCardIncrementX;
        }
        game.font.draw(game.batch, "WEEE", 0, 0);
        game.batch.end();
        // Add all images to stage
        displayDeck.forEach( (c) -> c.setName(""));
        displayDeck.forEach(cardDeck::addActor);
        cardDeck.setName("card-deck");
        stage.addActor(cardDeck);
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
        tableList.setName("player-list");
        stage.addActor(tableList);
    }
    /**
     * Load power down interactiable
     */
    private void loadPowerDown(){
        CheckBox powerDownCheckBox = new CheckBox("Power down next turn", game.skin);
        powerDownCheckBox.setName("powerdown-checkbox");
        powerDownCheckBox.setPosition(Gdx.graphics.getWidth()-375, 25);
        powerDownCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                gamePlayer.powerDown = powerDownCheckBox.isChecked();
            }
        });
        stage.addActor(powerDownCheckBox);
    }
    /**
     * Load chat input box with commands and message events
     */
    private void loadChatInputBox(){
        boolean alreadyInitialized = false;
        String chatInputName = "chat-input";
        for (Actor a : stage.getActors()){
            if (a.getName().equals(chatInputName)){
                alreadyInitialized = true;
            }
        }

        if (!alreadyInitialized){
            Color inputBoxColor = new Color(1f, 1f, 1f, 1);
            TextField inputBox = new TextField("", game.skin);
            inputBox.setWidth(375f);
            inputBox.setColor(inputBoxColor);
            inputBox.addListener(new InputListener(){
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    // Send message key is pressed
                    if (keycode == Input.Keys.ENTER){
                        // Check if message was intended as a command
                        boolean isCommand = false;

                                /*
                                Check if /c command
                                 */
                        // Commands have to be more than 2 characters long, else substring gives an exception
                        if (inputBox.getText().length()>2){
                            // If chat is a command
                            if (inputBox.getText().startsWith("/c")){
                                isCommand = true;
                                // Get content after /c indicator
                                String commandContent = inputBox.getText().substring(3);
                                System.out.println("Chat command entered: " + commandContent);
                                // Perform command
                                executeChatCommand(commandContent);
                            }
                        }

                                /*
                                Check if /h command
                                 */
                        // Send list of commands available if /h, only need to check if length is >1 here as there are no args or commands for /h
                        if (inputBox.getText().length()>1){
                            if (inputBox.getText().startsWith("/h")){
                                isCommand = true;
                                showChatHelpDialogue();
                            }
                        }

                        // Send message if no commands detected
                        if (!isCommand){
                            chat.sendMessage(inputBox.getText());
                        }
                        // Reset input text box after sending message
                        inputBox.setText("");
                        updateChat();
                    }
                    // Needed for inputhandler keyDown
                    return true;
                }
            });

            // Setup position and name of input box
            inputBox.setX(Gdx.graphics.getWidth()-inputBox.getWidth());
            inputBox.setY(200);
            inputBox.setName(chatInputName);

            stage.addActor(inputBox);
        }
    }
    /**
     * Get new messages that have been received from the network, get formatted table and add input box with listener.
     */
    private void updateChat(){
        // Get chat table from network
        chat.updateChat(network.messagesRecived);

        // Get index of table on stage
        int index = 0;
        // Update stage chat
        for (int i = 0; i<stage.getActors().size; i++){
            if (stage.getActors().get(i) != null){
                if (stage.getActors().get(i).getName().equals("chat")){
                    index = i;
                    break;
                }
            }
        }

        Table updatedChat = chat.getChatAsTable();
        updatedChat.setName("chat");
        // Update stage value chat
        stage.getActors().set(index, updatedChat);
    }





    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ----------    Chat command helpers    ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
    /**
     * Displays help dialogue in chat as a set internal messages
     */
    private void showChatHelpDialogue(){
        chat.sendInternalMessage("Only you can see the messages in green.\n", network);
        chat.sendInternalMessage("Commands:\n", network);
        chat.sendInternalMessage("\t FUNCITONAL", network);
        chat.sendInternalMessage("/h shows this dialogue.", network);
        chat.sendInternalMessage("/c connect <ip>", network);
        chat.sendInternalMessage("/c send-cards", network);
        chat.sendInternalMessage("/c reset-cards", network);
        chat.sendInternalMessage("/c clear", network);
        chat.sendInternalMessage("/c set-name <name>", network);
        chat.sendInternalMessage("/c show <message>", network);
        chat.sendInternalMessage("/c example-messages <all>", network);
        chat.sendInternalMessage("leave empty for local  ^\n", network);
        chat.sendInternalMessage("\t VISUAL", network);
        chat.sendInternalMessage("/c chat-color <r, g, b, black>", network);
        chat.sendInternalMessage("/c font-scale <font scale>", network);
        chat.sendInternalMessage("/c uwufy", network);
    }
    /**
     * Perform command in chat
     */
    private void executeChatCommand(String commandInput){
        CommandParser p = new CommandParser();

        // Get what command was input by user
        CommandParser.Command command = p.parseCommand(p.getCmd(commandInput));
        // Get the arguments for command (if any)
        String commandArgs = p.getArgs(commandInput);

        // Perform command
        switch (command){
            case SETNAME:
                chat.setName(commandArgs);
                chat.sendInternalMessage("Name changed to " + commandArgs + ".", network);
                break;
            case SETCOLOR:
                switch (commandArgs) {
                    case "r":
                        Color red = new Color(1, 0, 0, 1);
                        chat.chat.setChatColour(red);
                        chat.sendInternalMessage("Chat color set to red.", network);
                        break;
                    case "g":
                        Color green = new Color(0, 1, 0, 1);
                        chat.chat.setChatColour(green);
                        chat.sendInternalMessage("Chat color set to green.", network);
                        break;
                    case "b":
                        Color blue = new Color(0, 0, 1, 1);
                        chat.chat.setChatColour(blue);
                        chat.sendInternalMessage("Chat color set to blue.", network);
                        break;
                    case "black":
                        Color black = new Color(1, 1, 1, 1);
                        chat.chat.setChatColour(black);
                        chat.sendInternalMessage("Chat color set to black.", network);
                        break;
                    default:
                        System.out.println("Invalid colour.");
                        break;
                }
                break;
            case SETFONTSTCALE:
                float scale = Float.parseFloat(commandArgs);
                chat.chat.setChatFontSize(scale);
                chat.sendInternalMessage("Font scale set to " + scale + ".", network);
                break;
            case INVALID:
                chat.sendInternalMessage("Entered invalid command.", network);
                break;
            case UWU:
                Uwufier uwu = new Uwufier(network.messagesRecived); // TODO need to add non uwu backlog in network so can be reverted
                network.messagesRecived = uwu.postUwudMessages;
                chat.sendInternalMessage("UWUfication complete!", network);
                break;
            case CLEAR:
                network.messagesRecived.clear();
                chat.sendInternalMessage("Chat cleared.", network);
                break;
            case SENDINTERNAL:
                chat.sendInternalMessage(commandArgs, network);
                break;
            case EXAMPLEMESSAGES:
                if (commandArgs.equals("all")){
                    chat.sendMessage("Hello! This is a message.");
                    chat.sendMessage("I am playing roborally. ");
                    chat.sendMessage("This is alot of fun!");
                    chat.sendMessage("This game is impressive. Good job!");
                }else {
                    chat.sendInternalMessage("Hello! This is a message.", network);
                    chat.sendInternalMessage("I am playing roborally. ", network);
                    chat.sendInternalMessage("This is alot of fun!", network);
                    chat.sendInternalMessage("This game is impressive. Good job!", network);
                }
                chat.sendInternalMessage("Sent example messages.", network);
                break;
            case CONNECT:
                if (commandArgs.contains(" ")){
                    chat.sendInternalMessage("IP cannot contain spaces.", network);
                }else {
                    game.setScreen(new GameScreen(game, chat.cData.name, network));
                }
            case SENDCARDS:
                if (sendCardsIfPossible()){
                    chat.sendInternalMessage("Cards sent!", network);
                }else{
                    chat.sendInternalMessage("Could not send cards.", network);
                    chat.sendInternalMessage("Remember to select 5 cards.", network);
                }
                break;
            case RESETCARDS:
                resetCardChoices();
                chat.sendInternalMessage("Card selection reset.", network);
                break;
            case SELECTCARD:
                                                /*  this is buggy and doesn't work right now
                                                if (gamePlayer.state == GamePlayer.PLAYERSTATE.PICKING_CARDS && gamePlayer.chosenCards.size()<5){
                                                    int index = Integer.parseInt(p.getArgs(commandContent))-1;
                                                    if (index < 0 || index > 8){
                                                        chat.sendInternalMessage("Can only select cards 1-9.", network);
                                                    }else {
                                                        Card c = new Card();
                                                        c.setCardType(gamePlayer.hand.get(index).getCardType());
                                                        gamePlayer.chooseCards(index);
                                                        gamePlayer.chosenCards.add(c);
                                                        c.picked = true;
                                                        clearNonInteractiveStageElements();
                                                        loadActorsInOrder();
                                                    }
                                                }else{
                                                    chat.sendInternalMessage("Cannot pick cards right now.", network);
                                                }*/
                chat.sendInternalMessage("Card selection in chat disabled.", network);
                break;
            default:
                break;
        }
    }





    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------   Libgdx methods   --------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
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






    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------ Map display methods--------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
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
        }
    }
    /**
     * Loads all the laser textures from the map-class onto the board
     */
    public void loadLasers() {
        for (int x = 0; x < Game.BOARD_X; x++) {
            for (int y = 0; y < Game.BOARD_Y; y++) {
                ((TiledMapTileLayer) game.tiledMap.getLayers().get("LaserV")).setCell(x, y, laserToTile(x, y, true));
                ((TiledMapTileLayer) game.tiledMap.getLayers().get("LaserH")).setCell(x, y, laserToTile(x, y, false));
            }
        }
    }
    /**
     @param x the x position of the tile
      * @param y the y position of the tile
     * @return the correct texture to put in the tile
     */
    public TiledMapTileLayer.Cell laserToTile(int x, int y, boolean isVert) {

        if (map.laserLayer[x][y][0] == 1 && isVert) return singleVertical;
        if (map.laserLayer[x][y][0] == 2 && isVert) return doubleVertical;
        if (map.laserLayer[x][y][1] == 1 && !isVert) return singleHorizontal;
        if (map.laserLayer[x][y][1] == 2 && !isVert) return doubleHorizontal;
        if (map.laserLayer[x][y][2] == 1 && isVert) return singleVertical;
        if (map.laserLayer[x][y][2] == 2 && isVert) return doubleVertical;
        if (map.laserLayer[x][y][3] == 1 && !isVert) return singleHorizontal;
        if (map.laserLayer[x][y][3] == 2 && !isVert) return doubleHorizontal;
        else return null;
    }
    /**
     * Gets player locations and states from map and sets tiledmaplayer cells to correct texture
     */
    public void translatePlayerLayer(){
        for (int x = 0; x< map.playerLayer.length; x++){
            for (int y = 0; y< map.playerLayer[x].length; y++){

                // Initialize empty cell
                TiledMapTileLayer.Cell currentCell = new TiledMapTileLayer.Cell();

                if(map.playerLayer[x][y].config != null){

                    // Generate cell from config if config for tile found
                    PlayerConfig c = map.playerLayer[x][y].config;
                    TextureRegion textReg = new TextureRegion(CharacterCustomizer.generatePlayerTexture(c.getImage(), c.getMainColor()));
                    StaticTiledMapTile currentTile = new StaticTiledMapTile(textReg);
                    // Set tile to empty cell
                    currentCell.setTile(currentTile);
                }

                switch (map.playerLayer[x][y].state){
                    case PLAYERNORMAL:
                    case PLAYERSELFNORMAL:
                    case PLAYERSELFWON:
                    case PLAYERWON:
                        playerLayer.setCell(x, y, currentCell);
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
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------     UI methods     --------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
    /**
     * Clears all stage elements that are not interactive
     * When the stage clears, the chat input box clears, and the text you write disappears if you haven't sent t yet
     * Does not clear things like chat input box, as stage clears happens without user interaction and clearing the message all the time would be annoying
     */
    private void clearNonInteractiveStageElements(){
        List<String> itemsNotToClear = new ArrayList<>();
        itemsNotToClear.add("chat-input");

        stage.getActors().forEach( (a) -> {
                    for (String s : itemsNotToClear){
                        if (!a.getName().equals(s)){
                            a.clear();
                        }
                    }
                }
        );
    }


    /**
     * Poll updates from the network client that needs to be updated to local session in real time
     */
    private void pollUiUpdates(){
        // Force cards to update when new cards have been received
        if(gamePlayer.newCardsDelivered){
            clearNonInteractiveStageElements();
            loadActorsInOrder();

            // Check if any null actors are found, clear them if so
            try {
                stage.getActors().forEach((n) -> {
                    if (n == null) {
                        stage.getActors().removeValue(null, true);
                    }
                });
            } catch (Exception e) {
                System.out.println("Not able to remove null value from getActors, exception " + e);
            }
            gamePlayer.newCardsDelivered = false;
        }

        if(network.messagesRecived.size() > networkChatBacklogSize){
            stage.getActors().forEach( (a) -> {
                        if (a.getName().equals("chat")){
                            a.clear();
                        }
                    }
            );
            updateChat();
            networkChatBacklogSize = network.messagesRecived.size();
        }
    }






    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ------------------Map internal methods--------- ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
    /**
     * Adds a permanent laser shooter to the map if there is a laser shooter in the laser cell
     * @param laserCell the cell you wish to check for lasers
     * @param i its i position
     * @param j its j position
     */
    private void setLaserDirection(TiledMapTileLayer.Cell laserCell, int i, int j) {
        // NORTH, EAST, SOUTH, WEST
        if (laserCell.getTile().getId() == 38) map.laserShooters.add(new Map.LaserShooter(Direction.EAST, 1, i, j, true));
        if (laserCell.getTile().getId() == 46) map.laserShooters.add(new Map.LaserShooter(Direction.WEST, 1, i, j, true));
        if (laserCell.getTile().getId() == 95) map.laserShooters.add(new Map.LaserShooter(Direction.WEST, 2, i, j, true));
        if (laserCell.getTile().getId() == 93) map.laserShooters.add(new Map.LaserShooter(Direction.EAST, 2, i, j, true));
    }
    /**
     * Sets the correct values for the wall in the wall layer in map
     * @param wallCell the wall cell you wish to check
     * @param i the x position of the wall cell
     * @param j the y position of the wall cell
     */
    private void setWallDirections(TiledMapTileLayer.Cell wallCell, int i, int j){
        //TODO a lot of these are lacking

        // NORTH, EAST, SOUTH, WEST. This is where the wall is placed
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
            map.beltLayer[i][j] = new Map.BeltInformation(Direction.EAST, false, 0);
        }
        if (wallCell.getTile().getId() == 11) {
            map.wallLayer[i][j] = new boolean[] {false, false, true, false};
            map.beltLayer[i][j] = new Map.BeltInformation(Direction.NORTH, false, 0);
        }
        if (wallCell.getTile().getId() == 10) {
            map.wallLayer[i][j] = new boolean[] {false, true, false, false};
            map.beltLayer[i][j] = new Map.BeltInformation(Direction.WEST, false, 0);
        }
        if (wallCell.getTile().getId() == 9) {
            map.wallLayer[i][j] = new boolean[] {true, false, false, false};
            map.beltLayer[i][j] = new Map.BeltInformation(Direction.SOUTH, false, 0);
        }
        //if (wallCell.getTile().getId() == 11) map.wallLayer[i][j] = new boolean[] {true, false, false, true};

    }
    /**
     *  Fills the belt-layer in the map class with the correct belt information
     * @param beltCell the cell containing the belt
     * @param i the x position of the cell containing the belt
     * @param j the y position of the cell containing the belt
     */
    private void setBeltInformation(TiledMapTileLayer.Cell beltCell, int i, int j){
        if (beltCell.getTile().getId() == 50) map.beltLayer[i][j] = new Map.BeltInformation(Direction.SOUTH, false, 0);
        if (beltCell.getTile().getId() == 21) map.beltLayer[i][j] = new Map.BeltInformation(Direction.SOUTH, true, 0);
        if (beltCell.getTile().getId() == 49) map.beltLayer[i][j] = new Map.BeltInformation(Direction.NORTH, false, 0);
        if (beltCell.getTile().getId() == 41) map.beltLayer[i][j] = new Map.BeltInformation(Direction.EAST, false, -1, Direction.NORTH);
        if (beltCell.getTile().getId() == 52) map.beltLayer[i][j] = new Map.BeltInformation(Direction.EAST, false, 0);
        if (beltCell.getTile().getId() == 86) map.beltLayer[i][j] = new Map.BeltInformation(Direction.SOUTH, true, 1, Direction.EAST);
        if (beltCell.getTile().getId() == 51) map.beltLayer[i][j] = new Map.BeltInformation(Direction.WEST, false, 0);
        if (beltCell.getTile().getId() == 22) map.beltLayer[i][j] = new Map.BeltInformation(Direction.WEST, true, 0);
        if (beltCell.getTile().getId() == 14) map.beltLayer[i][j] = new Map.BeltInformation(Direction.EAST, true, 0);
        if (beltCell.getTile().getId() == 13) map.beltLayer[i][j] = new Map.BeltInformation(Direction.NORTH, true, 0);
        if (beltCell.getTile().getId() == 77) map.beltLayer[i][j] = new Map.BeltInformation(Direction.NORTH, true, 1, Direction.WEST);
        if (beltCell.getTile().getId() == 34) map.beltLayer[i][j] = new Map.BeltInformation(Direction.WEST, false, -1, Direction.SOUTH);
    }





    /**
     --------- ------------------ ------------------ ------------------ ---------
     --------- ----------  Random getters/unspecified   ------------------
     --------- ------------------ ------------------ ------------------ ---------
     */
    /**
     * gets the spawn points from the map and puts them in the map class //TODO move this to getBoardElementPositions
     * @param startLayer the start layer
     */
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
    /**
     * Loops through all the tiles in the board, and fills up the map class accordingly with all the board elements
     * @param tiledMap the tiled map you wish to load the board elements from
     */
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
     * Reset all card choices
     */
    public void resetCardChoices(){
        for (Card c : gamePlayer.hand){
            c.picked = false;
        }
        gamePlayer.chosenCards.clear();
        clearNonInteractiveStageElements();
        loadActorsInOrder();
    }
    /**
     * Sends cards to host if all requirements for sending cards are met.
     */
    private boolean sendCardsIfPossible(){
        if (gamePlayer.chosenCards.size() >= 5) {
            if (network.isHost) {
                if (((GameHost) gamePlayer).allCardsReady()) {
                    System.out.println("Cards are being sent to processing. Stage size before deck clear: " + stage.getActors().size);
                    clearNonInteractiveStageElements();
                    for (Card c : gamePlayer.hand){
                        for (Card d : gamePlayer.chosenCards){
                            if (c.getCardType() == d.getCardType() && c.picked){
                                c.setCardType(CardType.NONE);
                            }else {
                                gamePlayer.discard.add(c);
                            }
                        }
                    }
                    gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
                    gamePlayer.registerChosenCards();
                    return true;
                } else {
                    System.out.println("Not all players have delivered their cards yet! Cannot process cards yet.");
                    return false;
                }
            } else {
                System.out.println("Cards are being sent to processing. Stage size before deck clear: " + stage.getActors().size);
                clearNonInteractiveStageElements();
                for (Card c : gamePlayer.hand){
                    for (Card d : gamePlayer.chosenCards){
                        if (c.getCardType() == d.getCardType() && c.picked){
                            c.setCardType(CardType.NONE);
                        }else {
                            gamePlayer.discard.add(c);
                        }
                    }
                }
                gamePlayer.state = GamePlayer.PLAYERSTATE.SENDING_CARDS;
                gamePlayer.registerChosenCards();
                return true;
            }
        }
        return false;
    }
}