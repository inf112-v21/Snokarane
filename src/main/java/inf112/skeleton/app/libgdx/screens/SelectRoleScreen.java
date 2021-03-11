package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.libgdx.RoboGame;

public class SelectRoleScreen extends ScreenAdapter implements IUiScreen{
    // RoboGame class instance
    private RoboGame game;
    // Stage for UI items
    Stage stage = new Stage(new ScreenViewport());

    // Screen width + height
    float gdxW = Gdx.graphics.getWidth();
    float gdxH = Gdx.graphics.getHeight();

    private boolean roleHost;
    private String IP_address;

    private TextArea nameInputField;
    private TextField IPInputField;

    public SelectRoleScreen(RoboGame game){
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
        // Background image
        Texture backgroundImage = new Texture(Gdx.files.internal("decorative/roborally-boardgame-irl.jpg"));
        Image background = new Image(backgroundImage);
        background.setSize(gdxW, gdxH);
        background.setPosition(0, 0);
        background.setColor(1, 1, 1, 0.15f);

        stage.addActor(background);
    }
    // Load intractable UI elements on screen
    public void loadUIIntractables(){
        loadTextButtons();
        loadTextFields();
    }

    private void loadTextButtons(){
        TextButton startGamebutton = new TextButton("Join game", game.skin, "small");
        float startGameButtonLocationY = 2.1f;
        startGamebutton.setWidth(150);
        startGamebutton.setPosition(gdxW/2- startGamebutton.getWidth()/2, gdxH/startGameButtonLocationY- startGamebutton.getHeight()/2);
        startGamebutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                IP_address = IPInputField.getText();
                roleHost = false;
                game.setScreen(new GameScreen(game, roleHost, IP_address));
                return true;
            }
        });

        TextButton hostButton = new TextButton("Host Game", game.skin, "small");
        float hstLocationY = 2.8f;
        hostButton.setWidth(150);
        hostButton.setPosition(gdxW/2- hostButton.getWidth()/2, gdxH/hstLocationY- hostButton.getHeight()/2);
        hostButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                roleHost = true;
                game.setScreen(new GameScreen(game, roleHost, ""));
                return true;
            }
        });

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

        stage.addActor(hostButton);
        stage.addActor(startGamebutton);
        stage.addActor(backButton);
    }
    private void loadTextFields(){
        nameInputField = new TextArea("Name", game.skin);
        float taLocationY = 1.2f;
        nameInputField.setWidth(300);
        nameInputField.setPosition(gdxW/2-nameInputField.getWidth()/2, gdxH/taLocationY-nameInputField.getHeight()/2);

        IPInputField = new TextField("IP address", game.skin);
        float IPLocationY = 1.6f;
        IPInputField.setPosition(gdxW/2-IPInputField.getWidth()/2, gdxH/IPLocationY-IPInputField.getHeight()/2);

        stage.addActor(IPInputField);
        stage.addActor(nameInputField);
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
