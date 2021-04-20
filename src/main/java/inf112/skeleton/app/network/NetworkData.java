package inf112.skeleton.app.network;

import com.badlogic.gdx.math.GridPoint2;
import inf112.skeleton.app.game.objects.*;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import inf112.skeleton.app.libgdx.PlayerConfig;
import inf112.skeleton.app.ui.chat.backend.ChatterData;
import inf112.skeleton.app.ui.chat.backend.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains objects that are to be sent from host to client and vise-versa.
 */
public class NetworkData {

    // Register classes to kryonet
    public static List<Class<?>> classesToRegister() {
        List<Class<?>> classesToRegister = new ArrayList<>();
        classesToRegister.add(Card.class);
        classesToRegister.add(CardList.class);
        classesToRegister.add(ArrayList.class);
        classesToRegister.add(CardType.class);
        classesToRegister.add(PlayerToken.class);
        classesToRegister.add(Direction.class);
        classesToRegister.add(GridPoint2.class);
        classesToRegister.add(NetworkDataWrapper.class);
        classesToRegister.add(Flag.class);
        classesToRegister.add(PlayerToken.CHARACTER_STATES.class);
        classesToRegister.add(String.class);
        classesToRegister.add(int[][][].class);
        classesToRegister.add(int[][].class);
        classesToRegister.add(int[].class);
        classesToRegister.add(int.class);
        classesToRegister.add(Message.class);
        classesToRegister.add(ChatterData.class);
        classesToRegister.add(PlayerConfig.class);

        return classesToRegister;
    }
}

