package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandMyCurrCourse11 extends CommandImpl{

    private String courseList;

    public commandMyCurrCourse11(){
        super();
        courseList = new String();
    }

    @Override
    public boolean execute(String username) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        courseList = Database.getInstance().listOfCoursesOfStudent(username,null, true); // null because it is not and admin request
        if (courseList != null){
            setERR(false);
            setUserName(username);
            return true;
        }

        return false;
    }

    public String getCourseList() {
        return courseList;
    }
}
