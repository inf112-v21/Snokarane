package inf112.skeleton.app.libgdx;

import inf112.skeleton.app.game.objects.PlayerToken;

import java.util.ArrayList;
import java.util.List;

/**
 * This is to send player tokens to and from clients
 */
public class NetworkDataWrapper {
    public List<PlayerToken> PlayerTokens = new ArrayList<>();
}
