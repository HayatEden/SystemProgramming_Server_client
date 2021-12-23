package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandCheckKdam6 extends CommandImpl {

    private String kdamCoursesList;

    public commandCheckKdam6(){
        super();
    }

    @Override
    public boolean execute(String userName) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        kdamCoursesList=Database.getInstance().getKdamCourses(getCourseNum());
        if (kdamCoursesList == null){
            return false;
        }
        setERR(false);
        return true;
    }

    public String getKdamCoursesList() {
        return kdamCoursesList;
    }
}