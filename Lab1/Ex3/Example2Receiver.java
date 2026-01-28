package mtu_distributed_computing.Lab1.Ex3;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Example2Receiver {
    public static void main(String[] args) {
    if (args.length != 2)
        System.out.println
        ("This program requires two command line arguments");
        else {
            try {
                int portNo = Integer.parseInt(args[0]);
                String message = args[1];
                ServerSocket connectionSocket = new ServerSocket(portNo); // (ServerSocket = ConnectionSocket)
                System.out.println("now ready accept a connection on port: " + portNo);

                Socket dataSocket = connectionSocket.accept(); // when awake, automatically connects to the client (Socket = DataSocket)
                System.out.println("connection accepted, new data socket on port: " + dataSocket.getLocalPort());

                OutputStream outStream = dataSocket.getOutputStream(); //returns output

                PrintWriter socketOutput = new PrintWriter(new OutputStreamWriter(outStream)); // do not need to work with bytes, return string
                socketOutput.println(message);
                socketOutput.flush(); // make sure everything is sent, even if the buffer is not full

                System.out.println("message sent");
                dataSocket.close( );
                System.out.println("data socket closed");
                connectionSocket.close( );
                System.out.println("connection socket closed");
            } catch (Exception ex) {
                ex.printStackTrace( );
            } 
        } 
    } 
}
