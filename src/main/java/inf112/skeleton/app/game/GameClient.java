package inf112.skeleton.app.game;
import inf112.skeleton.app.game.objects.Card;

import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkData;

import java.util.ArrayList;

public class GameClient extends GamePlayer {

    public GameClient() {
        new ObjectSpace(this).register(NetworkData.GameClient, this);
    }
    NetworkClient client = new NetworkClient();

    ArrayList<Card> hand = new ArrayList<>();

    public void addCardToHand(Card card){
        hand.add(card);
    }
}
