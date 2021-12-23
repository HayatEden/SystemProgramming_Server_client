package bgu.spl.net.impl.BGRSServer;

import java.util.LinkedList;

public class Student{
    private String userName;
    private String password;
    private LinkedList<Integer> courseList;


    public Student(String userName, String password){
        this.userName = userName;
        this.password = password;
        courseList = new LinkedList<>();
    }

    public void addCourse(Course courseToAdd){
        courseList.add(courseToAdd.getCourseNum());
    }
    public String getUserName(){
        return userName;
    }
    public String getPassword(){
        return password;
    }


    public LinkedList<Integer> getCourseList() {
        return courseList;
    }


}