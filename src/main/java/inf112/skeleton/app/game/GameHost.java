package inf112.skeleton.app.game;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.Direction;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * GameHost contains all game logic like cards, and moving players according to their selected cards.
 * This class is used as a central point of synchronizing maps, as GameClients ask this class for map updates
 * whenever the map is to be displayed.
 */
public class GameHost extends GamePlayer {

    /**
     * @param network connection to be used in game
     */
    public GameHost(NetworkHost network){
        // Add cards to deck
        super();
        host = network;
        host.host = this;

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();
    }

    // Game map
    public Map map;

    public boolean isShowingCards = false;
    private final List<List<Card>> cardsPerPlayerTurn = new ArrayList<>();
    private final List<Card> currentCardListBeingProcessed = new ArrayList<>();
    private final HashMap<Card, PlayerToken> cardPlayerTokenMap = new HashMap<>();
    private final int cardsProcessedPerRound = 5;
    private int currentCardRound = 1;
    private long timeSinceLastCardProcessed = System.currentTimeMillis();

    public NetworkHost host;

    // Has all clients (which contain connnection ID's) as well as their tokens
    public HashMap<Integer, PlayerToken> clientPlayers;

    /**
     * TODO rewrite this to what it actually does now
     * which is put them at their spawn points
     */
    public PlayerToken initializePlayerPos(PlayerToken player){
        GridPoint2 pos = map.spawnPoints.remove(0);

        player.spawnLoc.x = pos.x;
        player.spawnLoc.y = pos.y;

        player.position.x = pos.x;
        player.position.y = pos.y;

        if (player.position.y != 0) {
            player.playerDirection = Direction.SOUTH;
        }

        return player;
    }
    /**
     * Create token for the host
     */
    public void initializeHostPlayerToken(String name) {
        // This is so processCards also includes the host
        PlayerToken token = new PlayerToken();
        token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
        token.name = name;
        token.ID = NetworkHost.hostID;
        token.setConfig(CharacterCustomizer.loadCharacterConfigFromFile());
        token = initializePlayerPos(token);
        clientPlayers.put(NetworkHost.hostID, token);
    }

    /**
     * Registers the client's chosen cards, then clears the host's storage of
     * clients card so that the clientCards map can be used again next turn.
     */
    @Override
    public void registerChosenCards() {
        // Add the cards (prematurely for now) to the discard pile)
        discard.addAll(chosenCards);

        // Remove all nulls, aka drawn cards
        for (Card card : hand) {
            if (card.getCardType() != CardType.NONE) discard.add(card);
        }

        //Update the clientCards in host
        host.playerCards.put(NetworkHost.hostID, chosenCards);
        clientPlayers.get(NetworkHost.hostID).powerDown = powerDown;
        checkCards();
    }

    /**
     *
     * @return true if the host can send its card, false otherwise
     */
    public boolean allCardsReady(){
        return host.playerCards.keySet().size() == host.alivePlayers.size()-1; // host doesn't deliver cards until after this check
    }

    /**
     * Checks that all players have sent their cards, and if so, processes the cards
     */
    public void checkCards(){
        if (host.playerCards.keySet().size() == host.alivePlayers.size()) {
            System.out.println("Processing cards!");
            processCards();

            // Reset the chosen cards and the hand
            chosenCards = new ArrayList<>();
            hand = new ArrayList<>();
            host.playerCards.clear();
        }
    }

    /**
     * Draw cards to deck for both host and clients
     */
    public void drawCards(){
        host.promptCardDraw();  // tell clients to draw cards
    }

    /**
     * Return host's version of map
     * @param map for clients
     * @return host's map
     */
    @Override
    public Map updateMap(Map map) {
        return this.map;
    }

    /**
     * Updates the map in the host, loads it for both clients and host
     * so that the map is properly loaded when the game starts.
     * @param map map
     */
    @Override
    public void setMap(Map map){
        this.map = map;
        this.map.setID(NetworkHost.hostID);
        host.sendMapLayerWrapper(wrapper());
        map.loadPlayers(wrapper().PlayerTokens);
    }

