import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class ClientHelper {
    MyStreamSocket mySocket;

    public ClientHelper(String hostName, String portNum) throws Exception {
        int serverPort = Integer.parseInt(portNum);

        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(hostName, serverPort);
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
