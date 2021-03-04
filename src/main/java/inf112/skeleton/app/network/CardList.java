package inf112.skeleton.app.network;

import inf112.skeleton.app.game.objects.Card;

import java.util.List;

/**
 * Can't send lists over net with kryonet so
 * have to use this wrapper class instead.
 */
public class CardList {

    public List<Card> cardList;
}
