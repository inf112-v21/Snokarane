package inf112.skeleton.app.libgdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
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

    //sliders
    Slider redSlider;
    Slider greenSlider;
    Slider blueSlider;


    //Character preview defaults
    Image characterPreviewImage;
    Texture defaultPlayerTexture = CharacterCustomizer.generatePlayerTexture(true, Color.RED); //TODO: perhaps change default color?


    private void updatePreviewImage(){
        //change previewImage
        Color newColor = new Color(redSlider.getValue() /255f, greenSlider.getValue() /255f, blueSlider.getValue() /255f, 100f);
        Texture newPlayerTexture = CharacterCustomizer.generatePlayerTexture(true, newColor);
        characterPreviewImage.setDrawable(new SpriteDrawable(new Sprite(newPlayerTexture)));


    }

    @Override
    public void startScreen(RoboGame game) {
        this.game = game;
        loadUIIntractables();
        loadUIVisuals();
    }

    @Override
    public void loadUIVisuals() {

        //Character preview
        //TODO: update character preview within handlers
        characterPreviewImage = new Image(defaultPlayerTexture);
        characterPreviewImage.setPosition(stage.getHeight() / 2,stage.getWidth() / 2); //TODO improve positioning
        stage.addActor(characterPreviewImage);

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



        //Offesets for relative positioning and sizing
        int labelOffset = -100;
        int texFiledOffset = 150;
        int textFiledWidth = 50;
        int textFiledMaxLength = 3;
        int possibleColors = 255;


        //Creating sliders for selecting color with rgb

        redSlider = new Slider(0, 255, 1, false, game.skin);
        redSlider.setPosition(gdxW / 2 - redSlider.getWidth() / 2, gdxH / 2 - redSlider.getHeight() / 2);

        greenSlider = new Slider(0, 255, 1, false, game.skin);
        greenSlider.setPosition(gdxW/2-greenSlider.getWidth()/2, gdxH/2-greenSlider.getHeight()/2-50);

        blueSlider = new Slider(0, 255, 1, false, game.skin);
        blueSlider.setPosition(gdxW/2-blueSlider.getWidth()/2, gdxH/2-blueSlider.getHeight()/2-100);


        //Adding labels to sliders

        Label redSliderLabel = new Label("Red", game.skin);
        redSliderLabel.setPosition(redSlider.getX() + labelOffset, redSlider.getY());

        Label greenSliderLabel = new Label("Green", game.skin);
        greenSliderLabel.setPosition(greenSlider.getX() + labelOffset, greenSlider.getY());

        Label blueSliderLabel = new Label("Blue", game.skin);
        blueSliderLabel.setPosition(blueSlider.getX() + labelOffset, blueSlider.getY());


        //only allow numbers in text fields
        TextField.TextFieldFilter digitsOnlyFilter = new TextField.TextFieldFilter.DigitsOnlyFilter();

        //Text fields for displaying and changing color number
        TextField redTextField= new TextField("0", game.skin);
        redTextField.setWidth(textFiledWidth);
        redTextField.setTextFieldFilter(digitsOnlyFilter);
        redTextField.setMaxLength(textFiledMaxLength);
        redTextField.setPosition(redSlider.getX() + texFiledOffset, redSlider.getY());

        TextField greenTextField = new TextField("0", game.skin);
        greenTextField.setWidth(textFiledWidth);
        greenTextField.setTextFieldFilter(digitsOnlyFilter);
        greenTextField.setMaxLength(textFiledMaxLength);
        greenTextField.setPosition(greenSlider.getX() + texFiledOffset, greenSlider.getY());

        TextField blueTextField = new TextField("0", game.skin);
        blueTextField.setWidth(textFiledWidth);
        blueTextField.setTextFieldFilter(digitsOnlyFilter);
        blueTextField.setMaxLength(textFiledMaxLength);
        blueTextField.setPosition(blueSlider.getX() + texFiledOffset, blueSlider.getY());


        //Event handlers for the sliders

        redSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                redTextField.setText(String.valueOf(Math.round(redSlider.getValue()))); //sets value of textfield to be same as slider
                redSliderLabel.setColor(redSlider.getValue() / possibleColors, 0,0f, 100f);
                System.out.println("redSlider moved: " + redSlider.getValue());
                updatePreviewImage();

            }
        });


        greenSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                greenTextField.setText(String.valueOf(Math.round(greenSlider.getValue()))); //sets value of textfield to be same as slider
                greenSliderLabel.setColor(0f, greenSlider.getValue() / possibleColors,0f, 100f);
                System.out.println("greenslider moved: " + greenSlider.getValue());
                updatePreviewImage();
            }
        });


        blueSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                blueTextField.setText(String.valueOf(Math.round(blueSlider.getValue()))); //sets value of textfield to be same as slider
                blueSliderLabel.setColor(0f, 0f,blueSlider.getValue() / possibleColors, 100f);
                System.out.println("blueSlider moved: " + blueSlider.getValue());
                updatePreviewImage();
            }
        });


        //text field event handlers

        redTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                //TODO: make sure that player can't set value above 255?

                try {
                    if(255 >= Float.parseFloat(redTextField.getText()) && Float.parseFloat(redTextField.getText()) >= 0){
                        redTextField.setText(redTextField.getText()); //sets value of textfield to be same as slider
                        redSliderLabel.setColor(Float.parseFloat(redTextField.getText()) / possibleColors, 0,0f, 100f);
                        redSlider.setValue(Float.parseFloat(redTextField.getText()));
                    }

                    else if (255 < Float.parseFloat(redTextField.getText())){ //color value can't be more than 255
                        redTextField.setText("255");
                        redSliderLabel.setColor(100f, 0,0f, 100f);
                        redSlider.setValue(255);
                    }

                    else if (0 > Float.parseFloat(redTextField.getText())){ //color value can't be less than 0
                        redTextField.setText("0");
                        redSliderLabel.setColor(0f, 0,0f, 100f);
                        redSlider.setValue(0);
                    }

                }
                catch (Exception e){
                    redTextField.setText("0");
                    redSliderLabel.setColor(0f, 0,0f, 100f);
                    redSlider.setValue(0);
                }

                System.out.println("redSlider moved: " + redTextField.getText());

            }
        });

        //TODO: implement event handler for greenTextField
        //TODO: implement event handler for blueTextField


        stage.addActor(redSlider);
        stage.addActor(redSliderLabel);
        stage.addActor(redTextField);
        stage.addActor(greenSlider);
        stage.addActor(greenSliderLabel);
        stage.addActor(greenTextField);
        stage.addActor(blueSlider);
        stage.addActor(blueSliderLabel);
        stage.addActor(blueTextField);
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
