package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandCourseReg5 extends CommandImpl {

    public commandCourseReg5(){
        super();
    }

    @Override
    public boolean execute(String userName) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        if(Database.getInstance().registerToCourse(userName,getCourseNum())){
            setERR(false);
            return true;
        }
        return false;
    }
}