    /**
     * Handles end of turn logic. Belt moving, lasers, rotating, repairs, etc.
     */
    public void endOfTurn(){
        List<Integer> tokensToRemove = new ArrayList<>();
        for (Integer key : clientPlayers.keySet()){
            PlayerToken token = clientPlayers.get(key);
            if (token.diedThisTurn) continue;

            for (int i = 0; i < 2; i++) {
                Map.BeltInformation belt = map.beltLayer[token.getX()][token.getY()];
                if (belt != null) {
                    if (i == 0 && belt.isExpress) {
                        movePlayer(token, 1, belt.beltDirection, false);
                    }
                    else if(i == 1) {
                        movePlayer(token, 1, belt.beltDirection, false);
                    }
                }
                if (token.diedThisTurn) break;
                //TODO Need to make sure that they do move if one player is on a belt directly in front of another

                //TODO There's a bug here, when you're pushed off the map
                Map.BeltInformation nextBelt = map.beltLayer[token.getX()][token.getY()];
                if (nextBelt != null && nextBelt.beltRotationDirection != null) {
                    assert belt != null;
                    if (nextBelt.beltRotationDirection == belt.beltDirection) {
                        if (nextBelt.beltRotation == -1) {
                            token.rotate(CardType.TURNLEFT);
                        }
                        if (nextBelt.beltRotation == 1) {
                            token.rotate(CardType.TURNRIGHT);
                        }
                    }
                }
            }
            //These are really ugly //TODO FIX
            if (token.diedThisTurn) break;
            int rotation = map.isGear(token.position.x, token.position.y);
            if (rotation == 1) {
                System.out.println(token.name + " is on a gear!");
                token.rotate(CardType.TURNRIGHT);
            }
            if (rotation == 2){
                System.out.println(token.name + " is on a gear!");
                token.rotate(CardType.TURNLEFT);
            }
        }
        map.clearLasers();
        map.shootLasers(wrapper());
        //TODO If a player is moved off the map by a belt, it's still hit by a laser
        //TODO Does this make sense
        for (Integer key : clientPlayers.keySet()){
            PlayerToken token = clientPlayers.get(key);
            if (token.diedThisTurn) {
                continue;
            }

            int lasers = 0;

            for (int i = 0; i < 4; i++) {
                lasers += map.laserLayer[token.getX()][token.getY()][i];
            }
            System.out.println("Took " + lasers + " damage");
            token.damage += lasers;
            if (token.damage > 9) {
                token.died();
                continue;
            }

            if (map.isRepair(token.getX(), token.getY())){
                System.out.println(token.name + " has " + token.damage + " damage tokens");
                if (token.damage > 0) token.damage--;
                System.out.println(token.name + " healed, and now has " + token.damage + " damage tokens");
            }

            //TODO ADD FLAG CHECK HERE
        }

        for (Integer key : clientPlayers.keySet()) {
            if (clientPlayers.get(key).isPermanentlyDestroyed()) {
                tokensToRemove.add(key);

            }
        }

        for (Integer key : tokensToRemove) {
            clientPlayers.remove(key);
            host.alivePlayers.remove(key);
        }

        //Do this here because we don't have a well defined start of turn...
        for (PlayerToken token : clientPlayers.values()) {
            if (token.powerDown) {
                token.damage = 0;
            }
        }

        drawCards();
    }
    /**
     * Process card selection from all clients and host
     */
    private void processCards() {
        //System.out.println("-------------------------------------");
        //System.out.println("Preparing card selections...");
        // iterator i is same as client connection id

        for (int i = 0; i<cardsProcessedPerRound; i++){
            List<Card> cardList = new ArrayList<>();
            for (int key : clientPlayers.keySet()){
                // Get next card for the given player and pop it so it can be played
                List<Card> cards = host.playerCards.get(key);

                if (!cards.isEmpty()) {
                    // get first card from players cards
                    Card currentCard = cards.remove(0);

                    // Add card to list of all clients cards
                    cardList.add(currentCard);

                    // Map card to client
                    cardPlayerTokenMap.put(currentCard, clientPlayers.get(key));
                }
            }
            // Sort cards by card priority
            cardList.sort(new Card.cardComparator());

            // Add first card from each players card selections to cardsPerPlayerTurn
            // This way all cards have now been mapped to each player, but are now saved as which order the cards will be processed
            // as cards will be processed one at a time per player, then the next card per player etc.
            cardsPerPlayerTurn.add(cardList);

            System.out.println(cardsPerPlayerTurn.size()+". card selection ready.");
        }
        //System.out.println("Card selections for this turn ready.");
        //System.out.println("-------------------------------------");
        // Start processing each card sequentially
        isShowingCards = true;
    }

