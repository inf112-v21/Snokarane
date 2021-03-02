package inf112.skeleton.app.network;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains objects that are to be sent from host to client and vise-versa.
 */
public class NetworkData {
    // The entire map
    private TiledMap board;

    // The IDs for the object space
    static public final short GameClient = 1;

    private List<Card> allCards;
    private List<Card> cardsChosenByClient;

    // I think this should be here?
    public static List<Class> classesToRegister() {
        List<Class> classesToRegister = new ArrayList<>();
        classesToRegister.add(TiledMap.class);
        classesToRegister.add(Card.class);
        classesToRegister.add(cardList.class);
        classesToRegister.add(ArrayList.class);
        classesToRegister.add(CardType.class);
        return classesToRegister;
    }
}

