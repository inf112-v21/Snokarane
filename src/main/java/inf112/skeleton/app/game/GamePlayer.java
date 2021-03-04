package inf112.skeleton.app.game;

import edu.emory.mathcs.backport.java.util.Arrays;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;
import inf112.skeleton.app.libgdx.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // Current state of gameplay of player
    public PLAYERSTATE state = PLAYERSTATE.NONE;

    // Cards selected to be sent to host for processing
    public ArrayList<Card> chosenCards = new ArrayList<>();
    // Cards to choose from in current turn
    public ArrayList<Card> hand = new ArrayList<>();
    // Where cards get selected from
    protected ArrayList<Card> deck = new ArrayList<>();
    // Cards that weren't selected
    protected ArrayList<Card> discard = new ArrayList<>();

    /**
     * Give player a stack of cards to deck
     */
    public GamePlayer(){
        //Move1, Move2, Move3, Back up, Rotate right, Rotate left, U-turn
        List<Integer> numOfEachCardType = Arrays.asList(new Integer[]{18, 12, 6, 6, 18, 18, 6});
        for(int i = 0; i < numOfEachCardType.size(); i++) {
            CardType cardType = CardType.values()[i];
            for (int j = 0; j < numOfEachCardType.get(i); j++) {
                Card card = new Card();
                card.setCardType(cardType);
                deck.add(card);
            }
        }
        Collections.shuffle(deck);
    }

    /**
     * Adds cards to hand from deck, and sets playerstate to PICKING_CARDS when added
     */
    public void drawCardsFromDeck(){
        int cardsToAdd = Math.min(9-hand.size(), deck.size()-hand.size());

        for (int i = 0; i<cardsToAdd; i++){
            hand.add(deck.remove(0));
        }
        if (hand.size() != 9) {
            deck.addAll(discard);
            Collections.shuffle(deck);
            discard = new ArrayList<>();
            drawCardsFromDeck();
        }
        state = PLAYERSTATE.PICKING_CARDS;
    }

    // Register cards from chosenCards as cards of choice to send
    public abstract void registerChosenCards();

    /**
     * Add selected card from hand to chosenCards
     */
    public void chooseCards(int cardSelection){
        if (hand.get(cardSelection) == null) return;
        chosenCards.add(hand.get(cardSelection));
        //TODO: Should this be null, or should there be a 'null'-equivalent in the CardType enum?
        hand.set(cardSelection, null);
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
