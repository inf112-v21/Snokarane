package inf112.skeleton.app.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;

import java.io.IOException;

public class NetworkClient extends Network {

    public Client client;
    protected String IP;
    public Map map;
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
                    if (object.equals("Name")) {
                        giveNickname(gameClient.name);
                    }
                    else {
                        gameClient.drawCardsFromDeck();
                    }
                }
                if (object instanceof NetworkDataWrapper){
                    map.loadPlayers((NetworkDataWrapper) object);
                }

                if(object instanceof Integer) {
                    map.setID((Integer) object);
                }

                if (object instanceof PlayerToken) {
                    System.out.println(((PlayerToken) object).name + " has won! Congratulations to them!");
                    System.exit(0);
                }
            }
            public void disconnected (Connection connection) {
                System.exit(0);
            }
        }));
        return true;
    }

    /**
     * Connect to server on
     * @param ip ip
     * @return true if connect successful
     */
    public boolean connectToServer(String ip){
        try {
            client.connect(5000, ip, 54555);
            return true;
        }
        catch (IOException e){
            System.out.print("Encountered an exception during connection.");
            System.exit(0);
            return false;
        }
    }

    public void giveNickname(String name) {
        client.sendTCP(name);
    }
}
