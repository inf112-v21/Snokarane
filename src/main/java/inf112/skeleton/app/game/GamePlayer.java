package inf112.skeleton.app.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.esotericsoftware.kryonet.Connection;
import edu.emory.mathcs.backport.java.util.Arrays;
import inf112.skeleton.app.game.objects.Card;
import inf112.skeleton.app.game.objects.CardType;

import java.util.ArrayList;
import java.util.List;

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
    }

    /**
     * Adds cards to hand from deck, and sets playerstate to PICKING_CARDS
     */
    public void drawCardsFromDeck(){
        for (int i = 0; i<Math.min(9-hand.size(), deck.size()-hand.size()); i++){
            hand.add(deck.remove(0));
        }
        if (hand.size() != 9) {
            deck.addAll(discard);
            discard = new ArrayList<>();
            drawCards();
        }
        state = PLAYERSTATE.PICKING_CARDS;
    }

    // Register cards from chosenCards as cards of choice :)
    public abstract void registerChosenCards();

    public void chooseCards(int cardSelection){
        chosenCards.add(deck.get(cardSelection));
        //TODO: Should this be null, or should there be a 'null'-equivalent in the CardType enum?
        deck.set(cardSelection, null);
    }

    // Starts next turn, everyone draws cards.
    // In Game.java this function is used only in GameHost, but since GameClient and GameHost is
    // initialized from this class, we need this as an abstract function here which makes GameClient
    // have an empty function, but this seems like the best solution to this problem for now.
    public abstract void drawCards();

    // return updated map if client, send new map to clients if host
    public abstract TiledMap updateMap(TiledMap tiledMap);

    public enum PLAYERSTATE{
        WAITING,
        RECIEVING_CARDS,
        PICKING_CARDS,
        SENDING_CARDS,
        NONE
    }
}
