package inf112.skeleton.app.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import inf112.skeleton.app.game.GameHost;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import inf112.skeleton.app.libgdx.PlayerConfig;
import inf112.skeleton.app.ui.avatars.PlayerAvatar;
import inf112.skeleton.app.ui.chat.backend.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetworkHost extends Network {

    private Server server;
    public Connection[] connections;
    public int clientsRegistered = 0;
    public List<Integer> alivePlayers = new ArrayList<>();
    public GameHost host;

    // Map connection ID's to cards players chose
    public HashMap<Integer, List<Card>> playerCards = new HashMap<>();

    // Random number, and a poor implementation
    // Just grabbing a random negative number so that it doesn't clash with connection.getID()
    public static int hostID = -230230;

    // Initialize internet
    @Override
    public boolean initialize(){

        server = new Server();
        registerClasses(server);
        server.start();

        server.addListener(new Listener() {
            @Override
            public void received (Connection c, Object object) {
              
                if (object instanceof PlayerConfig) {

                    //TODO Put all this in a public method in gamehost?
                    System.out.println("Recieved the name " + ((PlayerConfig) object).getName() + " from client number " + c.getID());
                    PlayerToken token = new PlayerToken();
                    token.charState = PlayerToken.CHARACTER_STATES.PLAYERNORMAL;
                    token.ID = c.getID();
                    token.name = ((PlayerConfig) object).getName();
                    token.setConfig((PlayerConfig) object);
                    token = host.initializePlayerPos(token);
                    host.clientPlayers.put(c.getID(), token);
                }
              
                //If the player is dead, ignore them
                if (host != null && !host.clientPlayers.containsKey(c.getID())) return;
              
                // Only cards get sent through here
                if (object instanceof CardList) {
                    System.out.println("Recieved cards from " + host.clientPlayers.get(c.getID()).name);
                    //TODO We need to make sure that we wait for the answer here...
                    server.sendToTCP(c.getID(), "Power down!");
                    playerCards.put(c.getID(),((CardList) object).cardList);
                    host.checkCards();
                }
              
                if (object instanceof Message){
                    sendMessageToAll((Message) object);
                }
                  
                if (object instanceof Boolean) {
                    host.clientPlayers.get(c.getID()).powerDown = (Boolean) object;
                }
              
                if (object instanceof PlayerAvatar){
                    ((PlayerAvatar)object).id = avatars.size()-1;
                    avatars.add((PlayerAvatar)object);
                    sendAvatars();
                }
            }
        });

        try {
            this.server.bind(54555);
            return true;
        }
        catch (IOException e){
            System.out.println("Encountered an exception during binding");
            return false;
        }
    }

    /**
     * Sends MapLayerWrapper to all clients
     * @param wrapper map layer data that should be sent
     */
    public void sendMapLayerWrapper(NetworkDataWrapper wrapper) {
        server.sendToAllTCP(wrapper);
    }

    /**
     * Prompts all connected clients to draw cards
     */
    public void promptCardDraw() {
        System.out.println("Prompted clients to draw cards.");
        //TODO There's a bug here if everybody dies
        //TODO a bit shady, but the idea is to force the game to move on if everyone has powered down
        boolean allPoweredDown = !alivePlayers.isEmpty();
        for (Integer connectionID : alivePlayers) {
            PlayerToken token = host.clientPlayers.get(connectionID);
            if (connectionID == hostID && !token.powerDown) {
                allPoweredDown = false;
                host.damageCounters = token.damage;
                host.drawCardsFromDeck();
                //TODO was return here, should be continue?
                continue;
            }
            if (token.powerDown) {
                System.out.println(token.name + " has decided to power down! Good on them");
                token.powerDown = false;
                //Empty list here to simulate a player
                playerCards.put(connectionID, new ArrayList<>());
            }
            else {
                allPoweredDown = false;
                server.sendToTCP(connectionID, host.clientPlayers.get(connectionID));
            }
        }
        if (allPoweredDown) {
            host.checkCards();
            host.endOfTurn();
        }
    }


    /**
     * Broadcasts the winner so that all the clients and the host can display and handle it
     * @param winnerToken The player who won.
     */
    public void sendWinner(PlayerToken winnerToken) {
        this.winner = new PlayerToken();
        winner.name = winnerToken.name;
        server.sendToAllTCP(winner.name);
    }



    /**
     * Initializes the connections for the server. Call this only when all users are connected.
     * It also sends the IDs to the clients.
     */

    public void initConnections() {
    }
          
    /**
     * Register new connected clients to the server
     */
    public void updateConnections(){
        // only need to check connections if any have been received
        if (connections != null){
            // Poll new connections from kryo
            connections = server.getConnections();
            // Any new connections received from kryo
            if (connections.length > clientsRegistered){
                // Register new clients to host
                int amountOfConnectionsToRegister = connections.length-clientsRegistered;
                // Register the new clients
                for (int i = 0; i<amountOfConnectionsToRegister; i++){
                    // New connections get pushed to the start of connections array... so can start at index 0
                    registerClient(i);
                    clientsRegistered++;
                }
            }
        }else{
            // First time connection getter
            connections = server.getConnections();
            // Recurse back into updateConnections to check any new connections
            updateConnections();
        }
    }

    /**
     * Register client to server -
     * add them to alive players, send them their connection id, and ask them for their name
     * @param connectionIndex index of connection in connetions[] (from kryo)
     */
    private void registerClient(int connectionIndex){
        // Register to hosts players
        alivePlayers.add(connections[connectionIndex].getID());
        for (Connection c : connections){
            System.out.println(c.getID());
        }
    }

    /**
     * Register hosts connection, call this when all clients have connected
     */
    public void finalizeConnections(){
        alivePlayers.add(hostID);
    }

    public void close(){
        server.close();
    }

    public void sendIDs(){
        for (Integer i : alivePlayers){
            server.sendToTCP(i, i);
        }
    }

    public void requestNames(){
        for (Integer i : alivePlayers){
            server.sendToTCP(i, "Name");
        }
    }

    public void sendMessageToAll(Message m){
        messagesRecived.add(m);
        server.sendToAllTCP(m);
    }

    public void sendReadySignal(){
        readyToInitialize = true;
        server.sendToAllTCP(true);
    }

    public void sendAvatars(){
        for (PlayerAvatar a : avatars){
            server.sendToAllTCP(a);
        }
    }
}
