package inf112.skeleton.app.ui.chat.backend;

/**
 * Data for user that is using chat
 */
public class ChatterData {
    public String name;
    public int chatterID;

    public ChatterData(){}

    public void setData(String name, int id){
        this.name = name;
        this.chatterID = id;
    }
}
