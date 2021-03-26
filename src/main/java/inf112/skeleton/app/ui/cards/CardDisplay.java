package inf112.skeleton.app.ui.cards;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import inf112.skeleton.app.game.GamePlayer;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;

import java.util.HashMap;

/**
 * This class contains some helper functions to render clickable cards
 * onto the game screen from the players hand.
 */
public class CardDisplay {

    // Base x, base y, entire deck width, max height
    public int x, y, w, h;

    public CardDisplay (int x, int y, int w, int h){
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    /*
     * Helper for card image loading with touchup event
     */
    public Image generateClickableCard(CardType cardType, TextureRegion t, GamePlayer p){
        int cardW = 100;
        int cardH = 135;

        Image img = new Image(t);
        img.setSize(cardW, cardH);

        img.addListener(new ClickListener(){
            // Assign event handler to handle card choice on click
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (p.state == GamePlayer.PLAYERSTATE.PICKING_CARDS && p.chosenCards.size()<5){
                    Card c = new Card();
                    c.setCardType(cardType);
                    System.out.println("Clicked card with move " + cardType);

                    // intellij complaining about get before ispresent check is incorrect
                    p.chooseCards(p.hand.indexOf(p.hand.stream().anyMatch(card -> (card.getCardType() == cardType)) ? p.hand.stream().filter(card -> (card.getCardType() == cardType)).findFirst().get() : new Card()));

                    // Give some green feedback on click
                    img.setColor(0.5f, 0.7f, 0.5f, 0.5f);

                    // Clear listener so it can't be clicked again
                    img.getListeners().clear();
                }
            }
        });
        return img;
    }

    /**
     * finds duplicate cards in deck
     * HashMap used because its O(n) instead of O(n^2)
     */
    public void getDuplicateCardsInHand(GamePlayer p, HashMap<CardType, Integer> duplicates) {
        duplicates.clear();
        if (!p.hand.isEmpty()) {
            for (Card c : p.hand) {
                if (!duplicates.containsKey(c.getCardType())) {
                    duplicates.put(c.getCardType(), 1);
                } else {
                    duplicates.put(c.getCardType(), duplicates.get(c.getCardType()) + 1);
                }
            }
        }
    }
}
