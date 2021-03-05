package inf112.skeleton.app.game.objects;

public class Card {
    private CardType cardType;

    public Card(){
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
    public CardType getCardType() {
        return cardType;
    }
}
