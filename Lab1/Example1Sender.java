package mtu_distributed_computing.Lab1;
import java.net.*;
import java.io.*;

public class Example1Sender {
   public static void main(String[] args) {
      if (args.length != 3)
         System.out.println
            ("This program requires three command line arguments");
      else {
         try {
  		      InetAddress receiverHost = InetAddress.getByName(args[0]);
  		      System.out.println("Host Name: " + receiverHost.getHostName());
  		      System.out.println("Host Address: " +receiverHost.getHostAddress());
  		      //System.out.println("canonical: " +receiverHost.getCanonicalHostName());
  		      System.out.println("to String: " +receiverHost.toString());
  		      int receiverPort = Integer.parseInt(args[1]);
            String message = args[2];
  	
   	      DatagramSocket	mySocket = new DatagramSocket();  
   	      System.out.println("Bound to port: " + mySocket.getLocalPort());        
            byte[ ] buffer = message.getBytes( );                                     
            DatagramPacket datagram = 
               new DatagramPacket(buffer, buffer.length, 
                                  receiverHost, receiverPort);
            mySocket.send(datagram);
            mySocket.close( );
         } 
	 catch (Exception ex) {
       ex.printStackTrace( );
	 }
      } 
   } 
} 

