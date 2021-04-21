package inf112.skeleton.app.ui.chat.backend;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;

public class Uwufier {
    private String[] emojiis = {"^.^", ":3", ">_<", "x3", "(U w U)", "ony-chan", "UwU", "OwO"};
    public boolean smolify = true; // convert words like "small" to "smol"
    public boolean L_R_replace = true; // replace "L" and "R" with "W"
    public boolean stutter = true; // Stutter sometimes "Hi" -> "H-Hi"
    public boolean emojiAfterPunc = true; // Occasionally add emoji after punctiation (!, ., ,)

    public List<Message> preUwudMessages;
    public List<Message> postUwudMessages;

    public Uwufier(List<Message> messagesToUwufy){
        preUwudMessages = messagesToUwufy;
        postUwudMessages = preUwudMessages;
        for (Message m : postUwudMessages){
            m.message = uwufyString(m.message);
        }
    }

    public String uwufyString(String s){
        String result = "";
        char[] stringCharArray = s.toCharArray();
        Random rand = new Random();
        boolean stutterOnNext = false;

        // Modify result to follow rules that can be decided with one char
        for (char c : stringCharArray){
            String app = "";

            // Check if adding emoji this iteration incase punctuation
            boolean emojiThisIteration = false;
            // Check if rolled to stutter
            boolean stutterQualified = false;
            if (rand.nextInt(100) > 60){
                stutterQualified = true;
                emojiThisIteration = true;
            }

            if (stutterOnNext && stutterQualified){
                app += c+"-";
            }

            switch (c){
                case ',': // ,
                case '.': // or .
                case '!': // or !
                case ':': // or :
                    stutterOnNext = false;
                    app += c + " ";
                    app += emojiThisIteration ? emojiis[rand.nextInt(emojiis.length-1)] : "";
                    break;
                case 'l':
                case 'r':
                    stutterOnNext = false;
                    app += 'w';
                    break;
                case 'L':
                case 'R':
                    stutterOnNext = false;
                    app += 'W';
                    break;
                case ' ':
                    app += c + "";
                    stutterOnNext = true;
                    break;
                default:
                    stutterOnNext = false;
                    app += c + "";
                    break;

            }
            result += app;
        }
        return result;
    }
}
