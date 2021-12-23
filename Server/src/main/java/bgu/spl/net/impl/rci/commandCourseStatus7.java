package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Course;
import bgu.spl.net.impl.BGRSServer.Database;

import java.util.ArrayList;
import java.util.LinkedList;

public class commandCourseStatus7 extends CommandImpl {
    private String courseName;
    private String numOfMaxStudents;
    private String seatsAvailable;
    private LinkedList<String> registeredStudents;

    public commandCourseStatus7(){
        super();
    }

    @Override
    public boolean execute(String userName) {
        if (userName == ""){ // student hasn't logged in
            return false;
        }
        registeredStudents = Database.getInstance().getRegisteredStudents(userName,getCourseNum());
        if(registeredStudents==null){//course doesn't exsits or it is not an admin who requested it
            return false;
        }
        Course course = Database.getInstance().getCoursesList().get(getCourseNum());
        numOfMaxStudents = course.getNumOfMaxStudents().toString();
        seatsAvailable = course.getSeatsAvailable().toString();
        courseName = course.getCourseName();
        setERR(false);
        return true;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public String getSeatsAvailable() {
        return seatsAvailable;
    }

    public LinkedList<String> getRegisteredStudents() {
        return registeredStudents;
    }
}