package mtu_distributed_computing.Lab1.Ex2;
import java.net.InetAddress;
import java.util.Scanner;

public class Example2ReceiverSender {
    public static void main(String[] args) {
        try{

			   int localPort = 2000;
            int senderPort = 2001;

            MyDatagramSocket socket = new MyDatagramSocket(localPort);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Waiting to receive a message or 'exit' to quit");

            while (true) {
               String receivedMessage = socket.receiveMessage().trim();
               System.out.println("Received: " + receivedMessage);

               System.out.print("Reply: ");
               String reply = scanner.nextLine();

               if (reply.equalsIgnoreCase("exit")) break;

               InetAddress senderHost = InetAddress.getLocalHost();

               socket.sendMessage(senderHost, senderPort, reply);
            }

            socket.close();
            scanner.close();
         } 
	 catch (Exception ex) {
        ex.printStackTrace( );
	 }
      } 
   } 
