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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
import inf112.skeleton.app.libgdx.PlayerConfig;
import inf112.skeleton.app.libgdx.RoboGame;

import static inf112.skeleton.app.libgdx.CharacterCustomizer.loadCharacterConfigFromFile;
import static inf112.skeleton.app.libgdx.CharacterCustomizer.saveCharacterConfigToFile;

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

    //Sliders
    Slider redSlider;
    Slider greenSlider;
    Slider blueSlider;

    //Character preview defaults
    Image characterPreviewImage;
    Texture defaultPlayerTexture = CharacterCustomizer.generatePlayerTexture(loadCharacterConfigFromFile().getImage(), loadCharacterConfigFromFile().getMainColor()); //TODO: perhaps change default color?

    //checkBox
    CheckBox useLargeCheckBox;


    private void updatePreviewImage(){
        //change previewImage
        //String playerImage = loadCharacterConfigFromFile().getImage();
        String playerImage;

        if (useLargeCheckBox.isChecked()){
            playerImage = "robot_large.png";
        } else {
            playerImage = "robot_small.png";
        }

        Color newColor = new Color(redSlider.getValue() /255f, greenSlider.getValue() /255f, blueSlider.getValue() /255f, 100f);

        Texture newPlayerTexture = CharacterCustomizer.generatePlayerTexture(playerImage, newColor);
        characterPreviewImage.setDrawable(new SpriteDrawable(new Sprite(newPlayerTexture)));
        characterPreviewImage.setPosition(gdxW/2 - characterPreviewImage.getWidth()/2, gdxH-450);

    }


    @Override
    public void startScreen(RoboGame game) {
        this.game = game;
        loadUIVisuals();
        loadUIIntractables();
    }

    @Override
    public void loadUIVisuals() {

        // Background image
        Texture backgroundImage = new Texture(Gdx.files.internal("decorative/roborally-boardgame-irl.jpg"));
        Image background = new Image(backgroundImage);
        background.setSize(gdxW, gdxH);
        background.setPosition(0, 0);
        background.setColor(1, 1, 1, 0.045f);

        Label title = new Label("Your Character", game.skin, "big");
        title.setAlignment(Align.center);
        title.setY(gdxH-100);
        title.setWidth(gdxW);

        stage.addActor(background);
        stage.addActor(title);
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
                game.buttonPressSound.play();
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });


        //Character preview
        characterPreviewImage = new Image(defaultPlayerTexture);
        characterPreviewImage.setPosition(stage.getHeight() / 2,stage.getWidth() / 2); //TODO improve positioning
        stage.addActor(characterPreviewImage);


        //Offsets for relative positioning and sizing
        int labelOffset = -100;
        int texFiledOffset = 150;
        int textFiledWidth = 50;
        int textFiledMaxLength = 3;
        int possibleColors = 255;
        int saveButtonOffset = 100;


        //Button for saving chosen colors
        Button saveButton = new TextButton("save", game.skin, "small");
        saveButton.setPosition(gdxW/2-saveButton.getWidth()/2, backButton.getY() + saveButtonOffset);  //set position of button relative to backButton

        useLargeCheckBox = new CheckBox("big?", game.skin);

        saveButton.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                game.buttonPressSound.play();
                String defaultImage = "robot_small.png";
                String image;

                if(useLargeCheckBox.isChecked()){ //large player if checked
                    image = "robot_large.png";
                } else {
                    image = defaultImage;
                }

                Color chosenColor = new Color(redSlider.getValue() /255f,greenSlider.getValue() /255f, blueSlider.getValue() /255f, 100f);
                saveCharacterConfigToFile(image, chosenColor);
                return true;
            }
        });


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
        TextField.TextFieldFilter digitsOnlyFilter = new TextField.TextFieldFilter.DigitsOnlyFilter(); //TODO: create own filter that prohibits values above 255

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

        //for selecting large or small player texture //TODO: add more options? (if so change to something different than checkbox)
        useLargeCheckBox.setPosition(Gdx.graphics.getWidth()/2-useLargeCheckBox.getWidth()/2, redSlider.getY() + 50); //random offset //TODO: change offset to variable




        //sets values of intractable ui elements from config if possible
        try{
            PlayerConfig playerConfig = CharacterCustomizer.loadCharacterConfigFromFile();
            redSlider.setValue(playerConfig.getMainColor().r * possibleColors);
            greenSlider.setValue(playerConfig.getMainColor().g * possibleColors);
            blueSlider.setValue(playerConfig.getMainColor().b * possibleColors);

            redSliderLabel.setColor(playerConfig.getMainColor().r,0,0,100f);
            greenSliderLabel.setColor(0,playerConfig.getMainColor().g,0,100f);
            blueSliderLabel.setColor(0,0,playerConfig.getMainColor().b,100f);

            redTextField.setText(Integer.toString(Math.round( playerConfig.getMainColor().r * possibleColors)));
            greenTextField.setText(Integer.toString(Math.round( playerConfig.getMainColor().g * possibleColors)));
            blueTextField.setText(Integer.toString(Math.round( playerConfig.getMainColor().b * possibleColors)));

            if(playerConfig.getImage().equals("robot_large.png")){ //sets checkbox to checked if config says playerIsLarge
                useLargeCheckBox.setChecked(true);
            } else {
                useLargeCheckBox.setChecked(false);
            }


            updatePreviewImage();

        } catch (Exception e){
            System.out.println("Could not load config setting ui elements to defaults");
        }



        useLargeCheckBox.addListener(new ChangeListener() { //Event handler for isLargeCheckBox
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                updatePreviewImage();
            }
        });


        //Event handlers for the sliders

        redSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                redTextField.setText(String.valueOf(Math.round(redSlider.getValue()))); //sets value of textfield to be same as slider
                redSliderLabel.setColor(redSlider.getValue() / possibleColors, 0,0f, 100f);
                updatePreviewImage();

            }
        });


        greenSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                greenTextField.setText(String.valueOf(Math.round(greenSlider.getValue()))); //sets value of textfield to be same as slider
                greenSliderLabel.setColor(0f, greenSlider.getValue() / possibleColors,0f, 100f);
                updatePreviewImage();
            }
        });


        blueSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                blueTextField.setText(String.valueOf(Math.round(blueSlider.getValue()))); //sets value of textfield to be same as slider
                blueSliderLabel.setColor(0f, 0f,blueSlider.getValue() / possibleColors, 100f);
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


            }
        });


        greenTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                //TODO: make sure that player can't set value above 255?

                try {
                    if(255 >= Float.parseFloat(greenTextField.getText()) && Float.parseFloat(greenTextField.getText()) >= 0){
                        greenTextField.setText(greenTextField.getText()); //sets value of textfield to be same as slider
                        greenSliderLabel.setColor(0, Float.parseFloat(greenTextField.getText()) / possibleColors,0f, 100f);
                        greenSlider.setValue(Float.parseFloat(greenTextField.getText()));
                    }

                    else if (255 < Float.parseFloat(greenTextField.getText())){ //color value can't be more than 255
                        greenTextField.setText("255");
                        greenSliderLabel.setColor(0f, 100f,0f, 100f);
                        greenSlider.setValue(255);
                    }

                    else if (0 > Float.parseFloat(greenTextField.getText())){ //color value can't be less than 0
                        greenTextField.setText("0");
                        greenSliderLabel.setColor(0f, 0,0f, 100f);
                        greenSlider.setValue(0);
                    }

                }
                catch (Exception e){
                    greenTextField.setText("0");
                    greenSliderLabel.setColor(0f, 0,0f, 100f);
                    greenSlider.setValue(0);
                }


            }
        });


        blueTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) { // Slider event (when slider gets moved this function is called)

                //TODO: make sure that player can't set value above 255?

                try {
                    if(255 >= Float.parseFloat(blueTextField.getText()) && Float.parseFloat(blueTextField.getText()) >= 0){
                        blueTextField.setText(blueTextField.getText()); //sets value of textfield to be same as slider
                        blueSliderLabel.setColor(0f, 0f,Float.parseFloat(blueTextField.getText()) / possibleColors, 100f);
                        blueSlider.setValue(Float.parseFloat(blueTextField.getText()));
                    }

                    else if (255 < Float.parseFloat(blueTextField.getText())){ //color value can't be more than 255
                        blueTextField.setText("255");
                        blueSliderLabel.setColor(0f, 0f,100f, 100f);
                        blueSlider.setValue(255);
                    }

                    else if (0 > Float.parseFloat(blueTextField.getText())){ //color value can't be less than 0
                        blueTextField.setText("0");
                        blueSliderLabel.setColor(0f, 0,0f, 100f);
                        blueSlider.setValue(0);
                    }

                }
                catch (Exception e){
                    blueTextField.setText("0");
                    blueSliderLabel.setColor(0f, 0,0f, 100f);
                    blueSlider.setValue(0);
                }


            }
        });

        stage.addActor(redSlider);
        stage.addActor(redSliderLabel);
        stage.addActor(redTextField);
        stage.addActor(greenSlider);
        stage.addActor(greenSliderLabel);
        stage.addActor(greenTextField);
        stage.addActor(blueSlider);
        stage.addActor(blueSliderLabel);
        stage.addActor(blueTextField);
        stage.addActor(useLargeCheckBox);
        stage.addActor(saveButton);
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
