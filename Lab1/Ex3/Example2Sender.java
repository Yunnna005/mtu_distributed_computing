package mtu_distributed_computing.Lab1.Ex3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Example2Sender {
    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println
            ("This program requires two command line arguments");
        else {
            try {
                InetAddress acceptorHost = InetAddress.getByName(args[0]);
                int acceptorPort = Integer.parseInt(args[1]);

                Socket mySocket = new Socket(acceptorHost, acceptorPort); //creates connection
                System.out.println("Connection request granted to port: " + acceptorPort);
                
                InputStream inStream = mySocket.getInputStream();
                
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inStream)); //if nothing in a steam the code waits
                System.out.println("waiting to read");
                
                String message = socketInput.readLine( );
                System.out.println("Message received:");
                System.out.println("\t" + message);
                mySocket.close( );
                System.out.println("data socket closed");
            } catch (Exception ex) {
                ex.printStackTrace( );
            } 
        } 
    }
}
