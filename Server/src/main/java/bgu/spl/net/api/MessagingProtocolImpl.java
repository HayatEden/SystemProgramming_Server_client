package bgu.spl.net.api;

import bgu.spl.net.impl.rci.CommandImpl;

public class MessagingProtocolImpl implements MessagingProtocol<CommandImpl> {

    private boolean shouldTerminate;
    private String userName = ""; // holds what is the username this protocol connects with and indicates it is logged in

    @Override
    public CommandImpl process(CommandImpl command) {
        boolean actionWorked = command.execute(userName);
        if (actionWorked){
            if (command.getOpcode() == 3){ // user logged in
                userName = command.getUserName();
            }
            else if (command.getOpcode() == 4){ // the client sent logout and therefore connection needs to end
                userName = "";
                shouldTerminate = true;
            }
        }
        return command;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
