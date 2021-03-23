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



        //Creating sliders for selecting color with rgb

        Slider redSlider = new Slider(0, 255, 1, false, game.skin);
        redSlider.setPosition(gdxW / 2 - redSlider.getWidth() / 2, gdxH / 2 - redSlider.getHeight() / 2);

        Slider greenSlider = new Slider(0, 255, 1, false, game.skin);
        greenSlider.setPosition(gdxW/2-greenSlider.getWidth()/2, gdxH/2-greenSlider.getHeight()/2-50);

        Slider blueSlider = new Slider(0, 255, 1, false, game.skin);
        blueSlider.setPosition(gdxW/2-blueSlider.getWidth()/2, gdxH/2-blueSlider.getHeight()/2-100);


        //Adding labels to the sliders

        Label redSliderLabel = new Label("Red", game.skin);
        redSliderLabel.setPosition(redSlider.getX()-100, redSlider.getY());

        Label greenSliderLabel = new Label("Green", game.skin);
        greenSliderLabel.setPosition(greenSlider.getX()-100, greenSlider.getY());

        Label blueSliderLabel = new Label("Blue", game.skin);
        blueSliderLabel.setPosition(blueSlider.getX()-100, blueSlider.getY());



        //Event handlers for the sliders

        redSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)
                /*
                can for example change texture colour here to preview final choice
                 */
                System.out.println("redSlider moved: " + redSlider.getValue());
            }
        });


        greenSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)
                /*
                can for example change texture colour here to preview final choice
                 */
                System.out.println("greenSlider moved: " + greenSlider.getValue());
            }
        });


        blueSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)
                /*
                can for example change texture colour here to preview final choice
                 */
                System.out.println("blueSlider moved: " + blueSlider.getValue());
            }
        });


        stage.addActor(redSlider);
        stage.addActor(redSliderLabel);
        stage.addActor(greenSlider);
        stage.addActor(greenSliderLabel);
        stage.addActor(blueSlider);
        stage.addActor(blueSliderLabel);
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
