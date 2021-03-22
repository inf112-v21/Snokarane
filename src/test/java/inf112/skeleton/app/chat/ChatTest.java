package inf112.skeleton.app.chat;

import inf112.skeleton.app.network.NetworkClient;
import inf112.skeleton.app.network.NetworkHost;
import inf112.skeleton.app.ui.chat.managers.ChatClient;
import inf112.skeleton.app.ui.chat.managers.ChatManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChatTest {

    ChatClient client;
    ChatManager manager;

    NetworkClient nClient;
    NetworkHost nHost;

    @Before
    public void setUp()  {
        nClient = new NetworkClient();
        nHost = new NetworkHost();
        nHost.isHost = true;
        nClient.isHost = false;

        nHost.initialize();
        nClient.initialize();
        nClient.giveNickname("gay");

        nClient.connectToServer("localhost");
        nHost.initConnections();

        client = new ChatClient();
        manager = new ChatManager();

        manager.setName("Wrangle");
        manager.setChatterID(0);
        client.setName("Tonga");
        client.setChatterID(1);

        manager.initializeMessageList();
    }

    @Test
    public void firstManagerMessageIsEmpty(){
        assertEquals("", manager.messages.get(0).message);
    }

    @Test
    public void firstManagerMessageHasId0(){
        assertEquals(0, manager.messages.get(0).messageID);
    }

    @Test
    public void firstManagerMessageHasSenderHost(){
        assertEquals(manager.getChatterData(), manager.messages.get(0).sender);
    }

    @Test
    public void messageFromClientIsSent() throws InterruptedException {
        client.sendMessage(" ", nClient);

        // have to let kryonet do its thing
        Thread.sleep(1500);

        manager.getAllMessages(nHost);

        assertEquals(1, nHost.getMessages().size());
        manager = new ChatManager();
        manager.setName("Wrangle");
        manager.setChatterID(0);
        manager.initializeMessageList();
    }

    @Test
    public void messageIsReceivedFromClient() throws InterruptedException {
        int previousMessagesCount = nHost.getMessages().size();
        client.sendMessage("", nClient);

        // have to let kryonet do its thing
        while (nHost.getMessages().size() <= previousMessagesCount){
            client.sendMessage("", nClient);
        }

        manager.getAllMessages(nHost);

        assertEquals(2, manager.messages.size());
        manager = new ChatManager();
        manager.setName("Wrangle");
        manager.setChatterID(0);
        manager.initializeMessageList();
    }

    @Test
    public void correctMessageIsReceivedFromClient() throws InterruptedException {
        String testMessage = "test";
        client.sendMessage(testMessage, nClient);

        // have to let the kryonet do its thing
        Thread.sleep(500);

        manager.getAllMessages(nHost);

        assertEquals("test", manager.messages.get(manager.messages.size()-1).message);
        manager = new ChatManager();
        manager.setName("Wrangle");
        manager.setChatterID(0);
        manager.initializeMessageList();
    }
}
