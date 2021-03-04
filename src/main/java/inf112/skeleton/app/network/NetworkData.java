package inf112.skeleton.app.network;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.MapLayerWrapper;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;

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
        classesToRegister.add(Card.class);
        classesToRegister.add(cardList.class);
        classesToRegister.add(ArrayList.class);
        classesToRegister.add(CardType.class);
        classesToRegister.add(PlayerToken.class);
        classesToRegister.add(PlayerToken.Direction.class);
        classesToRegister.add(NetworkDataWrapper.class);

        return classesToRegister;
    }
}

