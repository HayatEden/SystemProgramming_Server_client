package bgu.spl.net.impl.BGRSServer;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    /**
     * Passive object representing the Database where all courses and users are stored.
     * <p>
     * This class must be implemented safely as a thread-safe singleton.
     * You must not alter any of the given public methods of this class.
     * <p>
     * You can add private fields and methods to this class as you see fit.
     */
    private static class DatabaseHolder{
        private static Database instance = new Database();
    }
    private ConcurrentHashMap<Integer, Course> coursesList;
    private ConcurrentHashMap<String, Student> students;
    private ConcurrentHashMap<String, Admin> admins;
    private ConcurrentHashMap<String,Object> whosLoggedIn;
    private LinkedList<Integer> coursesAsInt;
    private Object lock1;
    private Object lock2;

    //to prevent user from creating new Database
    private Database() {
        coursesList = new ConcurrentHashMap<Integer,Course>();
        students = new ConcurrentHashMap<String, Student>();
        admins = new ConcurrentHashMap<String, Admin>();
        coursesAsInt = new LinkedList<Integer>();
        whosLoggedIn = new ConcurrentHashMap<String,Object>();
        lock1 = new Object();
        lock2 = new Object();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseHolder.instance;
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    public boolean initialize(String coursesFilePath) {
        File file = new File(coursesFilePath);
        try {
            Scanner scanner = new Scanner(new FileReader(file));
            String[] currentLine;
            while (scanner.hasNextLine()) {
                LinkedList<Integer> kdamCourses = new LinkedList<>();
                currentLine = scanner.nextLine().split("[|]");

                //initialzing a new course according to Courses.txt information
                Course course = new Course(Integer.parseInt(currentLine[0]),currentLine[1],kdamCourses,Integer.parseInt(currentLine[3]));

                if (hasKdamCourses(currentLine[2])){ // check if this course has kdam courses, if so add them to kdamCourses
                    String check = currentLine[2].substring(1,currentLine[2].length()-1);
                    String[] currKdam = check.split("[,]");
                    for (int i = 0; i < currKdam.length;i++){
                        kdamCourses.addLast(Integer.parseInt(currKdam[i]));
                    }
                }
                coursesList.put(Integer.parseInt(currentLine[0]),course); // add the new course to the hashmap
                coursesAsInt.add(Integer.parseInt(currentLine[0]));
            }
        }catch (FileNotFoundException fileNotFoundException){};
        return true;
    }

    private boolean hasKdamCourses(String kdamCoursesList){
        return kdamCoursesList.length() != 2;
    }

    public ConcurrentHashMap<Integer, Course> getCoursesList() {
        return coursesList;
    }

    public boolean registerStudent(String userName, String password){
        synchronized (lock1) {
            if (students.containsKey(userName) || admins.containsKey(userName)) { // username already exists
                return false;
            }
            Student newStudent = new Student(userName, password);
            students.put(userName, newStudent);
            return true;
        }
    }

    public boolean registerAdmin(String userName, String password){
        synchronized (lock1) {
            if (admins.containsKey(userName) || students.containsKey(userName)) { // username already exists
                return false;
            }
            Admin newAdmin = new Admin(userName, password);
            admins.put(userName, newAdmin);
            return true;
        }
    }
    public boolean loginRequest(String userName, String password) {
        synchronized (lock2) {
            Admin tempAdmin = admins.get(userName);
            Student tempStudent = students.get(userName);
            if (tempStudent == null && tempAdmin == null) { // for case that the username isn't register yet
                return false;
            } else if (tempStudent != null) {//is a student
                String currPass = tempStudent.getPassword();
                if (currPass.equals(password) && !whosLoggedIn.containsKey(userName)) { // no other client is currently logged in to this user and pass is correct
                    whosLoggedIn.put(userName, new Object());
                    return true;
                }
            } else {//is an admin
                String currpass = tempAdmin.getPassword();
                if (currpass.equals(password) && !whosLoggedIn.containsKey(userName)) { // no other client is currently logged in to this user and pass is correct
                    whosLoggedIn.put(userName, new Object());
                    return true;
                }
            }
            return false;
        }
    }


    public boolean logoutRequest(String userName) {
        Admin tempAdmin = admins.get(userName);
        Student tempStudent = students.get(userName);
        if(tempStudent == null && tempAdmin==null) { // for the case that the username isn't register yet
            return false;
        }
        whosLoggedIn.remove(userName);
        return true;
    }

    public boolean registerToCourse(String username, int courseNum){
        if (!students.containsKey(username)){ // username doesn't exists
            return false;
        }
        Student student = students.get(username);
        if (student != null){ // student exists
            Course course = coursesList.get(courseNum);
            if (course != null){ // course exists
                for (Integer c:course.getKdamCoursesList()) { // check if student has registered to all kdam courses
                    if (!student.getCourseList().contains(c)){
                        return false;
                    }
                }
                if (checkIfRegistered(courseNum, username)) {  //student is already registered to this course
                    return false;
                }
                boolean regWorked = course.addRegisteredStudents(student); // add student to course's list
                if (regWorked){ // there were an available seat
                    student.addCourse(course); // add course to student's courses list
                    return true;
                }

            }
        }
        return false;
    }


    public String getKdamCourses(int courseNum){
        if (coursesList.get(courseNum) == null){ // course doesn't exist
            return null;
        }
        List<Integer> kdamCourseInt = coursesList.get(courseNum).getKdamCoursesList();
        String kdamCourseStr = '\n' + "[";
        for (int i = 0; i < kdamCourseInt.size(); i++){
            kdamCourseStr = kdamCourseStr + kdamCourseInt.get(i).toString() + ",";
        }
        if (kdamCourseInt.size() == 0){ // no kdam courses
            kdamCourseStr = kdamCourseStr + "]";
        }
        else {
            kdamCourseStr = kdamCourseStr.substring(0,kdamCourseStr.length()-1) + "]";
        }
        return kdamCourseStr;
    }

    public LinkedList<String> getRegisteredStudents(String username, int courseNum){
        if (admins.get(username) != null) { // it is admin who requested this action
            Course course = coursesList.get(courseNum);
            if (course != null){ // course exists
                LinkedList<String> registeredStudents = course.getRegisteredStudents();
                Collections.sort(registeredStudents);
                return registeredStudents;
            }
        }
        return null;
    }


    public String listOfCoursesOfStudent(String studentName, String adminName, boolean itIsMYCOURSES){
        Student student = students.get(studentName);
        if (student != null) { // student exists
            boolean actionApproved = false;
            if (itIsMYCOURSES) { // it is command11 and can be initiated by a student
                actionApproved = true;
            } else if (adminName != null && admins.containsKey(adminName)) { // it is command8 and can only be initiated by an admin
                actionApproved = true;
            }
            if (actionApproved) {
                LinkedList<Integer> studentsCoursesList = student.getCourseList();
                String StudentCoursesListStr = "[";
                for (int i = 0; i < coursesAsInt.size(); i++) { // create a string sorted as courses.txt
                    if (studentsCoursesList.contains(coursesAsInt.get(i))) {
                        StudentCoursesListStr = StudentCoursesListStr + coursesAsInt.get(i).toString() + ',';
                    }
                }
                if (studentsCoursesList.size() == 0) { // student isn't registered to any courses
                    StudentCoursesListStr = StudentCoursesListStr + "]";
                } else {
                    StudentCoursesListStr = StudentCoursesListStr.substring(0, StudentCoursesListStr.length() - 1) + "]";
                }
                return StudentCoursesListStr;
                }
            }
        return null;
    }

    public boolean checkIfRegistered(int courseNum, String username){
        Student student = students.get(username);
        if (student != null){ // it is a valid student
            if (student.getCourseList().contains(courseNum)){ // student is registered to this course
                return true;
            }
        }
        return false;
    }

    public boolean Unregister(String username, int courseNum){
        Student student = students.get(username);
        if (student != null) { //it is a valid student
            LinkedList<Integer> studentCourses = student.getCourseList();
            if (studentCourses.contains(courseNum)) { // student is registered to this course
                int index = studentCourses.indexOf(courseNum);
                student.getCourseList().remove(index); // remove course from student's courses list
                coursesList.get(courseNum).Unregister(student); // remove Student from Course's registeredStudentsList
                return true;
            }
        }
        return false;
    }



}

