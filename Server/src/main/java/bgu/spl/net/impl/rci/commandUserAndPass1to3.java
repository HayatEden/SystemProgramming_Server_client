package bgu.spl.net.impl.rci;

import bgu.spl.net.impl.BGRSServer.Database;

public class commandUserAndPass1to3 extends CommandImpl {

    public commandUserAndPass1to3() {
        super();
    }


    @Override
    public boolean execute(String userName) {
        boolean actionWorked = false;
        if (opcode == 1 && userName == "") { // the message recieved is ADMINGREG
            String check = this.getUserName();
            actionWorked = Database.getInstance().registerAdmin(this.getUserName(), password);
        } else if (opcode == 2 && userName == "") {// the message recieved is STUDENTREG
            actionWorked = Database.getInstance().registerStudent(this.getUserName(), password);
        } else if (userName == ""){// the message received is LOGIN and this client is not logged in yet
            actionWorked = Database.getInstance().loginRequest(this.getUserName(), password);
        }
        if (actionWorked) {
            setERR(false);
        }
        return actionWorked;
    }
}