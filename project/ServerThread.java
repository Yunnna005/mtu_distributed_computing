import java.io.*;
import java.util.ArrayList;

public class ServerThread implements Runnable {
    MyStreamSocket myStreamSocket;
    String clientUsername;
    boolean loggedIn;

    public ServerThread(MyStreamSocket socket) {
        this.myStreamSocket = socket;
        this.clientUsername = "";
        this.loggedIn = false;
    }

    public void run() {
        boolean done = false;
        String message;

        try{
            while(!done){
                message = myStreamSocket.receiveMessage();
                if(message == null) {
                    System.out.println("Client disconnected: " + myStreamSocket.getInetAddress().getHostAddress());
                    done = true;
                    break;
                }

                System.out.println("Received from client: " + message);

                String[] parts = message.split(" ", 2);
                String code = parts[0];
                
                if (code.equals("100")){
                    handleLogin(parts);
                }else if(code.equals("200")){
                    handleUpload(parts);
                }else if (code.equals("300")) {
                    handleDownloadAll();
                }
                else if (code.equals("350")) {
                    handleDownloadOne(parts);
                }
                else if (code.equals("400")) {
                    handleLogoff();
                    done = true;
                }else{
                    myStreamSocket.sendMessage("500 Unknown command");
                    System.out.println("Unknown command received: " + code); 
                }
            }
        } catch (IOException ex) {
            System.out.println("Error in communication with client: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                myStreamSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }

    private void handleLogin(String[] parts) throws IOException {
        if (parts.length < 2) {
            myStreamSocket.sendMessage("102 Login failed, missing credentials");
            System.out.println("Login failed: no credentials provided");
            return;
        }
        String[] credentials = parts[1].split(" ", 2);

        if (credentials.length < 2) {
            myStreamSocket.sendMessage("102 Login failed, missing password");
            System.out.println("Login failed: missing password");
            return;
        }

        String username = credentials[0].trim();
        String password = credentials[1].trim();

        if (username.length() == 0 || password.length() == 0) {
            myStreamSocket.sendMessage("102 Login failed, empty credentials");
            System.out.println("Login failed: empty credentials");
            return;
        }

        this.clientUsername = username;
        this.loggedIn = true;
        myStreamSocket.sendMessage("101 Login successful");
        System.out.println("User logged in: " + username);
    }

    private void handleUpload(String[] parts) throws IOException {
      if (!loggedIn) {
         myStreamSocket.sendMessage("202 Upload failed, please log in");
         System.out.println("Upload failed: client not logged in");
         return;
      }

      if (parts.length < 2 || parts[1].trim().length() == 0) {
         myStreamSocket.sendMessage("202 Upload failed, empty message");
         System.out.println("Upload failed: empty message");
         return;
      }

      String messageText = parts[1].trim();

      String storedMessage = clientUsername + ": " + messageText;
      Server.addMessage(storedMessage);

      myStreamSocket.sendMessage("201 Message uploaded successfully");
      System.out.println("Message uploaded by " + clientUsername);
   }

   private void handleDownloadAll() throws IOException {
      if (!loggedIn) {
         myStreamSocket.sendMessage("302 Download failed. Please log in");
         System.out.println("Download all failed: client not logged in");
         return;
      }
      ArrayList<String> messages = Server.getAllMessages();

      if (messages.size() == 0) {
         myStreamSocket.sendMessage("302 No messages available");
         System.out.println("Download all: no messages available");
         return;
      }

      myStreamSocket.sendMessage("301 " + messages.size());

      for (int i = 0; i < messages.size(); i++) {
         myStreamSocket.sendMessage("[" + i + "] " + messages.get(i));
      }

      myStreamSocket.sendMessage("END");
      System.out.println("Sent " + messages.size() + " messages to " + clientUsername);
   }

   private void handleDownloadOne(String[] parts) throws IOException {
      if (!loggedIn) {
         myStreamSocket.sendMessage("352 Download failed. Please log in");
         System.out.println("Download one failed: client not logged in");
         return;
      }

      if (parts.length < 2) {
         myStreamSocket.sendMessage("352 Invalid message, no index provided");
         System.out.println("Download one failed: no index");
         return;
      }

      int index;
      try {
         index = Integer.parseInt(parts[1].trim());
      }
      catch (NumberFormatException ex) {
         myStreamSocket.sendMessage("352 Invalid message, index not a number");
         System.out.println("Download one failed: bad index format");
         return;
      }

      String message = Server.getMessage(index);

      if (message == null) {
         myStreamSocket.sendMessage("352 Invalid message, index out of range");
         System.out.println("Download one failed: index " + index + " out of range");
         return;
      }

      myStreamSocket.sendMessage("351 " + message);
      System.out.println("Sent message " + index + " to " + clientUsername);
   }

   private void handleLogoff() throws IOException {
      myStreamSocket.sendMessage("401 Logoff successful");
      System.out.println("User logged off: " + clientUsername);
      loggedIn = false;
      clientUsername = "";
   } 
}