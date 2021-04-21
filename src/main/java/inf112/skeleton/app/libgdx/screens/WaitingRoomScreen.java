package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
import inf112.skeleton.app.libgdx.PlayerConfig;
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
    int localAvatars = 0;

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

        this.avatar = new PlayerAvatar(CharacterCustomizer.loadCharacterConfigFromFile(), name);

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
            }else{
                // Send clients avatar
                ((NetworkClient)network).sendAvatar(avatar);
            }
        }else{
            // Add hosts avatar to avatars
            avatar.id = network.avatars.size()-1;
            network.avatars.add(avatar);
        }
        loadUIVisuals();
        loadUIIntractables();
    }

    @Override
    public void startScreen(RoboGame game) {
    }

    @Override
    public void loadUIVisuals() {
        // Background image
        Texture backgroundImage = new Texture(Gdx.files.internal("decorative/roborally-boardgame-irl.jpg"));
        Image background = new Image(backgroundImage);
        background.setSize(gdxW, gdxH);
        background.setPosition(0, 0);
        background.setColor(1, 1, 1, 0.15f);
        background.setName("back-image");
        stage.addActor(background);
    }

    @Override
    public void loadUIIntractables() {
        TextButton backButton = new TextButton("Back", game.skin, "small");
        backButton.setName("back-button");
        float backLocationY = 6f;
        backButton.setWidth(100);
        backButton.setPosition(gdxW/2- backButton.getWidth()/2, gdxH/backLocationY- backButton.getHeight()/2);
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                network.close();
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });
        stage.addActor(backButton);
        connectionIndicatorLabel = new Label("", game.skin);
        connectionIndicatorLabel.setName("conn-indicator");
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
        startGamebutton.setName("start-game-button");
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
        connectionIndicatorLabel.setText("Waiting for connections...");
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
                game.setScreen(new GameScreen(game, host, ip, name, network));
            }
        }else{
            if (network.readyToInitialize){
                connectionIndicatorLabel.setText("Connected.");
                game.setScreen(new GameScreen(game, host, ip, name, network));
            }
        }

        // poll avatar updates
        if (network.avatars.size() > localAvatars){
            for (Actor a : stage.getActors()){
                if (a.getName().equals("players-connected")){
                    a.clear();
                }
            }
            localAvatars = network.avatars.size();
            loadPlayerAvatars();
        }


        stage.act();
        stage.draw();
    }

    private void loadPlayerAvatars(){
        Table playersConnected = new Table(game.skin);
        playersConnected.setSize(gdxW-100, 500);
        playersConnected.setPosition(50, 250);
        playersConnected.setName("players-connected");

        for (PlayerAvatar av : network.avatars){
            PlayerConfig c = av.playerConfig;
            Texture text = CharacterCustomizer.generatePlayerTexture(c.getImage(), c.getMainColor());
            Image i = new Image(text);
            playersConnected.add(i);
        }

        playersConnected.row();

        for (PlayerAvatar av : network.avatars){
            Label l = new Label(av.id+". "+av.playerName, game.skin);
            playersConnected.add(l);
        }
        stage.addActor(playersConnected);
    }

    @Override
    public void hide() {
    }
}
