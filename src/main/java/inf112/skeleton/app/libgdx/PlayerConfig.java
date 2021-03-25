package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PlayerConfig {

    String image;

    @JsonProperty("mainColor")
    Color mainColor;


    public PlayerConfig(String image, Color mainColor) {
        this.image = image;
        this.mainColor = mainColor;
    }

    public PlayerConfig() {
    }

    public void setMainColor(Color mainColor) {
        this.mainColor = mainColor;
    }

    public Color getMainColor() {
        return mainColor;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
