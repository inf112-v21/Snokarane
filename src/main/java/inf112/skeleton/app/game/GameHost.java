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

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();

        initializePlayerTokens();
    }

    // Game map
    private Map mlp;

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
        discard.addAll(hand);

        //Update the clientCards in host
        host.playerCards.put(NetworkHost.hostID, chosenCards);

        //TODO add a way to wait for the clients
        processCards();

        // Reset the chosen cards and the hand
        chosenCards = new ArrayList<>();
        hand = new ArrayList<>();
        host.playerCards.clear();
        drawCards();
    }

    /**
     * Draw cards to deck for both host and clients
     */
    public void drawCards(){
        host.promptCardDraw();
        drawCardsFromDeck(); // draw host cards
    }

    /**
     * Return host's version of map
     * @param mlp for clients
     * @return host's map
     */
    @Override
    public Map updateMap(Map mlp) {
        return this.mlp;
    }

    /**
     * Updates the map in the host, loads it for both clients and host
     * so that the map is properly loaded when the game starts.
     * @param mlp map
     */
    @Override
    public void setMap(Map mlp){
        this.mlp = mlp;
        this.mlp.setID(NetworkHost.hostID);
        host.sendMapLayerWrapper(wrapper());
        mlp.loadPlayers(wrapper());
    }

    /**
     * Process card selection from all clients and host
     */
    private void processCards() {
        int cardsProcessedPerRound = 5;

        // iterator i is same as client connection id
        for (int i = 0; i<cardsProcessedPerRound; i++){
            for (int key : clientPlayers.keySet()){

                // Get next card for the given player and pop it so it can be played
                List<Card> cards = host.playerCards.get(key);
                Card currentCard = cards.remove(0);

                // Move the clients player token
                resolveCard(currentCard, clientPlayers.get(key));
            }
        }
        // Force local board to update
        mlp.loadPlayers(wrapper());
        List<PlayerToken> playerTokens = new ArrayList<>(clientPlayers.values());

        PlayerToken winner = mlp.hasWon(playerTokens);

        if(winner != null) {
            host.sendWinner(winner);

        }

        // Send new map to clients
        host.sendMapLayerWrapper(wrapper());
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

            //TODO: Simple out of bounds check, fix this with some death logic
            if (wouldEndUp.x < 0 || wouldEndUp.x >= Game.BOARD_X || wouldEndUp.y < 0 || wouldEndUp.y >= Game.BOARD_Y) {
                break;
            }
            else {
                player.move(direction);
            }

        }
    }
}
