package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandCheckIfReg9 extends CommandImpl{

    private boolean isRegistered;

    public commandCheckIfReg9(){
        super();
    }

    @Override
    public boolean execute(String username) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        isRegistered = Database.getInstance().checkIfRegistered(getCourseNum(),username);
        setERR(false); // always receives ack
        if (isRegistered)
            return true;
        return false;
    }

    public boolean isRegistered() {
        return isRegistered;
    }


}
