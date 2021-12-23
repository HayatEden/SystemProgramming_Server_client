package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandUnregister10 extends CommandImpl {

    private boolean UnregWorked;

    public commandUnregister10(){
        super();
    }

    @Override
    public boolean execute(String username) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        UnregWorked = Database.getInstance().Unregister(username,getCourseNum());
        if (UnregWorked){ // student is not registered to the course so no unregister is needed
            setERR(false);
        }
        return UnregWorked;
    }
}
