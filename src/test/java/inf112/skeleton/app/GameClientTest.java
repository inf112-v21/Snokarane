package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import inf112.skeleton.app.game.GameClient;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.network.Network;
import inf112.skeleton.app.network.NetworkClient;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class GameClientTest {

    @Test
    public void HandsAreDrawnCorrectly(){
        NetworkClient network = new NetworkClient();
        // Press cancel on the prompt
        //TODO Make an initilizier that just makes a random object purely for testing?
        network.initialize();
        GamePlayer player = new GameClient(network);

        for (int k = 0; k < 9; k++){
            assertEquals(84-k*9, player.deck.size());
            //System.out.println(player.deck.size());
            player.drawCardsFromDeck();
            //System.out.println(player.deck.size());
            //System.out.println("-----------------");
            assertEquals(9, player.hand.size());
            assertEquals(k*9, player.discard.size());

            for (int i = 0; i < 5; i++)
                player.chooseCards(i);
            assertEquals(5, player.chosenCards.size());
            player.registerChosenCards();
            assertEquals(0, player.hand.size());
            assertEquals(0, player.chosenCards.size());
            assertEquals((k+1)*9, player.discard.size());
        }
        player.drawCardsFromDeck();
        assertEquals(9, player.hand.size());
        assertEquals(75, player.deck.size());
        assertEquals(0, player.discard.size());
    }

    @Test
    public void CorrectAmountOfCards() {
        NetworkClient network = new NetworkClient();
        // Press cancel on the prompt
        //TODO Make an initilizier that just makes a random object purely for testing?
        network.initialize();
        GamePlayer player = new GameClient(network);
        HashMap<CardType, Integer> cardTypes = new HashMap<>();

        for (CardType type : CardType.values()) {
            cardTypes.put(type, 0);
        }
        for (Card card : player.deck) {
            cardTypes.put(card.getCardType(), cardTypes.get(card.getCardType())+1);
        }

        assertEquals((Integer)18, cardTypes.get(CardType.FORWARDONE));
        assertEquals((Integer)12, cardTypes.get(CardType.FORWARDTWO));
        assertEquals((Integer)6, cardTypes.get(CardType.FORWARDTHREE));
        assertEquals((Integer)6, cardTypes.get(CardType.BACK_UP));
        assertEquals((Integer)18, cardTypes.get(CardType.TURNRIGHT));
        assertEquals((Integer)18, cardTypes.get(CardType.TURNLEFT));
        assertEquals((Integer)6, cardTypes.get(CardType.UTURN));
    }
}
