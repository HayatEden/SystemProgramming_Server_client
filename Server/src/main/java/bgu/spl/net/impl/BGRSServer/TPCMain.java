package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.MessagingProtocolImpl;
import bgu.spl.net.srv.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TPCMain {

    public static void main (String[] args ) throws UnknownHostException{
        Database.getInstance().initialize("Courses.txt");
        int port = Integer.parseInt(args[0]);
        InetAddress localhost =InetAddress.getLocalHost();
        System.out.println("system IP Address: "+localhost.getHostAddress());
        Server.threadPerClient(port,()-> new MessagingProtocolImpl(),()-> new MessageEncoderDecoderImpl()).serve();
    }
}