package inf112.skeleton.app.game;

import edu.emory.mathcs.backport.java.util.Arrays;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.libgdx.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Base class for players
 * This class contains cards chosen from players, and ways to update the game map from client to host.
 */
public abstract class GamePlayer{

    // Gameplay states the player is in during the game
    public enum PLAYERSTATE{
        PICKING_CARDS,
        SENDING_CARDS,
        NONE
    }

    // Whether game screen needs to update card deck, is true when all cards recieved for one round
    public boolean newCardsDelivered = false;

    // Current state of gameplay of player
    public PLAYERSTATE state = PLAYERSTATE.NONE;

    // Cards selected to be sent to host for processing
    public ArrayList<Card> chosenCards = new ArrayList<>();
    // Cards to choose from in current turn
    public ArrayList<Card> hand = new ArrayList<>();
    // Where cards get selected from
    protected ArrayList<Card> deck = new ArrayList<>();
    // Cards that weren't selected
    public ArrayList<Card> discard = new ArrayList<>();

    /**
     * Give player a stack of cards to deck
     */
    public GamePlayer(){
        Random random = new Random();
        //Move1, Move2, Move3, Back up, Rotate right, Rotate left, U-turn
        List<Integer> numOfEachCardType = Arrays.asList(new Integer[]{18, 12, 6, 6, 18, 18, 6});
        for(int i = 0; i < numOfEachCardType.size(); i++) {
            CardType cardType = CardType.values()[i];
            for (int j = 0; j < numOfEachCardType.get(i); j++) {
                Card card = new Card();
                card.setCardType(cardType);
                card.setPriority(random.nextInt(1000));
                deck.add(card);
            }
        }
        Collections.shuffle(deck);
    }

    /**
     * Adds cards to hand from deck, and sets playerstate to PICKING_CARDS when added
     */
    public void drawCardsFromDeck(int damageCounters){
        int cardsToAdd = Math.min(9-damageCounters-hand.size(), deck.size()-hand.size());

        for (int i = 0; i<cardsToAdd; i++){
            hand.add(deck.remove(0));
        }
        if (hand.size() != 9-damageCounters) {
            deck.addAll(discard);
            Collections.shuffle(deck);
            discard = new ArrayList<>();
            drawCardsFromDeck(damageCounters);
        }
        newCardsDelivered = true;
        state = PLAYERSTATE.PICKING_CARDS;
    }

    // Register cards from chosenCards as cards of choice to send
    public abstract void registerChosenCards();

    /**
     * Add selected card from hand to chosenCards
     */
    public void chooseCards(int cardSelection){
        if (hand.get(cardSelection).getCardType() == CardType.NONE) return;
        hand.get(cardSelection).picked = true;
    }

    // Starts next turn, everyone draws cards.
    // In Game.java this function is used only in GameHost, but since GameClient and GameHost is
    // initialized from this class, we need this as an abstract function here which makes GameClient
    // have an empty function, but this seems like the best solution to this problem for now.
    public abstract void drawCards();

    // return updated map if client, send new map to clients if host
    public abstract Map updateMap(Map mlp);
    // Set local map
    public abstract void setMap(Map mlp);
}
