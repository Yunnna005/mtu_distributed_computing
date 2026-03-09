import java.net.*;
import java.util.*;

public class SMPServer {
    private static List<String> messages = new ArrayList<>();

    public static void main(String[] args) {
        int serverPort = 7;
        if(args.length == 1) {
            serverPort = Integer.parseInt(args[0]); 
        }

        try {
            ServerSocket myConnectionSocket = new ServerSocket(serverPort);
            System.out.println("Server ready on port " + serverPort); 

            while (true) {
                Socket myDataSocket = myConnectionSocket.accept();
                System.out.println("Connection accepted from " + myDataSocket.getInetAddress());
                Thread theThread = new Thread(new SMPServerThread(myDataSocket));
                theThread.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static synchronized void addMessage(String msg) {
        messages.add(msg);
    }

    public static synchronized String getMessage(int id) {
        if (id < 1 || id > messages.size()) return null;
        return messages.get(id - 1);
    }

    public static synchronized List<String> getAllMessages() {
        return new ArrayList<>(messages);
    }
}