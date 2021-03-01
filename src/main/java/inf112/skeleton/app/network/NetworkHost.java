package inf112.skeleton.app.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkHost extends Network {

    Server server;
    List<PlayerToken> players = new ArrayList<>();

    @Override
    public void stop() {
        server.stop();
    }

    @Override
    public void registerClasses() {
        Kryo kryo = server.getKryo();
        kryo.register(PlayerToken.class);
    }

    @Override
    public boolean initialize(){
        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new CharacterConnection();
            }
        };
        this.server.start();
        try {
            this.server.bind(54555, 54777);
            return true;
        }
        catch (IOException e){
            System.out.println("Encountered an exception during binding");
            return false;
        }
    }

    // This holds per connection state.
    static class CharacterConnection extends Connection {
        public Character character;
    }
}
