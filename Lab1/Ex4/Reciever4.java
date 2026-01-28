package mtu_distributed_computing.Lab1.Ex4;

import java.net.ServerSocket;
import java.net.Socket;

public class Reciever4 {
    public static void main(String[] args) {
        if (args.length != 2)
        System.out.println
        ("This program requires two command line arguments");
        else {
            try {
                int portNo = Integer.parseInt(args[0]);
                String message = args[1];
                
                ServerSocket connectionSocket = new ServerSocket(portNo); 
                Socket dataSocket = connectionSocket.accept();

                MyStreamSocket mySocket = new MyStreamSocket(dataSocket);

                mySocket.sendMessage(message);
                mySocket.close( );
                connectionSocket.close( );
            } catch (Exception ex) {
                ex.printStackTrace( );
            } 
        }
    }   
}
