package inf112.skeleton.app.game;

import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.network.NetworkHost;

public class GameHost extends GamePlayer {
    NetworkHost host = new NetworkHost();

    public Card handOutCard(Card card){
        return card;
    }

}
