import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.*;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
* Mainly this is the main class for the SMP System’s server. 
* The server will listen for incoming ssl connections by clients, then start up a new thread for each client. 
* So several clients can run on this server at the same time. 
* The server keeps messages within an ArrayList in memory. 
* I selected ArrayList as the data structure because it can grow dynamically, and it is very simple to access all the messages in the list when a client wishes to get them. 
* To use SSL the server has to have a keystore file (server_keystore.jks), which includes the server’s private key and its certificate. 
* The keystore is loaded at startup and used to establish an SSLServerSocket. 
* I selected TCP Stream Sockets versus UDP Datagrams, since the SMP Protocol requires dependable, sequential transmission of messages. 
* Using UDP, messages may get lost or received out of sequence, breaking the login/upload/download sequence. 
* TCP ensures that all messages are delivered and in the proper order. Also, both SSL/TLS operate on top of TCP and do not operate on UDP. 
* Therefore, TCP was necessary for the security requirement.
*
* References:
* M. L. Liu, EchoServer3.java, class materials
* SslReverseEchoer.java, Lab 2 SSL materials
* Oracle, "Java Secure Socket Extension (JSSE) Reference Guide", https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
*/

public class Server {

    static ArrayList<String> messageStored = new ArrayList<String>();

    public static void main(String[] args) {
        int serverPort = 7;
        if(args.length == 1) {
            serverPort = Integer.parseInt(args[0]); 
        }

        String keystoreName = "server_keystore.jks";
        String keystorePassword = "password";
        String keyPassword = "password";

        try {

            // Load the keystore and initialize the SSL context
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystoreName), keystorePassword.toCharArray());
            
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyPassword.toCharArray());
            
            // Create the SSL context and server socket
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create the SSL server socket and listen for connections
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