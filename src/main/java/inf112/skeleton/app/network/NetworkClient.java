package inf112.skeleton.app.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.CharacterCustomizer;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import inf112.skeleton.app.libgdx.PlayerConfig;
import inf112.skeleton.app.ui.avatars.PlayerAvatar;
import inf112.skeleton.app.ui.chat.backend.Message;

import java.io.IOException;

public class NetworkClient extends Network {

    public Client client;
    protected String IP;
    public Map map;
    public GameClient gameClient;

    @Override
    public void initConnections() {
    }

    /**
     * Initialize network,
     *
     * @return if network correctly started
     */
    @Override
    public boolean initialize(){

        client = new Client();
        registerClasses(client);
        client.start();

        // Add listeners to all objects that are sent over internet
        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof String) {
                    if (object.equals("Name")) {
                        giveNickname(name);
                    }
                    else if (object.equals("Config")) {
                        PlayerConfig config = CharacterCustomizer.loadCharacterConfigFromFile();
                        client.sendTCP(config);
                    }
                    else if (object.equals("Power down!")) {
                        client.sendTCP(Network.prompt("Do you wish to power down", new String[] {"true", "false"}));
                    }
                    else {
                        System.out.println(((String) object));
                        System.exit(0);
                    }
                }

                if (object instanceof NetworkDataWrapper){
                    if (map != null){
                        map.loadPlayers(((NetworkDataWrapper) object).PlayerTokens);
                        map.laserLayer = ((NetworkDataWrapper) object).laserLayer;
                    }
                }

                if(object instanceof Integer) {
                    map.setID((Integer) object);
                }

                if (object instanceof PlayerToken) {
                    gameClient.damageCounters = ((PlayerToken) object).damage;
                    gameClient.drawCardsFromDeck();
                }

                if (object instanceof Message){
                    messagesRecived.add((Message) object);
                }
                if (object instanceof Boolean){
                    readyToInitialize = true;
                }
                if (object instanceof PlayerAvatar){
                    boolean newPlayer = true;
                    for (PlayerAvatar a : avatars){
                        if (((PlayerAvatar)object).id == a.id){
                            newPlayer = false;
                        }
                    }
                    if (newPlayer){
                        avatars.add((PlayerAvatar)object);
                    }
                }
            }
            public void disconnected (Connection connection) {
                System.exit(0);
            }
        }));
        return true;
    }

    public void close(){
        client.close();
    }

    /**
     * Connect to server on
     * @param ip ip
     * @return true if connect successful
     */
    public boolean connectToServer(String ip){
        try {
            client.connect(5000, ip, 54555);
            return true;
        }
        catch (IOException e){
            System.out.print("Encountered an exception during connection.");
            System.exit(0);
            return false;
        }
    }

    public void sendAvatar(PlayerAvatar avatar){ client.sendTCP(avatar); }

    public void sendMessage(Message m){ client.sendTCP(m); }

    public void giveNickname(String name) {
        //load config from file
        PlayerConfig config = CharacterCustomizer.loadCharacterConfigFromFile();

        //adds name to config
        config.setName(name);

        //sends config including name
        client.sendTCP(config);
    }
}
