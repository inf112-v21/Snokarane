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
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.MapLayerWrapper;

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
        classesToRegister.add(MapLayers.class);
        classesToRegister.add(Map.class);
        classesToRegister.add(TiledMapTileLayer.class);
        classesToRegister.add(MapLayerWrapper.class);
        classesToRegister.add(TiledMapTileLayer.Cell.class);
        classesToRegister.add(TiledMapTileLayer.Cell[][].class);
        classesToRegister.add(TiledMapTileLayer.Cell[].class);
        classesToRegister.add(StaticTiledMapTile.class);
        classesToRegister.add(TiledMapTile.class);
        classesToRegister.add(TiledMapTile.BlendMode.class);
        classesToRegister.add(TextureRegion.class);
        classesToRegister.add(Texture.class);
        classesToRegister.add(FileTextureData.class);
        classesToRegister.add(Lwjgl3FileHandle.class);

        // This is starting to seem like a bad idea...
        classesToRegister.add(java.io.File.class);
        classesToRegister.add(Files.FileType.class);
        classesToRegister.add(com.badlogic.gdx.graphics.Camera.class);

        // Do I need all these? Probably not. Am I sick of doing them one
        // by one? Absolutely.
        classesToRegister.add(Pixmap.Format.class);
        classesToRegister.add(com.badlogic.gdx.graphics.Color.class);
        classesToRegister.add(com.badlogic.gdx.graphics.Colors.class);
        classesToRegister.add(com.badlogic.gdx.graphics.Cubemap.class);
        classesToRegister.add(com.badlogic.gdx.graphics.FPSLogger.class);
        classesToRegister.add(com.badlogic.gdx.graphics.GLTexture.class);
        classesToRegister.add(com.badlogic.gdx.graphics.Mesh.class);
        classesToRegister.add(com.badlogic.gdx.graphics.VertexAttributes.class);
        classesToRegister.add(com.badlogic.gdx.graphics.TextureArray.class);
        classesToRegister.add(com.badlogic.gdx.graphics.PixmapIO.class);
        classesToRegister.add(com.badlogic.gdx.graphics.PerspectiveCamera.class);
        classesToRegister.add(com.badlogic.gdx.graphics.OrthographicCamera.class);
        return classesToRegister;
    }
}

