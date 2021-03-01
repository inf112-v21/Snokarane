package inf112.skeleton.app;

import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import org.junit.Test;
import static org.junit.Assert.*;


public class CardTest {

    @Test
    public void getCardTypeTest(){
        Card card = new Card(CardType.FORWARDONE);

        assertEquals(CardType.FORWARDONE, card.getCardType());

    }
}
