package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import inf112.skeleton.app.libgdx.Game;
import inf112.skeleton.app.libgdx.RoboGame;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;

import java.util.Objects;

import static inf112.skeleton.app.network.Network.prompt;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Roborally");
        cfg.setWindowedMode(1200, 900);

        Object[] possibilities = {"No", "Yes"};
        String s = prompt("Launch with new GUI?", possibilities);
        if (Objects.isNull(s)) {
            System.out.print("Could not choose UI Type.");
        }
        else if(s.equals("Yes")) {
            new Lwjgl3Application(new RoboGame(), cfg);
        }
        else {
            new Lwjgl3Application(new Game(), cfg);
        }
    }
}