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
    public GameHost(NetworkHost network){
        // Add cards to deck
        super();
        host = network;
        host.host = this;

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();

        initializePlayerTokens();
    }

    // Game map
    private Map map;

    private NetworkHost host;

    // Has all clients (which contain connnection ID's) as well as their tokens
    private HashMap<Integer, PlayerToken> clientPlayers;

    /**
     * Create tokens for each connected client as well as the host
     */
    private void initializePlayerTokens(){
        // Set token character states to normal default and give connection ID to token
        for (Connection c : host.connections){
            PlayerToken token = new PlayerToken();
            token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
            token.ID = c.getID();
            clientPlayers.put(c.getID(), token);
        }
        // This is so processCards also includes the host
        PlayerToken token = new PlayerToken();
        token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
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
        int cardsProcessedPerRound = 5;
        HashMap<Card, PlayerToken> cardPlayerTokenMap = new HashMap<>();
        // iterator i is same as client connection id
        for (int i = 0; i<cardsProcessedPerRound; i++){
            List<Card> cardList = new ArrayList<>();
            for (int key : clientPlayers.keySet()){
                // Check if the player is dead
                if (clientPlayers.get(key).diedThisTurn == true || clientPlayers.get(key).isDead()){
                    continue;
                }
                // Get next card for the given player and pop it so it can be played
                List<Card> cards = host.playerCards.get(key);
                Card currentCard = cards.remove(0);

                cardList.add(currentCard);
                cardPlayerTokenMap.put(currentCard, clientPlayers.get(key));
            }
            Collections.sort(cardList, new Card.cardComparator());

            for (Card card : cardList) {
                System.out.println("This card has priority " + card.getPriority());
                // Move the clients player token
                resolveCard(card, cardPlayerTokenMap.get(card));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                map.loadPlayers(wrapper());
                host.sendMapLayerWrapper(wrapper());
            }
            System.out.println();
        }

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
                System.out.println("Player " + player.ID + " died");
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
