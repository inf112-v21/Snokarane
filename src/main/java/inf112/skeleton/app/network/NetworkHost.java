package inf112.skeleton.app.network;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetworkHost extends Network {

    Server server;
    List<GameClient> clients = new ArrayList<>();
    boolean Initialized = false;

    @Override
    public void stop() {
        server.stop();
    }


    @Override
    public boolean initialize(){
        server = new Server();
        registerClasses(server);
        server.start();

        try {
            this.server.bind(54555);
            Initialized = true;
            return true;
        }
        catch (IOException e){
            System.out.println("Encountered an exception during binding");
            return false;
        }
    }

    /**
     * Sends the map to all the connected clients
     * @param map The TiledMap that should be sent
     */
    public void sendMaps(TiledMap map) {
        server.sendToAllTCP(map);
    }

    /**
     * Initializes the connections for the server. Call this only when all users are connected.
     */
    public void initConnections() {
        for (Connection c: server.getConnections()) {
            clients.add(ObjectSpace.getRemoteObject(c, NetworkData.GameClient, GameClient.class));
        }
    }

    /**
     * Uses KryoNet to ask the users to pick cards
     * @return A list containing the list of the users different card choices, where the inner lists are in order.
     */
    public List<List<Card>> getCards() {
        if (clients.isEmpty()) {
            System.out.println("Call initConnections first, or make sure that users are connected");
            return null;
        }

        //TODO: Call it's own getCards first
        List<List<Card>> playerChoices = new ArrayList<>();
        for (GameClient client: clients) {
            //TODO: Call whatever the method ends up being called
            playerChoices.add(client.pickCard());
        }
        return playerChoices;

    }

}
