package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandStudentStatus8 extends CommandImpl {
    private String courseList;

    public commandStudentStatus8(){
        super();
        courseList = new String();
    }

    @Override
    public boolean execute(String username) {

        if (userName == ""){ // student hasn't logged in
            return false;
        }
        courseList = Database.getInstance().listOfCoursesOfStudent(getUserName(),username, false); // sends the students name and the admins name
        if (courseList != null){
            setERR(false);
            return true;
        }
        return false;
    }

    public String getCourseList() {
        return courseList;
    }


}
