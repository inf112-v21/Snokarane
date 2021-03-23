package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.libgdx.RoboGame;

public class CharacterCustomizationScreen extends ScreenAdapter implements IUiScreen  {
    // RoboGame class instance
    private RoboGame game;
    // Stage for UI items
    Stage stage = new Stage(new ScreenViewport());

    // Screen width + height
    float gdxW = Gdx.graphics.getWidth();
    float gdxH = Gdx.graphics.getHeight();

    public CharacterCustomizationScreen(RoboGame game){
        startScreen(game);
    }

    @Override
    public void startScreen(RoboGame game) {
        this.game = game;
        loadUIIntractables();
        loadUIVisuals();
    }

    @Override
    public void loadUIVisuals() {

    }

    @Override
    public void loadUIIntractables() {
        TextButton backButton = new TextButton("Back", game.skin, "small");
        float backLocationY = 6f;
        backButton.setWidth(100);
        backButton.setPosition(gdxW/2- backButton.getWidth()/2, gdxH/backLocationY- backButton.getHeight()/2);
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });

        // Slider
        Slider ySlider = new Slider(0f, 1f, 0.01f, false, game.skin);
        ySlider.setPosition(gdxW / 2 - ySlider.getWidth() / 2, gdxH / 2 - ySlider.getHeight() / 2);

        // Label for slider
        Label ySlabel = new Label("Yellow", game.skin);
        ySlabel.setPosition(ySlider.getX()-100, ySlider.getY());

        // Slider event (when slider gets moved this function is called)
        ySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                /*
                can for example change texture colour here to preview final choice
                 */
                System.out.println("ySlider moved: " + ySlider.getValue());
            }
        });

        // Slider
        Slider gSlider = new Slider(0f, 1f, 0.01f, false, game.skin);
        gSlider.setPosition(gdxW/2-gSlider.getWidth()/2, gdxH/2-gSlider.getHeight()/2-50);

        // Label for slider
        Label gSlabel = new Label("Green", game.skin);
        gSlabel.setPosition(gSlider.getX()-100, gSlider.getY());

        // Slider event (when slider gets moved this function is called)
        gSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                /*
                can for example change texture colour here to preview final choice
                 */
                System.out.println("gSlider moved: " + gSlider.getValue());
            }
        });

        stage.addActor(ySlider);
        stage.addActor(ySlabel);
        stage.addActor(gSlider);
        stage.addActor(gSlabel);
        stage.addActor(backButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
    }
}
