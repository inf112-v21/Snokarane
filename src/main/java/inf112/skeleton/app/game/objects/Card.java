package inf112.skeleton.app.game.objects;

import java.util.Comparator;

public class Card {
    private CardType cardType;
    private int priority;
    public boolean picked = false;

    public Card(){
        setCardType(CardType.NONE);
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
    public CardType getCardType() {
        return cardType;
    }
    public void setPriority(int newPriority) {this.priority = newPriority;}
    public int getPriority() {
        return priority;
    }


    public static class cardComparator implements Comparator<Card> {

        @Override
        public int compare(Card card1, Card card2) {
            return card2.getPriority()-card1.getPriority();
        }
    }

}
