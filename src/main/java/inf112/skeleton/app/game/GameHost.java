package inf112.skeleton.app.game;

import com.badlogic.gdx.math.GridPoint2;
import com.esotericsoftware.kryonet.Connection;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.Collections;
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
    public GameHost(NetworkHost network, String name){
        // Add cards to deck
        super();
        host = network;
        host.host = this;

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();

        initializeClientPlayerTokens();
        initializeHostPlayerToken(name);
    }

    // Game map
    private Map map;

    public boolean isShowingCards = false;
    private List<List<Card>> cardsPerPlayerTurn = new ArrayList<>();
    private List<Card> currentCardListBeingProcessed = new ArrayList<>();
    private HashMap<Card, PlayerToken> cardPlayerTokenMap = new HashMap<>();
    private int cardsProcessedPerRound = 5;
    private int currentCardRound = 1;
    private long timeSinceLastCardProcessed = System.currentTimeMillis();
    private long pauseBetweenEachCardProcess = 300;

    private NetworkHost host;

    // Has all clients (which contain connnection ID's) as well as their tokens
    public HashMap<Integer, PlayerToken> clientPlayers;

    /**
     * Create tokens for the host
     */
    private void initializeClientPlayerTokens(){
        // Set token character states to normal default and give connection ID to token
        for (Connection c : host.connections){
            PlayerToken token = new PlayerToken();
            token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
            token.ID = c.getID();
            clientPlayers.put(c.getID(), token);
        }

    }
    /**
     * Create token for the host
     */
    private void initializeHostPlayerToken(String name) {
        // This is so processCards also includes the host
        PlayerToken token = new PlayerToken();
        token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
        token.name = name;
        token.ID = NetworkHost.hostID;
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
        hand.removeAll(Collections.singleton(null));
        discard.addAll(hand);

        //Update the clientCards in host
        host.playerCards.put(NetworkHost.hostID, chosenCards);
        checkCards();
    }

    public void checkCards(){
        if (host.playerCards.keySet().size() == host.connections.length+1) {
            processCards();

            // Reset the chosen cards and the hand
            chosenCards = new ArrayList<>();
            hand = new ArrayList<>();
            host.playerCards.clear();
            drawCards();
        }
    }

    /**
     * Draw cards to deck for both host and clients
     */
    public void drawCards(){
        host.promptCardDraw();  // tell clients to draw cards
        drawCardsFromDeck(); // draw host cards
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
        map.loadPlayers(wrapper());
    }

    /**
     * Process card selection from all clients and host
     */
    private void processCards() {
        System.out.println("-------------------------------------");
        System.out.println("Preparing card selections...");
        // iterator i is same as client connection id
        for (int i = 0; i<cardsProcessedPerRound; i++){
            List<Card> cardList = new ArrayList<>();
            for (int key : clientPlayers.keySet()){
                // Get next card for the given player and pop it so it can be played
                List<Card> cards = host.playerCards.get(key);

                // get first card from players cards
                Card currentCard = cards.remove(0);

                // Add card to list of all clients cards
                cardList.add(currentCard);

                // Map card to client
                cardPlayerTokenMap.put(currentCard, clientPlayers.get(key));
            }
            // Sort cards by card priority
            Collections.sort(cardList, new Card.cardComparator());

            // Add first card from each players card selections to cardsPerPlayerTurn
            // This way all cards have now been mapped to each player, but are now saved as which order the cards will be processed
            // as cards will be processed one at a time per player, then the next card per player etc.
            cardsPerPlayerTurn.add(cardList);

            System.out.println(cardsPerPlayerTurn.size()+". card selection ready.");
        }
        System.out.println("Card selections for this turn ready.");
        System.out.println("-------------------------------------");
        // Start processing each card sequentially
        isShowingCards = true;
    }

    private void resetPlayerTokens(){
        // Reset which players have died this turn, so that they can keep playing
        for (PlayerToken player: clientPlayers.values()) {
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

        if (System.currentTimeMillis() >= timeSinceLastCardProcessed+pauseBetweenEachCardProcess){
            // If current Nth card list empty, start next round of cards
            if (currentCardListBeingProcessed.size() == 0){
                getNthSelectionFromEachPlayer();
                System.out.println("Number of cards being processed this round...." + currentCardListBeingProcessed.size());

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
        if (!cardPlayerTokenMap.get(card).diedThisTurn && !cardPlayerTokenMap.get(card).isDead()){
            // Move the clients player token
            resolveCard(card, cardPlayerTokenMap.get(card));
        }

        System.out.println("-------------------------------------");
        System.out.println("Processing card selection round nr. "+currentCardRound);

        map.loadPlayers(wrapper());
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

        // Should maybe be another place
        resetPlayerTokens();
    }

    /**
     * Initialize wrapper and add player tokens to wrapper for transfer
     * @return initialized wrapper
     */
    private NetworkDataWrapper wrapper() {
        NetworkDataWrapper wrapper = new NetworkDataWrapper();
        wrapper.PlayerTokens.addAll(clientPlayers.values());
        return wrapper;
    }

    /**
     * Resolve card moves on token
     *
     * @param card card to resolve
     * @param token token to move
     */
    private void resolveCard(Card card, PlayerToken token) {
        switch (card.getCardType()) {
            case FORWARDONE:
                movePlayer(token, 1, token.getDirection());
                break;
            case FORWARDTWO:
                movePlayer(token, 2, token.getDirection());
                break;
            case FORWARDTHREE:
                movePlayer(token, 3, token.getDirection());
                break;
            case BACK_UP:
                switch (token.getDirection()) {
                    case NORTH:
                        movePlayer(token, 1, PlayerToken.Direction.SOUTH);
                        break;
                    case SOUTH:
                        movePlayer(token, 1, PlayerToken.Direction.NORTH);
                        break;
                    case WEST:
                        movePlayer(token, 1, PlayerToken.Direction.EAST);
                        break;
                    case EAST:
                        movePlayer(token, 1, PlayerToken.Direction.WEST);
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
    private void movePlayer(PlayerToken player, int dist, PlayerToken.Direction direction) {
        for(int i = 0; i < dist; i++) {
            GridPoint2 wouldEndUp = player.wouldEndUp(direction);

            if (wouldEndUp.x < 0 || wouldEndUp.x >= Game.BOARD_X || wouldEndUp.y < 0 || wouldEndUp.y >= Game.BOARD_Y) {
                player.died();
                break;
            }
            else if (map.isHole(wouldEndUp.x, wouldEndUp.y)) {
                System.out.println("Player " + player.name + " died");
                player.died();
                break;
            }
            else if (map.playerLayer[wouldEndUp.x][wouldEndUp.y].state != PlayerToken.CHARACTER_STATES.NONE) {
                // TODO Fix this maybe?
                for (PlayerToken opponent : clientPlayers.values()) {
                    if (opponent.position.x == wouldEndUp.x && opponent.position.y == wouldEndUp.y) {
                        opponent.move(direction);
                    }
                }
                player.move(direction);
                // Don't think we need this, but better safe than sorry. Future proof!
                if (player.diedThisTurn) break;
            }
            else {
                player.move(direction);
                if (player.diedThisTurn) break;
            }

        }
    }
}
