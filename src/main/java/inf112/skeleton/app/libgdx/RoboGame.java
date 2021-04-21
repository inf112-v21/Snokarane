package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.libgdx.screens.MenuScreen;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.List;

public class RoboGame extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;

    // Board dimensions
    public static int BOARD_X = 12;
    public static int BOARD_Y = 12;

    public OrthogonalTiledMapRenderer renderer;
    public TiledMap tiledMap;

    public Music menuMusic;
    public Music gameMusic;

    public Sound buttonPressSound;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        this.skin = new Skin(Gdx.files.internal("skins/skin/glassy-ui.json"));

        // Load .tmx file from disk
        tiledMap = loadTileMapFromFile("hazardous.tmx");

        // Load music
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/menu.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/game.mp3"));

        // Load sounds
        buttonPressSound = Gdx.audio.newSound(Gdx.files.internal("audio/button-press.mp3"));

        startMenuMusic();

        // Start camera/rendering
        initializeRendering();

        setScreen(new MenuScreen(this));

     }

    /**
     * Initialize camera and renderer, sets view/renderer to tiledMap
     */
    private void initializeRendering(){
        // Initialize camera object
        OrthographicCamera camera = new OrthographicCamera();

        // for card deck
        float cardDeckSize = 1.5f;

        // for side menu scaling
        float sideMenuSize = 2f;

        // Set camera to orthographic, size board dimensions
        camera.setToOrtho(false, BOARD_X + (sideMenuSize*2), BOARD_Y +(cardDeckSize)*2);
        // Set camera X-position
        camera.position.x = (BOARD_X/2)+(sideMenuSize);
        camera.position.y = (BOARD_Y/2)-(cardDeckSize);
        camera.update();

        // Initialize renderer                                  v--- 300F is tile size
        renderer = new OrthogonalTiledMapRenderer(tiledMap, 1F/300F);
        // Set renderer to view camera
        renderer.setView(camera);
    }

    /**
     * Starts menu music
     */
    public void startMenuMusic(){
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.15f);
        menuMusic.play();
    }

    /**
     * Stop menu music
     */
    public void stopMenuMusic(){
        menuMusic.stop();
    }

    /**
     * Start game music
     */
    public void startGameMusic(){
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.15f);
        gameMusic.play();
    }

    /**
     * Stop game music
     */
    public void stopGameMusic(){
        gameMusic.stop();
    }

    /**
     * @return TiledMap object loaded from path
     * @param path path to .tmx file for map
     */
    public TiledMap loadTileMapFromFile(String path){
        return new TmxMapLoader().load(path);
    }

     // Libgdx memory management
    public void dispose () {
        batch.dispose();
        renderer.dispose();
        font.dispose();
    }

    // Call super.render() here or libgdx won't recognize the render call as anything useful
    public void render() {
        super.render();
    }
}
