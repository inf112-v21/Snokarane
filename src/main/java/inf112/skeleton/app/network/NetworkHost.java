package inf112.skeleton.app.network;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class NetworkHost extends Network {

    Server server;
    public Connection[] connections;
    public HashMap<Integer, List<Card>> playerCards = new HashMap<>();

    // Random number, and a poor implementation
    //Just grabbing a random negative number so that it doesn't clash with connection.getID()
    public int hostID = -230230;

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
        server.addListener(new Listener() {
            @Override
            public void received (Connection c, Object object) {
                // Only cards get sent through here
                if (object instanceof cardList) {
                    System.out.println("Got cards!");
                    playerCards.put(c.getID(),((cardList) object).cardList);
                }
            }
        });

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
     * Sends MapLayerWrapper to all clients
     * @param wrapper map layer data that should be sent
     */
    public void sendMapLayerWrapper(NetworkDataWrapper wrapper) {
        server.sendToAllTCP(wrapper);
    }

    /**
     * Prompts all connected clients to draw cards
     */
    public void promptCardDraw() {
        System.out.println("Sent it");
        server.sendToAllTCP("Draw cards!");
    }

    /**
     * Initializes the connections for the server. Call this only when all users are connected.
     */
    public void initConnections() {
        connections = server.getConnections();
    }

}
