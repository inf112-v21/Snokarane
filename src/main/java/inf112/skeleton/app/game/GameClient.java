package inf112.skeleton.app.game;

import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.CardList;

import java.util.ArrayList;

/**
 * This class contains information the client needs to interact with the game
 */
public class GameClient extends GamePlayer {

    //TODO Maybe do this differently?
    public String name;
    /**
     * @param network client network that has a connection to host
     */
    public GameClient(NetworkClient network, String name) {

        // Add cards to deck
        super();
        network.gameClient = this;
        client = network;
        this.name = name;
    }

    private final NetworkClient client;


    /**
     * Send clients chosen card to NetworkHost
     */
    @Override
    public void registerChosenCards() {
        // Turn list of card into cardList-wrapper to be sent over network
        CardList listOfCards = new CardList();
        listOfCards.cardList = chosenCards;

        // Actually send the cards to the host
        System.out.println("Sending the cards to host...");
        client.client.sendTCP(listOfCards);

        // Add the cards (prematurely for now) to the discard pile)
        discard.addAll(chosenCards);

        // Reset the chosen cards and the hand
        chosenCards = new ArrayList<>();
        hand = new ArrayList<>();
    }

    // This function is not in use in this class
    @Override
    public void drawCards() {
    }

    // Get map from network client
    @Override
    public void setMap(Map mlp) {
        client.map = mlp;
    }

    // Return newest version of Map instance from client
    @Override
    public Map updateMap(Map mlp) {
        return client.map;
    }

}
