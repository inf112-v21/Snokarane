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
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.RoboGame;

public class VictoryScreen  extends ScreenAdapter implements IUiScreen{
    // RoboGame class instance
    private RoboGame game;
    // Stage for UI items
    Stage stage = new Stage(new ScreenViewport());
    // Winner
    PlayerToken winner;

    public VictoryScreen(RoboGame game, PlayerToken winner){
        this.winner = winner;
        startScreen(game);
    }

    @Override
    public void startScreen(RoboGame game) {
        this.game = game;
        loadUIVisuals();
        loadUIIntractables();
    }

    @Override
    public void loadUIVisuals() {
        Label gameOverTitle = new Label("Game over!", game.skin);
        Table highScore = new Table(game.skin);

        Label n = new Label(winner.name + " won!", game.skin);
        n.setColor(0, 1, 1, 1);

        highScore.add(n);

        int gameOverTitleY = 100;
        int highScoreY = 300;

        gameOverTitle.setPosition(Gdx.graphics.getWidth()/2-gameOverTitle.getWidth()/2, Gdx.graphics.getHeight()-gameOverTitleY);
        highScore.setPosition(Gdx.graphics.getWidth()/2-highScore.getWidth()/2, Gdx.graphics.getHeight() - highScoreY);

        stage.addActor(gameOverTitle);
        stage.addActor(highScore);
    }

    @Override
    public void loadUIIntractables() {
        TextButton backButton = new TextButton("Back", game.skin, "small");
        float backLocationY = 6f;
        backButton.setWidth(100);
        backButton.setPosition(Gdx.graphics.getWidth()/2- backButton.getWidth()/2, Gdx.graphics.getHeight()/backLocationY- backButton.getHeight()/2);
        backButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });
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
