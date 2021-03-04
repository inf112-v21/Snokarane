package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class MapLayerWrapper {

    public TiledMapTileLayer.Cell[][] boardLayer = new TiledMapTileLayer.Cell[5][5];
    public TiledMapTileLayer.Cell[][] playerLayer = new TiledMapTileLayer.Cell[5][5];
    public TiledMapTileLayer.Cell[][] flagLayer = new TiledMapTileLayer.Cell[5][5];
}
