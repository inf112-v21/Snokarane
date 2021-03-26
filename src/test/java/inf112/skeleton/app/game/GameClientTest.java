package inf112.skeleton.app.game;


import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.network.NetworkClient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GameClientTest {

    @Test
    public void HandsAreDrawnCorrectly() throws InterruptedException {
        NetworkClient network = new NetworkClient();
        // Press cancel on the prompt
        //TODO Make an initilizier that just makes a random object purely for testing?
        network.initialize();
        GamePlayer player = new GameClient(network, "Mr. testman!");

        for (int k = 0; k < 9; k++){
            assertEquals(84-k*9, player.deck.size());
            player.drawCardsFromDeck();
            assertEquals(9, player.hand.size());
            assertEquals(k*9, player.discard.size());

            for (int i = 0; i < 5; i++) {
                player.chooseCards(i);
            }
            int choosenCards = 0;
            //Shady workaround since LIBGDX handles discard for chosen cards
            List<Card> cardsToDisc = new ArrayList<>();
            for(Card card: player.hand) {
                if (card.picked) player.chosenCards.add(card);
                else cardsToDisc.add(card);
            }
            assertEquals(5, player.chosenCards.size());
            player.registerChosenCards();
            assertEquals(0, player.hand.size());
            assertEquals(0, player.chosenCards.size());
            player.discard.addAll(cardsToDisc);
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
        GamePlayer player = new GameClient(network, "Mr. testman!");
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
