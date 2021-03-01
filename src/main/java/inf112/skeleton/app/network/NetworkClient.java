package inf112.skeleton.app.network;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import java.io.IOException;

public class NetworkClient extends Network {


    Client client;
    String IP;

    @Override
    public void stop() {
        client.stop();
    }

    @Override
    public boolean initialize(){

        client = new Client();
        registerClasses(client);
        client.start();

        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof TiledMap ) {
                    //TODO: Add what happens if TiledMapTile object is sent
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
