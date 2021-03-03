package inf112.skeleton.app.game;
import com.badlogic.gdx.maps.tiled.TiledMap;

import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.cardList;

import java.util.ArrayList;
import java.util.Collections;

public class GameClient extends GamePlayer {

    public GameClient(NetworkClient network) {
        super();
        network.gameClient = this;
        client = network;
    }
    NetworkClient client;

    /**
     * Send clients chosen card to NetworkHost
     */
    @Override
    public void registerChosenCards() {
        // Turn list of card into cardList-wrapper to be sent over network
        cardList listOfCards = new cardList();
        listOfCards.cardList = chosenCards;

        // Add the cards (prematurely for now) to the discard pile)
        discard.addAll(chosenCards);

        //TODO Fix this is you fix the todo in GamePlayer chosecards
        hand.removeAll(Collections.singleton(null));
        discard.addAll(hand);

        // Actually send the cards to the host
        System.out.println("Sending the cards");
        client.client.sendTCP(listOfCards);

        // Reset the chosen cards and the hand
        chosenCards = new ArrayList<>();
        hand = new ArrayList<>();
    }

    // This function is not in use in this class
    @Override
    public void drawCards() {
        // nothing happens here :o
    }

    // Return newest version of tiledMap from client
    @Override
    public TiledMap updateMap(TiledMap tiledMap) {
        return client.tiledMap;
    }
}
