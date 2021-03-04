package inf112.skeleton.app.network;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;

import java.io.IOException;
import java.sql.PseudoColumnUsage;

public class NetworkClient extends Network {

    public Client client;
    protected String IP;
    public Map mlp;
    public GameClient gameClient;

    @Override
    public void initConnections() {
    }

    /**
     * Initialize network,
     *
     * @return if network correctly started
     */
    @Override
    public boolean initialize(){

        client = new Client();
        registerClasses(client);
        client.start();

        // Add listeners to all objects that are sent over internet
        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof String) {
                    gameClient.drawCardsFromDeck();
                }
                if (object instanceof NetworkDataWrapper){
                    mlp.loadPlayers((NetworkDataWrapper) object);
                }

                if(object instanceof Integer) {
                    mlp.setID((Integer) object);
                }

                if (object instanceof PlayerToken) {
                    System.out.println(((PlayerToken) object).ID + " has won! Congratulations to them!");
                    System.exit(0);
                }
            }
            public void disconnected (Connection connection) {
                System.exit(0);
            }
        }));

        try {
            IP = prompt("Please enter the IP-address you wish to connect to", null);
            client.connect(5000, IP, 54555);
            return true;
        }

        catch (IOException e){
            System.out.println("Encountered an exception during connection");
            return false;
        }
    }
}
