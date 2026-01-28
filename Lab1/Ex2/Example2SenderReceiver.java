package mtu_distributed_computing.Lab1.Ex2;

import java.net.InetAddress;
import java.util.Scanner;


public class Example2SenderReceiver {
    public static void main(String[] args) {
         try { 
            
            int localPort = 2001;
  		      InetAddress receiverHost = InetAddress.getByName("127.0.0.1");
            int receiverPort = 2000;
  	
   	      MyDatagramSocket mySocket = new MyDatagramSocket(localPort);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter a message to send or 'exit' to quit");
            

            while (true) {
               System.out.println("Message: ");
               String message = scanner.nextLine();

               if (message.equals("exit")) {
                  break;
               }

               mySocket.sendMessage(receiverHost, receiverPort, message);

               String receivedMessage = mySocket.receiveMessage(); 
               System.out.println("Received Message: " + receivedMessage);
            }

            mySocket.close();
            scanner.close();
         } 
	 catch (Exception ex) {
       ex.printStackTrace( );
	 }
      } 
   } 