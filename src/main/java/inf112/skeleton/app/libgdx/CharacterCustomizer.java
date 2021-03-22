package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class CharacterCustomizer {

    /**
     *
     * @param isLarge bool for if using the robo_small or robo_large pngs
     * @return generated player texture
     */
    public static Texture generatePlayerTexture(Boolean isLarge) { //take in chosen colour as well

        //TODO: edit input textures to be correctly sized with correct color

        //Robotexture
        Texture roboTexture;



        //this can be moved outside function, and added as a parameter in the function call
        if (!isLarge){
            roboTexture = new Texture(Gdx.files.internal("src/main/assets/robot_small.png").file().getAbsolutePath());
        }
        else {
            roboTexture = new Texture(Gdx.files.internal("src/main/assets/robot_large.png").file().getAbsolutePath());
        }



        //preparing texture and converting to a pixmap
        if (!roboTexture.getTextureData().isPrepared()) {
            roboTexture.getTextureData().prepare();
        }
        Pixmap roboPixmap = roboTexture.getTextureData().consumePixmap();



        Color mainColour = Color.BLUE; //TODO: change to take in a players chosen colour value
        Color detailColour = Color.SKY; //TODO: change to take in a players chosen colour value



        for (int y = 0; y < roboPixmap.getHeight(); y++) {
            for (int x = 0; x < roboPixmap.getWidth(); x++) {
                Color currentPixelColor = new Color(roboPixmap.getPixel(x,y));


                //Currently only works for the large robot


                if(currentPixelColor.toString().equals("ffcc00ff")) { //checks if the color of the pixel is the primary color used in the large robot texture
                    roboPixmap.setColor(mainColour);
                    roboPixmap.fillRectangle(x, y,1,1);
                }
                else if (currentPixelColor.toString().equals("e7b900ff")) { //checks if the color of the pixel is the secondary color used in the large robot texture
                    roboPixmap.setColor(detailColour);
                    roboPixmap.fillRectangle(x, y,1,1);

                }



            }
        }


        Texture returnTexture = new Texture(roboPixmap);

        return returnTexture;
    }

}
