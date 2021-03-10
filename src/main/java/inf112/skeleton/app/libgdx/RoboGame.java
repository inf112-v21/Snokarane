package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import inf112.skeleton.app.libgdx.screens.MenuScreen;
import com.badlogic.gdx.Game;

public class RoboGame extends Game{
    public SpriteBatch batch;
    public BitmapFont font;
    public Skin skin;

    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();
        this.skin = new Skin(Gdx.files.internal("skins/skin/glassy-ui.json"));
        setScreen(new MenuScreen(this));
     }

     // Libgdx memory management
    public void dispose () {
        batch.dispose();
        font.dispose();
    }

    // Call super.render() here or libgdx won't recognize the render call as anything useful
    public void render() {
        super.render();
    }
}
