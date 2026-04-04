import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

/**
* Class responsible for handling all of the logic related to creating the protocol messages and parsing the responses from the server. 
* Presentation logic will be contained within Client.java, will call on methods of this class.
*
* The client uses SSL to connect with the server. 
* Using an SSL connection requires a Trust Store. 
* The Trust Store is passed into the program via the Command Line when the client runs.
*
* References:
* M. L. Liu, EchoClientHelper2.java, class materials
* Lab 2 SSL 2023, class materials
* Oracle, "SSLSocketFactory (Java SE 8)", https://docs.oracle.com/javase/8/docs/api/javax/net/ssl/SSLSocketFactory.html
**/


public class ClientHelper {
    MyStreamSocket mySocket;

    //Constructor to create an SSL connection to the server
    public ClientHelper(String hostName, String portNum) throws Exception {
        int serverPort = Integer.parseInt(portNum);

        //Get the default SSL socket factory. This uses the trust store set in the system properties to verify the server's certificate.
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        
        //Create an SSL socket connected to the server
        SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(hostName, serverPort);
        
        //This is where the client and server exchange certificates and agree on encryption settings
        sslSocket.startHandshake();

        System.out.println("SSL connection established.");

        mySocket = new MyStreamSocket(sslSocket);
    }

    public String login(String username, String password) throws IOException {
        String request = "100 " + username + " " + password;
        mySocket.sendMessage(request);
    
        String response = mySocket.receiveMessage();
        return response;
    }

    public String uploadMessage(String messageText) throws IOException{
        String request = "200 " + messageText;
        mySocket.sendMessage(request);

        String response = mySocket.receiveMessage();
        return response;
    }

    public String downloadAll() throws IOException {
        mySocket.sendMessage("300");
 
        String response = mySocket.receiveMessage();
    
        if (response.startsWith("301")) {
            StringBuilder allMessages = new StringBuilder();
            allMessages.append(response);
            allMessages.append("\n");
    
            String line = mySocket.receiveMessage();
            while (line != null && !line.equals("END")) {
                allMessages.append(line);
                allMessages.append("\n");
                line = mySocket.receiveMessage();
            }
    
            return allMessages.toString();
        }
        return response; 
    }

    public String downloadOne(int index) throws IOException{
        String request = "350 " + index;
        mySocket.sendMessage(request);
    
        String response = mySocket.receiveMessage();
        return response;
    }

    public String logoff() throws IOException{
        mySocket.sendMessage("400");
    
        String response = mySocket.receiveMessage();
    
        mySocket.close();
    
        return response;
    }

}
