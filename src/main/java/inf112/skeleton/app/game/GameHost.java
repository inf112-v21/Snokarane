package inf112.skeleton.app.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.GridPoint2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.MapLayerWrapper;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameHost extends GamePlayer {
    // Network connection to be used in game
    public GameHost(NetworkHost network){
        super();
        host = network;

        // Give each client a new player token to keep track of player data
        clientPlayers = new HashMap<>();
        for (Connection c : host.connections){
            clientPlayers.put(c.getID(), new PlayerToken());
        }
    }

    public MapLayerWrapper mlp;

    NetworkHost host;
    // Has all clients (which contain connnection ID's) as well as their tokens
    HashMap<Integer, PlayerToken> clientPlayers;

    /**
     * Registers the client's chosen cards, then clears the host's storage of
     * clients card so that the clientCards map can be used again next turn.
     */
    @Override
    public void registerChosenCards() {
        // Add the cards (prematurely for now) to the discard pile)
        discard.addAll(chosenCards);
        discard.addAll(hand);

        // Reset the chosen cards and the hand
        chosenCards = new ArrayList<>();
        hand = new ArrayList<>();

        waitForClientsToFinishCardChoices();
        processCards();
        host.clientCards.clear();
        drawCards();
    }

    /**
     * Draw cards to deck
     */
    public void drawCards(){
        host.promptCardDraw();
        drawCardsFromDeck(); // draw host cards
    }

    @Override
    public MapLayerWrapper updateMap(MapLayerWrapper mlp) {
        return this.mlp;
    }

    @Override
    public void getMap(MapLayerWrapper mlp){
        this.mlp = mlp;
    }

    /**
     * Recursively call this function until all clients have sent their cards to the host
     */
    public void waitForClientsToFinishCardChoices(){
        // If all clients have sent their cards, the host's client card storage size is same as the amount of clients connected
        while (host.clientCards.size() != host.connections.length){

        }
        System.out.println("We done");
        for (int i: host.clientCards.keySet())
            System.out.println(host.clientCards.get(i));
    }

    // TODO: need to process host card selections too
    public void processCards(){
        int cardsProcessedPerRound = 5;
        // iterator i is same as client connection id
        for (int i = 0; i<cardsProcessedPerRound; i++){
            for (int key : clientPlayers.keySet()){
                List<Card> cards = host.clientCards.get(key);
                Card currentCard = cards.remove(0);
                PlayerToken player = clientPlayers.get(key);
                int playerX = player.getX();
                int playerY = player.getY();

                //Clear player cell to prepare for move
                mlp.clearCell(playerX, playerY);

                // Move the clients player token
                resolveCard(currentCard, clientPlayers.get(key));

                // Update player layer cell
                mlp.setCell(clientPlayers.get(key).getX(), clientPlayers.get(key).getY(), mlp.playerNormal);

                // Send updated map to clients
                host.sendMapLayerWrapper(mlp);
                //artificialDelayToShowMoves();
                //processPlayerMoves();
            }
        }
    }

    /**
     * Resolve card moves on token
     *
     * @param card card to resolve
     * @param token token to move
     */
    private void resolveCard(Card card, PlayerToken token) {
        switch (card.getCardType()) {
            case FORWARDONE:
                movePlayer(token, 1);
            case FORWARDTWO:
                movePlayer(token, 2);
            case FORWARDTHREE:
                movePlayer(token, 3);
            case BACK_UP:
                movePlayer(token, -1);
            case TURNLEFT:
                token.rotate(CardType.TURNLEFT);
            case TURNRIGHT:
                token.rotate(CardType.TURNRIGHT);
            case UTURN:
                token.rotate(CardType.UTURN);

        }
    }
    private void movePlayer(PlayerToken player, int dist) {
        for(int i = 0; i < dist; i++) {
            GridPoint2 wouldEndUp = player.wouldEndUp();

            //TODO: Simple out of bounds check, fix this with some death logic
            if (wouldEndUp.x < 0 || wouldEndUp.x > 4 || wouldEndUp.y < 0 || wouldEndUp.y > 4) {
                continue;
            }

            // If the tile the player is trying to move into is empty
            // it can simply move there
            else if (mlp.isEmpty(wouldEndUp)) {
                player.move();
            }
            // If the tile the player is trying to move into is a wall
            // the player stands still
            // If the tile the player is trying to move into is another player
            // the player moves the other player, then itself
            //TODO: Reallllllly shitty way to do this
            else if (mlp.containsPlayer(wouldEndUp)) {
                for (PlayerToken token: clientPlayers.values()) {
                    if (token.getX() == wouldEndUp.x && token.getY() == wouldEndUp.y) {
                        token.move(player.getDirection());
                        player.move();
                    }
                }
            }

            //else if (map.containsWall(player.wouldEndUp())) {
            //    continue;
            //}

        }
    }

}
