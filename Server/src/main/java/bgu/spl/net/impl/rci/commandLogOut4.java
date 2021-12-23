package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandLogOut4 extends CommandImpl {

    public commandLogOut4(){
        super();
    }

    @Override
    public boolean execute(String userName) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        if(Database.getInstance().logoutRequest(userName)){
            setERR(false);
            return true;
        }
        return false;
    }

}