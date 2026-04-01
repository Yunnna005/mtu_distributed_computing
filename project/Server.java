import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {

    static ArrayList<String> messageStored = new ArrayList<String>();

    public static void main(String[] args) {
        int serverPort = 7;
        if(args.length == 1) {
            serverPort = Integer.parseInt(args[0]); 
        }

        String keystoreName = "serverkeystore.jks";
        String keystorePassword = "password";
        String keyPassword = "password";

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystoreName), keystorePassword.toCharArray());
            
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyPassword.toCharArray());
            
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket myConnectionSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort);
            System.out.println("Server ready on port " + serverPort + ". PLease wait for connections...");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) myConnectionSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                MyStreamSocket myStreamSocket = new MyStreamSocket(clientSocket);
                Thread clientThread = new Thread(new ServerThread(myStreamSocket));
                clientThread.start();
            }
        } catch (Exception ex) {
            System.out.println("Error starting server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static synchronized void addMessage(String message) {
        messageStored.add(message);
    }

    public static synchronized ArrayList<String> getAllMessages() {
        return new ArrayList<String>(messageStored);
    }

    public static synchronized String getMessage(int index) {
        if (index >= 0 && index < messageStored.size()) {
            return messageStored.get(index);
        }
        return null;
    }

    public static synchronized int getMessageCount() {
      return messageStored.size();
   }
 
}