package bgu.spl.net.impl.rci;

public abstract class CommandImpl implements Command<String> {

    protected short opcode;
    protected String userName;
    protected String password;
    protected Integer CourseNum;
    protected boolean isERR;

    public CommandImpl(){
        this.opcode=-1;
        this.userName=null;
        this.password=null;
        this.isERR = true;
    }

    public short getOpcode() {
        return opcode;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
    public Integer getCourseNum() {
        return CourseNum;
    }

    public boolean isERR() {
        return isERR;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setERR(boolean ERR) {
        isERR = ERR;
    }
    public void setCourseNum(int courseNum) {
        CourseNum = courseNum;
    }

}