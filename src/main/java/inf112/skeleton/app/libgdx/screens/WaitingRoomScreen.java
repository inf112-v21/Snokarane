package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.avatars.PlayerAvatar;

public class WaitingRoomScreen extends ScreenAdapter implements IUiScreen {
    // RoboGame class instance
    private RoboGame game;
    // Stage for UI items
    Stage stage = new Stage(new ScreenViewport());

    // Table to contain player avatars
    Table playerAvatars;

    // Screen width + height
    float gdxW = Gdx.graphics.getWidth();
    float gdxH = Gdx.graphics.getHeight();

    // Handles all data transfers over internet
    Network network;

    // Ready to load game
    boolean readyToStart = false;

    // Address to connect to if client
    String ip = "";

    // Player name
    String name = "";

    // Host or client
    boolean host = false;

    // This players waiting room avatar
    PlayerAvatar avatar;

    // Indicates connection status
    Label connectionIndicatorLabel;

    // used by host to keep track of new connetions
    private int currentConnections = 0;

    public WaitingRoomScreen(RoboGame game, boolean roleHost, String IP_Address, String name){
        this.game = game;
        // Init network depending on if role is host or client, take functions from gamescreen
        this.ip = IP_Address;
        this.host = roleHost;
        this.name = name;
        // Choose whether to host or connect
        network = Network.choseRole(this.host);
        // Initializes connections, ports and opens for sending and receiving data
        this.network.initialize();
        this.network.name = name;
        if (!roleHost){
            if (!((NetworkClient)network).connectToServer(ip)){
                System.out.println("Failed to start client due to connection error.");
                ((NetworkClient) network).client.close();
                game.setScreen(new SelectRoleScreen(game));
            }
        }
        loadUIIntractables();
    }

    @Override
    public void startScreen(RoboGame game) {
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
        stage.addActor(backButton);
        connectionIndicatorLabel = new Label("", game.skin);
        connectionIndicatorLabel.setWidth(200f);
        connectionIndicatorLabel.setPosition(gdxW/2-connectionIndicatorLabel.getWidth()/2, gdxH-200);
        if (host){
            loadHostInteractables();
        }else{
            loadClientInteractables();
        }
        stage.addActor(connectionIndicatorLabel);
    }

    private void loadHostInteractables(){
        TextButton startGamebutton = new TextButton("Start game", game.skin, "small");
        float startGamebuttonLocationY = 4f;
        startGamebutton.setWidth(150);
        startGamebutton.setPosition(gdxW/2- startGamebutton.getWidth()/2, gdxH/startGamebuttonLocationY- startGamebutton.getHeight()/2);
        startGamebutton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ((NetworkHost)network).sendReadySignal();
                return true;
            }
        });

        connectionIndicatorLabel.setText("Waiting for connections...");
        stage.addActor(startGamebutton);
    }

    private void loadClientInteractables(){
        connectionIndicatorLabel.setText("Connecting...");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        if (host){
            ((NetworkHost)network).updateConnections();

            // wait for host to press start button here
            if (network.readyToInitialize){
                ((NetworkHost) network).finalizeConnections();
                game.setScreen(new GameScreen(game, host, ip, name/* network*/));
            }
        }else{
            if (network.readyToInitialize){
                connectionIndicatorLabel.setText("Connected.");
                game.setScreen(new GameScreen(game, host, ip, name/* network*/));
            }
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
    }
}
