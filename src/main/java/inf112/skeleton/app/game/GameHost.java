package inf112.skeleton.app.game;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkHost;

import java.util.HashMap;
import java.util.List;

public class GameHost extends GamePlayer {
    // Network connection to be used in game
    public GameHost(NetworkHost network){
        super.GamePlayer();
        host = network;

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();
        for (Connection c : host.connections){
            clientPlayers.put(c.getID(), new PlayerToken());
        }
    }
    NetworkHost host;
    // Has all clients (which contain connnection ID's) as well as their tokens
    HashMap<Integer, PlayerToken> clientPlayers;

    /**
     * Registers the client's chosen cards, then clears the host's storage of
     * clients card so that the clientCards map can be used again next turn.
     */
    @Override
    public void registerChosenCards() {
        //System.out.println(this.chosenCards);
        waitForClientsToFinishCardChoices();
        processCards();
        host.clientCards.clear();
        drawCards();
    }

    /**
     * Draw cards to deck
     */
    public void drawCards(){
        host.promptCardDraw();
        drawCardsFromDeck(); // draw host cards
    }

    /**
     * Recursively call this function until all clients have sent their cards to the host
     */
    public void waitForClientsToFinishCardChoices(){
        // If all clients have sent their cards, the host's client card storage size is same as the amount of clients connected
        while (host.clientCards.size() != host.connections.length){
            System.out.println("Client size" + Integer.toString(host.clientCards.size()));
            System.out.println("Connections size" + Integer.toString(host.connections.length) + "\n\n");

        }
        System.out.println("We done");
        for (int i: host.clientCards.keySet())
            System.out.println(host.clientCards.get(i));
    }

    public void processCards(){
        System.out.print("HEHEHEHE");
        int cardsProccessedPerRound = 5;
        // iterator i is same as client connection id
        for (int i = 0; i<cardsProccessedPerRound; i++){
            System.out.print("HEHEHEHE2");
            for (int key : clientPlayers.keySet()){
                System.out.print("HEHEHEHE3");
                List<Card> cards = host.clientCards.get(key);
                Card currentCard = cards.remove(0);

                // Move the clients player token
                PlayerToken newPlayerToken = resolveCard(currentCard, clientPlayers.get(key));

                // Place new token in clientPlayers
                clientPlayers.put(key, newPlayerToken);
            }
        }
    }

    /**
     * Resolve card moves on token
     *
     * @param card card to resolve
     * @param token token to move
     * @return token that has been moved
     */
    private PlayerToken resolveCard(Card card, PlayerToken token){
        if(card.getCardType() == CardType.FORWARDONE){
            token.move(1, 0);
        }
        return token;
    }

}
