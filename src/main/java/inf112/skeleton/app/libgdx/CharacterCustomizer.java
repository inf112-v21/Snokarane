package inf112.skeleton.app.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class CharacterCustomizer {


    /*
    *
    *   Instantiate variables bellow in loadPlayerTexture
    *
    *   //roboPlayerTexture
    *
    *   Texture roboPlayerTexture = generatePlayerTexture(String img, color col);
    *   TextureRegion roboPlayerSplitTexture = new TextureRegion(roboPlayerTexture);
    *
    *    StaticTiledMapTile playerRoboStaticTile = new StaticTiledMapTile(roboPlayerSplitTexture);
    *    roboPlayer = new TiledMapTileLayer.Cell().setTile(playerRoboStaticTile);
    *
    * */


    /**
     *
     * @param image the name of a png located in the "resources" directory as string,
     * @return generated player texture
     */
    public static Texture generatePlayerTexture(String image, Color inputColor) {

        //Robotexture
        Texture roboTexture;

        //this can be moved outside function, and added as a parameter in the function call
        roboTexture = new Texture(Gdx.files.internal("src/main/resources/" + image).file().getAbsolutePath());

        //preparing texture and converting to a pixmap
        if (!roboTexture.getTextureData().isPrepared()) {
            roboTexture.getTextureData().prepare();
        }
        Pixmap roboPixmap = roboTexture.getTextureData().consumePixmap();

        Color secondaryColor = Color.PINK; //TODO: change to take in a players chosen colour value?

        //iterates over all pixels in the pixmap
        for (int y = 0; y < roboPixmap.getHeight(); y++) {
            for (int x = 0; x < roboPixmap.getWidth(); x++) {
                Color currentPixelColor = new Color(roboPixmap.getPixel(x,y));

                //Checks for colors that should be changed, and changes them based on desired colors
                //Add else if for each color you wish to change
                if(currentPixelColor.toString().equals("ffcc00ff")) { //checks if the color of the pixel is the primary color used in the robot textures //TODO: make this a method parameter
                    roboPixmap.setColor(inputColor);
                    roboPixmap.fillRectangle(x, y,1,1);
                }
                else if (currentPixelColor.toString().equals("e7b900ff")) { //checks if the color of the pixel is the secondary color used in the robot textures //TODO make this a method parameter
                    roboPixmap.setColor(secondaryColor);
                    roboPixmap.fillRectangle(x, y,1,1);

                }

            }
        }

        return new Texture(roboPixmap);
    }


    public static PlayerConfig loadCharacterConfigFromFile(){

        String defaultCharacterImage = "robot_large.png";
        Color defaultColor = Color.BLACK;

        File configFile = new File("src/main/customisation/playerConfig.json");
        if(configFile.exists()){
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream inputStream = new FileInputStream("src/main/customisation/playerConfig.json");
                TypeReference<PlayerConfig> typeReference = new TypeReference<>() {
                };
                PlayerConfig playerConfig = mapper.readValue(inputStream, typeReference);
                inputStream.close();
                return playerConfig;


            } catch (FileNotFoundException e) {

                //create file if it does not exist
                saveCharacterConfigToFile(defaultCharacterImage, defaultColor);
                System.out.println(e);
                System.out.println("Creating new Player config file");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            return new PlayerConfig(defaultCharacterImage,defaultColor);
        }

        return new PlayerConfig(defaultCharacterImage, defaultColor); //to avoid error
    }


    public static void saveCharacterConfigToFile(String image, Color color){
        ObjectMapper objectMapper = new ObjectMapper();
        PlayerConfig playerConfig = new PlayerConfig(image,color);

        try { //try to write to file
            objectMapper.writeValue(new File("src/main/customisation/playerConfig.json"), playerConfig);
            System.out.println("saved player config");
        } catch (IOException e) { //cant write to file
            System.out.println("failed to save to player config");
            e.printStackTrace();
        }

    }


}