    private void findPlayerRespawnLocation(PlayerToken player) {
        int x = player.spawnLoc.x;
        int y = player.spawnLoc.y;
        int i = 0;
        while (map.hasPlayer(x, y)) {
            if (Map.isInBounds(x-i, y) && !map.hasPlayer(x-i, y)) {
                x = x-i;
                break;
            }
            if (Map.isInBounds(x+i, y) && !map.hasPlayer(x+i, y)) {
                x = x+i;
                break;
            }
            if (Map.isInBounds(x, y+i) && !map.hasPlayer(x, y+i)) {
                y = y+i;
                break;
            }

            if (Map.isInBounds(x, y-i) && !map.hasPlayer(x, y-i)) {
                y = y-i;
                break;
            }
            i++;
        }
        player.position.x = x;
        player.position.y = y;
    }

    /**
     * Rests which playertokens died this turn, and checks if anyone won
     */
    //This is public for testing purposes
    public void resetPlayerTokens(){
        // Reset which players have died this turn, so that they can keep playing
        for (PlayerToken player: clientPlayers.values()) {
            if (player.diedThisTurn) findPlayerRespawnLocation(player);
            player.diedThisTurn = false;
        }
        List<PlayerToken> playerTokens = new ArrayList<>(clientPlayers.values());
        map.registerFlags(playerTokens);

        PlayerToken winner = map.hasWon(playerTokens);

        if(winner != null) {
            host.sendWinner(winner);
        }
    }

    /**
     * Handle single selection of a card for all players
     */
    public void handleSingleCardRound(){
        map.clearLasers();
        long pauseBetweenEachCardProcess = 300;
        if (System.currentTimeMillis() >= timeSinceLastCardProcessed+ pauseBetweenEachCardProcess){
            // If current Nth card list empty, start next round of cards
            if (currentCardListBeingProcessed.size() == 0){
                getNthSelectionFromEachPlayer();
                //System.out.println("Number of cards being processed this round...." + currentCardListBeingProcessed.size());

                // Increment N, so next round the card list becomes each N+1-th card selection
                // e.g. last round was Card 1 for each player, next is card 2 for each player
                currentCardRound++;
                // Doesn't execute until time threshold has passed
            }else {
                handleSinglePlayerCard(currentCardListBeingProcessed.remove(0));
            }
        }

        // Reset card delay variables for next turn
        if (currentCardRound >= cardsProcessedPerRound+2){
            endOfTurn();
            resetCardDelayVariables();
        }
    }

    /**
     * Get Nth selection of cards from each player
     */
    private void getNthSelectionFromEachPlayer(){
        if (cardsPerPlayerTurn.size() > 0) {
            currentCardListBeingProcessed.addAll(cardsPerPlayerTurn.remove(0));
        }
    }

