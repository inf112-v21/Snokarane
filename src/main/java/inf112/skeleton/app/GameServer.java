package inf112.skeleton.app;

import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class GameServer {

    public GameServer() throws IOException {
        Server server = new Server();
        server.start();
        server.bind(54555, 54777);
    }
}
