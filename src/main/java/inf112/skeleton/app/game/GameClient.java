package inf112.skeleton.app.game;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.network.NetworkClient;

import java.util.ArrayList;

public class GameClient extends GamePlayer {
    NetworkClient client = new NetworkClient();

    ArrayList<Card> hand = new ArrayList<>();

    public void addCardToHand(Card card){
        hand.add(card);
    }




}