    /**
     * Resolve single card and reset card process time
     * @param card card to handle
     */
    private void handleSinglePlayerCard(Card card){
        // Check if the player is dead
        if (!cardPlayerTokenMap.get(card).diedThisTurn && !cardPlayerTokenMap.get(card).isPermanentlyDestroyed()){
            // Move the clients player token
            resolveCard(card, cardPlayerTokenMap.get(card));
        }

        map.loadPlayers(wrapper().PlayerTokens);
        host.sendMapLayerWrapper(wrapper());
        timeSinceLastCardProcessed = System.currentTimeMillis();
    }

    /**
     * Prepare delay variables for next turn by resetting to default values
     */
    private void resetCardDelayVariables(){
        isShowingCards = false;
        cardsPerPlayerTurn.clear();
        currentCardListBeingProcessed.clear();
        cardPlayerTokenMap.clear();
        currentCardRound = 1;

        resetPlayerTokens();
    }

    /**
     * Initialize wrapper and add player tokens to wrapper for transfer
     * @return initialized wrapper
     */
    public NetworkDataWrapper wrapper() {
        NetworkDataWrapper wrapper = new NetworkDataWrapper();
        wrapper.PlayerTokens.addAll(clientPlayers.values());
        wrapper.laserLayer = map.laserLayer;
        return wrapper;
    }

    /**
     * Resolve card moves on token
     *
     * @param card card to resolve
     * @param token token to move
     */
    public void resolveCard(Card card, PlayerToken token) {
        switch (card.getCardType()) {
            case FORWARDONE:
                movePlayer(token, 1, token.getDirection(), true);
                break;
            case FORWARDTWO:
                movePlayer(token, 2, token.getDirection(), true);
                break;
            case FORWARDTHREE:
                movePlayer(token, 3, token.getDirection(), true);
                break;
            case BACK_UP:
                switch (token.getDirection()) {
                    case NORTH:
                        movePlayer(token, 1, Direction.SOUTH, true);
                        break;
                    case SOUTH:
                        movePlayer(token, 1, Direction.NORTH, true);
                        break;
                    case WEST:
                        movePlayer(token, 1, Direction.EAST, true);
                        break;
                    case EAST:
                        movePlayer(token, 1, Direction.WEST, true);
                        break;
                }
                break;
            case TURNLEFT:
                token.rotate(CardType.TURNLEFT);
                break;
            case TURNRIGHT:
                token.rotate(CardType.TURNRIGHT);
                break;
            case UTURN:
                token.rotate(CardType.UTURN);
                break;
        }
    }

    /**
     * Move the player according to the rules of the game
     * @param player the player that should move
     * @param dist the number of steps you want to take
     * @param direction the direction you want to move, usually player.getDirection()
     */
    private boolean movePlayer(PlayerToken player, int dist, Direction direction, boolean shouldPush) {
        if (dist == 0) {
            return false;
        }
        GridPoint2 wouldEndUp = player.wouldEndUp(direction);

        if (!map.canGo(player.position.x, player.position.y, direction)){
            return false;
        }
        else if (map.wouldDie(wouldEndUp.x, wouldEndUp.y)) {
            player.died();
            return true;
        }
        else if (map.hasPlayer(wouldEndUp.x, wouldEndUp.y)) {
            if (!shouldPush) {
                return false;
            }
            // I.e if the player is being pushed into a wall
            boolean didOppMove = false;
            //Shady way to get a hold of the player on the tile
            for (PlayerToken opponent : clientPlayers.values()) {
                if (opponent != player && opponent.position.x == wouldEndUp.x && opponent.position.y == wouldEndUp.y) {
                    didOppMove = movePlayer(opponent, 1, direction, true);
                }
            }
            if (didOppMove) player.move(direction);
                // Don't think we need this, but better safe than sorry. Future proof!
            if (player.diedThisTurn) return didOppMove;
        }
        else {
            player.move(direction);
            if (player.diedThisTurn) return true;
        }
        map.loadPlayers(wrapper().PlayerTokens);
        //host.sendMapLayerWrapper(wrapper());
        movePlayer(player, dist-1, direction, shouldPush);
        return true;
    }

}
