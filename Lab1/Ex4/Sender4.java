package mtu_distributed_computing.Lab1.Ex4;

import java.net.InetAddress;

public class Sender4 {
    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println
            ("This program requires two command line arguments");
        else {
            try {
                InetAddress acceptorHost = InetAddress.getByName(args[0]);
                int acceptorPort = Integer.parseInt(args[1]);

                MyStreamSocket mySocket = new MyStreamSocket(acceptorHost.getHostName(), acceptorPort);

                String message = mySocket.receiveMessage( );    
                System.out.println("Message received:" + message);
                mySocket.close( );
            } catch (Exception ex) {
                ex.printStackTrace( );
            } 
        }
    }
}
