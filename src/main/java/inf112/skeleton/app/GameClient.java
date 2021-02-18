package inf112.skeleton.app;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class GameClient extends Network{


    Client client;
    String IP;

    @Override
    protected void stop() {
        client.stop();
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
