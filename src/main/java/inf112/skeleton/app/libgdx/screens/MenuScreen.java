package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.libgdx.RoboGame;

public class MenuScreen extends ScreenAdapter implements IUiScreen{
    // RoboGame class instance
    private RoboGame game;
    // Stage for UI items
    Stage stage = new Stage(new ScreenViewport());

    // Screen width + height
    float gdxW = Gdx.graphics.getWidth();
    float gdxH = Gdx.graphics.getHeight();

    public MenuScreen(RoboGame game){
        startScreen(game);
    }

    @Override
    public void startScreen(RoboGame game) {
        this.game = game;
        loadUIVisuals();
        loadUIIntractables();
    }

    // Load visual UI elements on screen
    public void loadUIVisuals(){
        // Logo
        Texture logoTexture = new Texture(Gdx.files.internal("snokarane-logo-200.png"));
        Image logo = new Image(logoTexture);
        float lw = logo.getWidth();
        float lh = logo.getHeight();
        float logoPositionY = 3f;
        logo.setPosition((gdxW/2)-lw/2, (gdxH/logoPositionY)-lh/2);

        // Title
        Label title = new Label("Rbrlly", game.skin, "big");
        title.setAlignment(Align.center);
        title.setY(gdxH*2/3);
        title.setWidth(gdxW);

        // Background image
        Texture backgroundImage = new Texture(Gdx.files.internal("decorative/roborally-boardgame-irl.jpg"));
        Image background = new Image(backgroundImage);
        background.setSize(gdxW, gdxH);
        background.setPosition(0, 0);
        background.setColor(1, 1, 1, 0.15f);

        stage.addActor(background);
        stage.addActor(title);
        stage.addActor(logo);
    }
    // Load intractable UI elements on screen
    public void loadUIIntractables(){
        // Host/Join game button
        // sets screen to SelectRoleScreen
        TextButton playButton = new TextButton("Host/Join game", game.skin, "small");
        float pbw = playButton.getWidth();
        float pbh = playButton.getHeight();
        float playButtonY = 1.8f;
        playButton.setPosition(gdxW/2-pbw/2,gdxH/playButtonY-pbh/2);
        playButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.buttonPressSound.play();
                game.setScreen(new SelectRoleScreen(game));;
                return true;
            }
        });

        TextButton charCustom = new TextButton("Customize", game.skin, "small");
        float ccw = charCustom.getWidth();
        float cch = charCustom.getHeight();
        float charCustomY = 1.8f;
        charCustom.setPosition(gdxW/2-ccw/2, gdxH/charCustomY-cch/2-100);
        charCustom.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.buttonPressSound.play();
                game.setScreen(new CharacterCustomizationScreen(game));
                return true;
            }
        });

        Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, game.skin);
        volumeSlider.setPosition(Gdx.graphics.getWidth()/2-volumeSlider.getWidth()/2, 50);
        volumeSlider.setValue(game.menuMusic.getVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.menuMusic.setVolume(volumeSlider.getValue());
                game.gameMusic.setVolume(volumeSlider.getValue());
            }
        });

        Label volumeSliderLabel = new Label("Volume", game.skin);
        volumeSliderLabel.setPosition(volumeSlider.getX()+volumeSlider.getWidth()/2-volumeSliderLabel.getWidth()/2, volumeSlider.getY()-19);

        stage.addActor(volumeSlider);
        stage.addActor(volumeSliderLabel);
        stage.addActor(charCustom);
        stage.addActor(playButton);
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
