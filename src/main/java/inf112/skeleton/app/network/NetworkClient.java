package inf112.skeleton.app.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import inf112.skeleton.app.game.objects.PlayerToken;

import java.io.IOException;

public class NetworkClient extends Network {


    Client client;
    String IP;

    @Override
    public void stop() {
        client.stop();
    }

    @Override
    public void registerClasses() {
        Kryo kryo = client.getKryo();
        kryo.register(PlayerToken.class);
    }

    @Override
    public boolean initialize(){
            IP = prompt("Please enter the IP-address you wish to connect to", null);
            client = new Client();
            client.start();
            try {
            client.connect(5000, IP, 54555, 54777);
            return true;
        }
        catch (IOException e){
            System.out.println("Encountered an exception during connection");
            return false;
        }
    }
}
