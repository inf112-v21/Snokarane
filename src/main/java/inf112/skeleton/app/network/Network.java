package inf112.skeleton.app.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import inf112.skeleton.app.Map;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Network {

    public Boolean isHost;

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
            Network server = new NetworkHost();
            server.isHost = true;
            return server;
        }
        else {
            Network client = new NetworkClient();
            client.isHost = false;
            return client;
        }
    }

    /**
     * Stops the server or client.
     */
    public abstract void stop();

    /**
     * For hosts only
     */
    public abstract void initConnections();

    /**
     * For clients only
     */
    public abstract void setMap(Map map);

    protected void registerClasses(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        for (Class c : NetworkData.classesToRegister()) {
            kryo.register(c);
        }
        ObjectSpace.registerClasses(kryo);
        ObjectSpace objectSpace = new ObjectSpace();
        objectSpace.register(1, this);
    }

    /**
     * Initializes the server or client. If the network is a client, it prompts the user for the IP they wish to connect
     * to
     * @return True if the connection went through, false otherwise
     */
    public abstract boolean initialize();

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
