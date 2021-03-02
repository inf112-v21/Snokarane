package inf112.skeleton.app.game;

import com.esotericsoftware.kryonet.Client;
import inf112.skeleton.app.game.objects.Card;
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
        for (GameClient c : host.clients){
            clientPlayers.put(c, new PlayerToken());
        }
    }
    NetworkHost host;
    // Has all clients (which contain connnection ID's) as well as their tokens
    HashMap<GameClient, PlayerToken> clientPlayers;

    /**
     * Registers the client's chosen cards, then clears the host's storage of
     * clients card so that the clientCards map can be used again next turn.
     */
    @Override
    public void registerChosenCards() {
        System.out.println(this.chosenCards);
        waitForClientsToFinishCardChoices();
        host.clientCards.clear();
    }

    /**
     * Draw cards to deck
     */
    public void drawCards(){
        for (GameClient client : host.clients)
            client.drawCardsFromDeck();
        drawCardsFromDeck(); // draw host cards
    }

    /**
     * Recursively call this function until all clients have sent their cards to the host
     */
    public void waitForClientsToFinishCardChoices(){
        // If all clients have sent their cards, the host's client card storage size is same as the amount of clients connected
        if (host.clientCards.size() == host.clients.size()){
            // TODO: do something to cards, game logic things
        } else{
            waitForClientsToFinishCardChoices();
        }
    }

}
