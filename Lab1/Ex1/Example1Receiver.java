package mtu_distributed_computing.Lab1.Ex1;
import java.net.*;
import java.io.*;

public class Example1Receiver {

   public static void main(String[] args) {
      if (args.length != 1)
         System.out.println
            ("This program requires a command line argument.");
      else {
			int port = Integer.parseInt(args[0]);
         final int MAX_LEN = 12; 
			try {
   	      DatagramSocket	mySocket = new DatagramSocket(port);  
            byte[ ] buffer = new byte[MAX_LEN];                                     
            DatagramPacket datagram = 
               new DatagramPacket(buffer, MAX_LEN);
            mySocket.receive(datagram); 
            String message = new String(buffer);
            System.out.println(message);
            mySocket.close( );
         } 
	 catch (Exception ex) {
        ex.printStackTrace( );
	 }
      } 
   } 
} 
