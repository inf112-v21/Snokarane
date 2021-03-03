package inf112.skeleton.app.game;

import com.esotericsoftware.kryonet.Connection;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;

import java.util.ArrayList;

/**
 * Elements that a player in RoboRally can interact with
 * f.ex. picking a card
 */                               //Needs to extend this to call functions over the net
public abstract class GamePlayer{

    public PLAYERSTATE state = PLAYERSTATE.NONE;

    public ArrayList<Card> chosenCards = new ArrayList<>();
    ArrayList<Card> hand = new ArrayList<>();
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<Card> discard = new ArrayList<>();

    /**
     * Give player a stack of cards to deck
     */
    public void GamePlayer(){
        // Create deck TODO select random items from CardType
        for (int i = 0; i<500; i++){
            Card card = new Card();
            card.setCardType(CardType.FORWARDONE);
            deck.add(card);
        }
    }

    /**
     * Adds cards to hand from deck, and sets playerstate to PICKING_CARDS
     */
    public void drawCardsFromDeck(){
        for (int i = 0; i<9; i++){
            hand.add(deck.remove(0));
        }
        state = PLAYERSTATE.PICKING_CARDS;
    }

    // Register cards from chosenCards as cards of choice :)
    public abstract void registerChosenCards();

    public void chooseCards(int cardSelection){
        // TODO remove cards
        chosenCards.add(deck.get(cardSelection));
    }

    // Starts next turn, everyone draws cards.
    // In Game.java this function is used only in GameHost, but since GameClient and GameHost is
    // initialized from this class, we need this as an abstract function here which makes GameClient
    // have an empty function, but this seems like the best solution to this problem for now.
    public abstract void drawCards();

    public enum PLAYERSTATE{
        WAITING,
        RECIEVING_CARDS,
        PICKING_CARDS,
        SENDING_CARDS,
        NONE
    }
}
