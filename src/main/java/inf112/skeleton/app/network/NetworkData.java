package inf112.skeleton.app.network;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.skeleton.app.game.objects.Card;

import java.util.List;

/**
 * This class contains objects that are to be sent from host to client and vise-versa.
 */
public class NetworkData {
    // Layers of the map
    private TiledMapTileLayer boardLayer;
    private TiledMapTileLayer playerLayer;
    private TiledMapTileLayer flagLayer;

    private List<Card> allCards;
    private List<Card> cardsChosenByClient;
}
