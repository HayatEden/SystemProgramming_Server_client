package bgu.spl.net.impl.BGRSServer;

public class Admin{
    private String userName;
    private String password;

    public Admin(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
    public String getUserName(){
        return userName;
    }
    public String getPassword(){
        return password;
    }

}