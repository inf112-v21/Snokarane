package inf112.skeleton.app;

import inf112.skeleton.app.game.objects.Flag;
import inf112.skeleton.app.game.objects.PlayerToken;
import inf112.skeleton.app.libgdx.Map;
import inf112.skeleton.app.libgdx.NetworkDataWrapper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class MapTest {
    Map map;

    @Before
    public void init() {
        this.map = new Map();
    }

    @Test
    public void registerFlagRegistersFlags() {
        List<PlayerToken> playerTokenList = new ArrayList<>();
        playerTokenList.add(new PlayerToken());
        playerTokenList.add(new PlayerToken());
        playerTokenList.get(1).position.x = 1;
        playerTokenList.get(1).position.y = 1;

        map.flagList = new ArrayList<>();
        map.flagList.add(new Flag(0, 0));

        map.registerFlags(playerTokenList);

        assertEquals(1, playerTokenList.get(0).getVisitedFlags().size());
        assertEquals(0, playerTokenList.get(1).getVisitedFlags().size());
    }

    @Test
    public void winCheckReturnsWinner() {
        List<PlayerToken> playerTokenList = new ArrayList<>();
        playerTokenList.add(new PlayerToken());
        playerTokenList.add(new PlayerToken());
        playerTokenList.get(1).position.x = 1;
        playerTokenList.get(1).position.y = 1;

        map.flagList = new ArrayList<>();
        map.flagList.add(new Flag(0, 0));
        map.registerFlags(playerTokenList);

        assertEquals(playerTokenList.get(0), map.hasWon(playerTokenList));
        map.flagList.add(new Flag(1, 1));
        assertEquals(null, map.hasWon(playerTokenList));
    }

    @Test
    public void loadPlayerLoadsPlayers() {
        List<PlayerToken> playerTokenList = new ArrayList<>();
        playerTokenList.add(new PlayerToken());
        playerTokenList.add(new PlayerToken());
        playerTokenList.get(1).position.x = 1;
        playerTokenList.get(1).position.y = 1;
        playerTokenList.get(0).charState = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
        playerTokenList.get(1).charState = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;

        NetworkDataWrapper wrapper = new NetworkDataWrapper();
        wrapper.PlayerTokens = playerTokenList;

        map.loadPlayers(wrapper.PlayerTokens);

        assertEquals(playerTokenList.get(0).charState, map.playerLayer[0][0].state);
        assertEquals(playerTokenList.get(1).charState, map.playerLayer[1][1].state);
        assertEquals(PlayerToken.CHARACTER_STATES.NONE, map.playerLayer[2][2].state);
    }

    @Test
    public void clearPlayerLayerClearsPlayerLayer() {
        List<PlayerToken> playerTokenList = new ArrayList<>();
        playerTokenList.add(new PlayerToken());
        playerTokenList.add(new PlayerToken());
        playerTokenList.get(1).position.x = 1;
        playerTokenList.get(1).position.y = 1;
        playerTokenList.get(0).charState = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;
        playerTokenList.get(1).charState = PlayerToken.CHARACTER_STATES.PLAYERSELFNORMAL;

        NetworkDataWrapper wrapper = new NetworkDataWrapper();
        wrapper.PlayerTokens = playerTokenList;

        map.loadPlayers(wrapper.PlayerTokens);

        map.clearPlayerLayer();

        assertEquals(PlayerToken.CHARACTER_STATES.NONE, map.playerLayer[0][0].state);
        assertEquals(PlayerToken.CHARACTER_STATES.NONE, map.playerLayer[1][1].state);
    }
}
