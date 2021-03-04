package inf112.skeleton.app.game;

import com.badlogic.gdx.math.GridPoint2;
import com.esotericsoftware.kryonet.Connection;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameHost extends GamePlayer {
    // Network connection to be used in game
    public GameHost(NetworkHost network){
        super();
        host = network;

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();
        int i = 0;
        for (Connection c : host.connections){
            PlayerToken token = new PlayerToken();
            token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
            token.ID = c.getID();
            clientPlayers.put(c.getID(), token);
        }
        PlayerToken token = new PlayerToken();
        token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
        token.ID = NetworkHost.hostID;
        clientPlayers.put(NetworkHost.hostID, token);
    }

    public Map mlp;

    NetworkHost host;
    // Has all clients (which contain connnection ID's) as well as their tokens
    HashMap<Integer, PlayerToken> clientPlayers;

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

        waitForClientsToFinishCardChoices();
        processCards();

        // Reset the chosen cards and the hand
        chosenCards = new ArrayList<>();
        hand = new ArrayList<>();
        host.playerCards.clear();
        drawCards();
    }

    /**
     * Draw cards to deck
     */
    public void drawCards(){
        host.promptCardDraw();
        drawCardsFromDeck(); // draw host cards
    }

    @Override
    public Map updateMap(Map mlp) {
        return this.mlp;
    }

    @Override
    public void getMap(Map mlp){
        this.mlp = mlp;
        this.mlp.setID(NetworkHost.hostID);
        host.sendMapLayerWrapper(wrapper());

        //TODO Fix this terrible implementation...
        mlp.loadPlayers(wrapper());
    }

    /**
     * Recursively call this function until all clients have sent their cards to the host
     */
    public void waitForClientsToFinishCardChoices() {
        // If all clients have sent their cards, the host's client card storage size is same as the amount of clients connected
        while (host.playerCards.size() != host.connections.length+1){

        }
    }

    // TODO: need to process host card selections too
    public void processCards() {
        int cardsProcessedPerRound = 5;

        // iterator i is same as client connection id
        for (int i = 0; i<cardsProcessedPerRound; i++){
            for (int key : clientPlayers.keySet()){
                System.out.println(key);
                List<Card> cards = host.playerCards.get(key);
                Card currentCard = cards.remove(0);
                PlayerToken player = clientPlayers.get(key);
                int playerX = player.getX();
                int playerY = player.getY();

                // Move the clients player token
                resolveCard(currentCard, clientPlayers.get(key));


            }
        }
        // Send updated map to clients
        host.sendMapLayerWrapper(wrapper());

        // Force the board to update, not sure if necessary
        mlp.loadPlayers(wrapper());
    }

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
                movePlayer(token, 1);
                break;
            case FORWARDTWO:
                movePlayer(token, 2);
                break;
            case FORWARDTHREE:
                movePlayer(token, 3);
                break;
            case BACK_UP:
                movePlayer(token, -1);
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
    private void movePlayer(PlayerToken player, int dist) {
        for(int i = 0; i < dist; i++) {
            GridPoint2 wouldEndUp = player.wouldEndUp();

            //TODO: Simple out of bounds check, fix this with some death logic
            if (wouldEndUp.x < 0 || wouldEndUp.x >= Game.BOARD_X || wouldEndUp.y < 0 || wouldEndUp.y >= Game.BOARD_Y) {
                break;
            }

            player.move();

        }
    }

}
