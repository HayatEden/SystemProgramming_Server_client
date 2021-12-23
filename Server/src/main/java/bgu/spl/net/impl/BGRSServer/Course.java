package bgu.spl.net.impl.BGRSServer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Course { // a class that holds all the information about a certain course

    private Integer courseNum;
    private String courseName;
    private List<Integer> kdamCoursesList;
    private Integer numOfMaxStudents;
    private Integer seatsAvailable;
    private LinkedList<String> registeredStudents;

    public Course(int courseNum, String courseName, List<Integer> kdamCoursesList, int numOfMaxStudents) {
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.kdamCoursesList = kdamCoursesList;
        this.numOfMaxStudents = numOfMaxStudents;
        seatsAvailable = numOfMaxStudents;
        registeredStudents = new LinkedList<String>();
    }

    public Integer getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<Integer> getKdamCoursesList() {
        return kdamCoursesList;
    }

    public Integer getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public Integer getSeatsAvailable() {
        return seatsAvailable;
    }

    public LinkedList<String> getRegisteredStudents() {
        return registeredStudents;
    }

    public boolean addRegisteredStudents(Student studentToAdd) {
        if (updateSeatsAvailable()){ // there were seats avaliable
            String username = studentToAdd.getUserName();
            synchronized (registeredStudents){
                registeredStudents.add(username);
            }
            return true;
        }
        return false;

    }

    public synchronized boolean updateSeatsAvailable() {
        if (seatsAvailable == 0) { // course is full
            return false;
        }
        seatsAvailable--;
        return true;
    }


    public void Unregister(Student student){
        registeredStudents.remove(student.getUserName());
        seatsAvailable++;
    }

}