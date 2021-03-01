package inf112.skeleton.app.game;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkData;

public class GameClient extends GamePlayer {

    public GameClient() {
        new ObjectSpace(this).register(NetworkData.GameClient, this);
    }
    NetworkClient client = new NetworkClient();
}
