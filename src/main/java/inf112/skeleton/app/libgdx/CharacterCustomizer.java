package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class CharacterCustomizer {


    /*
    *
    *   Instantiate variables bellow in loadPlayerTexture
    *
    *   //roboPlayerTexture
    *
    *   Texture roboPlayerTexture = generatePlayerTexture(playerIsLarge);
    *   TextureRegion roboPlayerSplitTexture = new TextureRegion(roboPlayerTexture);
    *
    *    StaticTiledMapTile playerRoboStaticTile = new StaticTiledMapTile(roboPlayerSplitTexture);
    *    roboPlayer = new TiledMapTileLayer.Cell().setTile(playerRoboStaticTile);
    *
    * */



    /**
     *
     * @param isLarge bool for if using the robo_small or robo_large pngs
     * @return generated player texture
     */
    public static Texture generatePlayerTexture(Boolean isLarge, Color inputColor) { //TODO: take in chosen color as well

        //Robotexture
        Texture roboTexture;



        //this can be moved outside function, and added as a parameter in the function call
        if (!isLarge){
            roboTexture = new Texture(Gdx.files.internal("src/main/resources/robot_small.png").file().getAbsolutePath());
        }
        else {
            roboTexture = new Texture(Gdx.files.internal("src/main/resources/robot_large.png").file().getAbsolutePath());
        }



        //preparing texture and converting to a pixmap
        if (!roboTexture.getTextureData().isPrepared()) {
            roboTexture.getTextureData().prepare();
        }
        Pixmap roboPixmap = roboTexture.getTextureData().consumePixmap();



        Color mainColor = inputColor; //TODO: change to take in a players chosen colour value
        //Color secondaryColor = new Color(255 - mainColor.r,255 - mainColor.g, 255 - mainColor.b,100f); //TODO: generate complementry color from main color?
        Color secondaryColor = Color.PINK;

        //iterates over all pixels in the pixmap
        for (int y = 0; y < roboPixmap.getHeight(); y++) {
            for (int x = 0; x < roboPixmap.getWidth(); x++) {
                Color currentPixelColor = new Color(roboPixmap.getPixel(x,y));


                //Checks for colors that should be changed, and changes them based on desired colors

                if(currentPixelColor.toString().equals("ffcc00ff")) { //checks if the color of the pixel is the primary color used in the robot textures
                    roboPixmap.setColor(mainColor);
                    roboPixmap.fillRectangle(x, y,1,1);
                }
                else if (currentPixelColor.toString().equals("e7b900ff")) { //checks if the color of the pixel is the secondary color used in the robot textures
                    roboPixmap.setColor(secondaryColor);
                    roboPixmap.fillRectangle(x, y,1,1);

                }



            }
        }

        return new Texture(roboPixmap);
    }

}
