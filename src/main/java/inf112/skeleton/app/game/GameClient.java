package inf112.skeleton.app.game;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import inf112.skeleton.app.game.objects.Card;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkData;
import inf112.skeleton.app.network.cardList;

import java.util.ArrayList;

public class GameClient extends GamePlayer {

    public GameClient(NetworkClient network) {
        super();
        network.gameClient = this;
        client = network;
        connectionID = client.client.getID();
    }
    NetworkClient client;
    // Client connection ID
    int connectionID;

    /**
     * Send clients chosen card to NetworkHost
     */
    @Override
    public void registerChosenCards() {
        cardList listOfCards = new cardList();
        listOfCards.cardList = chosenCards;
        System.out.println("Sending the cards");
        client.client.sendTCP(listOfCards);
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
