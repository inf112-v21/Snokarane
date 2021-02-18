package inf112.skeleton.app;

import com.esotericsoftware.kryonet.Server;
import java.io.IOException;

public class GameServer extends Network{

    Server server;

    @Override
    protected void stop() {
        server.stop();
    }

    @Override
    public boolean initialize(){
        server = new Server();
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
}
