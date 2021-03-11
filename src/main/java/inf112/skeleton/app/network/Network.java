package inf112.skeleton.app.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import javax.swing.*;
import java.util.Objects;

public abstract class Network {

    public Boolean isHost;

    /**
     * Prompts the user to choose server or client
     * @return A server or client object based on what the user chose. Returns null if the user closes the dialog
     */
    public static Network choseRole(boolean isHost) {
        Network net = isHost ? new NetworkHost() : new NetworkClient();
        net.isHost = isHost;
        return net;
    }

    /**
     * For hosts only
     */
    public abstract void initConnections();

    /**
     * Register classes to send over internet
     */
    protected void registerClasses(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        for (Class<?> c : NetworkData.classesToRegister()) {
            kryo.register(c);
        }
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
