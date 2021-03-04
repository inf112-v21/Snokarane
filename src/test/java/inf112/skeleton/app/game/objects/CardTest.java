package inf112.skeleton.app.game.objects;

import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class CardTest {
    Card card;

    @Before
    public void setUp()  {
        card = new Card();
    }

    @Test
    public void cardTypeisFORWARDONE() {

        card.setCardType(CardType.FORWARDONE);
        assertEquals(CardType.FORWARDONE, card.getCardType());
    }


    @Test
    public void cardTypeisFORWARDTWO() {
        card.setCardType(CardType.FORWARDTWO);
        assertEquals(CardType.FORWARDTWO, card.getCardType());

    }


    @Test
    public void cardTypeisFORWARDTHREE() {
        card.setCardType(CardType.FORWARDTHREE);
        assertEquals(CardType.FORWARDTHREE, card.getCardType());

    }

    @Test
    public void cardTypeisBACK_UP() {
        card.setCardType(CardType.BACK_UP);
        assertEquals(CardType.BACK_UP, card.getCardType());

    }

    @Test
    public void cardTypeisTURNLEFT() {
        card.setCardType(CardType.TURNLEFT);
        assertEquals(CardType.TURNLEFT, card.getCardType());

    }

    @Test
    public void cardTypeisTURNRIGHT() {
        card.setCardType(CardType.TURNRIGHT);
        assertEquals(CardType.TURNRIGHT, card.getCardType());

    }

    @Test
    public void cardTypeisUTURN() {
        card.setCardType(CardType.UTURN);
        assertEquals(CardType.UTURN, card.getCardType());
    }
}
