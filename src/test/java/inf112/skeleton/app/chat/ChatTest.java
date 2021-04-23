package inf112.skeleton.app.chat;

import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.chat.managers.ChatClient;
import inf112.skeleton.app.ui.chat.managers.ChatManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

// TODO i need more tests
public class ChatTest {

    ChatClient client;
    ChatManager manager;

    NetworkClient nClient;
    NetworkHost nHost;

    @Before
    public void setUp(){
        nClient = new NetworkClient();
        nHost = new NetworkHost();
        nHost.isHost = true;
        nClient.isHost = false;

        nHost.initialize();
        nClient.initialize();
        nClient.giveNickname("gay");

        nClient.connectToServer("localhost");
        nHost.initConnections();

        client = new ChatClient(nClient);
        manager = new ChatManager(nHost);

        manager.setName("Wrangle");
        manager.setChatterID(0);
        client.setName("Tonga");
        client.setChatterID(1);
    }

    @Test
    public void messageFromClientIsSent() throws InterruptedException {
        client.sendMessage("rat");

        // have to let kryonet do its thing
        Thread.sleep(500);

        assertEquals(2, nHost.messagesRecived.size());
    }
}
