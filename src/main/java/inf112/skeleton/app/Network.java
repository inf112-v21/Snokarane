package inf112.skeleton.app;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public abstract class Network {

    protected Boolean isHost;
    //Purely for testing
    public static void main(String[] args){
        Network myNetwork = choseRole();
        myNetwork.initialize();
        System.out.println(myNetwork);
        System.out.println(myNetwork.isHost);
    }

    /**
     * Prompts the user to choose server or client
     * @return A server or client object based on what the user chose. Returns null if the user closes the dialog
     */
    public static Network choseRole() {
        Object[] possibilities = {"Host", "Client"};
        String s = prompt("Which role would you like?", possibilities);
        if (Objects.isNull(s)) {
            return null;
        }
        else if(s.equals("Host")) {
            Network server = new GameServer();
            server.isHost = true;
            return server;
        }
        else {
            Network client = new GameClient();
            client.isHost = false;
            return client;
        }
    }

    /**
     * Stops the server or client.
     */
    protected abstract void stop();

    /**
     * Initializes the server or client. If the network is a client, it prompts the user for the IP they wish to connect
     * to
     * @return True if the connection went through, false otherwise
     */
    protected abstract boolean initialize();

    public static String prompt(String prompt, Object[] possibilities) {
        String choice;
        if(Objects.isNull(possibilities)) {
            choice = JOptionPane.showInputDialog(
                    null,
                    prompt,
                    "",
                    JOptionPane.PLAIN_MESSAGE
            );
        }
        else{
            choice = (String) JOptionPane.showInputDialog(
                    null,
                    prompt,
                    "",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    possibilities[0]);
        }
        return choice;
    }
}
