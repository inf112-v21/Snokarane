package inf112.skeleton.app.ui.chat;

public class CommandParser {
    public enum Command{
        SETNAME,
        SETCOLOR,
        SETFONTSTCALE,
        INVALID
    }

    public String getCmd(String cmd){
        String stringCmd = "";

        for (Character c : cmd.toCharArray()){
            if (c == ' '){
                return stringCmd;
            }else {
                stringCmd += c;
            }
        }
        return stringCmd;
    }

    public String getArgs(String cmd){
        String stringArg = "";
        boolean atArgs = false;

        for (Character c : cmd.toCharArray()){
            if (c.equals(' ')){
                atArgs = true;
            }else {
                if (atArgs){
                    stringArg+=c;
                }
            }
        }
        return stringArg;
    }
    public Command parseCommand(String cmd){
        Command command = Command.INVALID;

        switch (cmd){
            case "set-name":
                return Command.SETNAME;
            case "chat-color":
                return Command.SETCOLOR;
            case "font-scale":
                return Command.SETFONTSTCALE;
            default:
                break;
        }

        return command;
    }
}
