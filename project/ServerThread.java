import java.io.*;
import java.util.ArrayList;

/**
* The ServerThread Class - Manages one client interaction for the SMP server.
*
* Since this class implements Runnable, we can use this class to create threads of execution.
* Everytime a client logs into the SMP server, the server will start a new ServerThread to manage the communication with that client. 
* This is the exact same approach taken in the EchoServerThread.java class material example.
*
* The run() method has the main processing loop of the application. It is where each protocol message received from the client is parsed and processed based upon the SMP protocol. 
* Then, an SMP protocol Response code is returned to the client as part of the response.
*
* SMP Protocol Messages (server side) :
* 100 username password : Client wants to login.
* 200 message_text: Client wants to upload a message. 
* 300: Client wants all messages.
* 350 index: Client wants to download a message. 
* 400: Client wants to logout. 
* SMP Protocol Response Codes:
* 101 Login was Successful.
* 102 Login Failed. 
* 201 Message Upload was Successful. 
* 202 Message Upload Failed. 
* 301 message_count (followed by messages(one per line, followed by END)) 
* 302 No messages are available. 
* 351 message_text: Single message requested by client. 
* 352 Invalid message Index. 
* 401 Logout was Successful. 
* 500 Unrecognized Command. 
*
* I have chosen Numeric Code responses because it is the way most Real Protocols communicate. 
* For instance, HTTP has 200 for Success, 404 for Not Found. In addition, FTP communicates using Three Digit Codes, because it is easy to send the response back to the client.
* I classified the codes by groups: 1xx = Login, 2xx = Uploads, 3xx = Downloads, 4xx = Logout and 5xx = Errors.
*
* References:
* M. L. Liu, EchoServerThread.java, class materials
*/

public class ServerThread implements Runnable {
    MyStreamSocket myStreamSocket;
    String clientUsername;
    boolean loggedIn;

    // Constructor takes the socket connected to the client
    public ServerThread(MyStreamSocket socket) {
        this.myStreamSocket = socket;
        this.clientUsername = "";
        this.loggedIn = false;
    }

    // Main loop for handling client messages
    public void run() {
        boolean done = false;
        String message;

        try{
            while(!done){
                message = myStreamSocket.receiveMessage();

                //If the client disconnects without sending ".", the original would throw an exception.
                if(message == null) {
                    System.out.println("Client disconnected: " + myStreamSocket.getInetAddress().getHostAddress());
                    done = true;
                    break;
                }

                System.out.println("Received from client: " + message);

                //The code splits the message to extract the command code and uses a switch statement to route to handler methods.
                String[] parts = message.split(" ", 2);
                String code = parts[0];

                switch (code) {
                    case "100":
                        handleLogin(parts);
                        break;
                    case "200":
                        handleUpload(parts);
                        break;
                    case "300":
                        handleDownloadAll();
                        break;
                    case "350":
                        handleDownloadOne(parts);
                        break;
                    //The code ends session when SMP code 400 is received.
                    case "400":
                        handleLogoff();
                        done = true;
                        break;
                    default:
                        myStreamSocket.sendMessage("500 Unknown command");
                        System.out.println("Unknown command received: " + code);
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Error in communication with client: " + ex.getMessage());
            ex.printStackTrace();

        //The code uses a finally block to ensure the socket is always closed, even if an exception occurs.
        } finally {
            try {
                myStreamSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing connection: " + ex.getMessage());
            }
        }
    }
    //All five handler methods below are SMP protocol logic. 
    //Each method checks for valid input, checks if the client is logged in (if required), and sends appropriate response codes back to the client.
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

        // Trim whitespace from username and password
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

    //The handleUpload method checks if the client is logged in and if the message text is valid. If so, it adds the message to the shared message list with the username as a prefix.
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

      // Trim whitespace from the message text
      String messageText = parts[1].trim();

      String storedMessage = clientUsername + ": " + messageText;
      Server.addMessage(storedMessage);

      myStreamSocket.sendMessage("201 Message uploaded successfully");
      System.out.println("Message uploaded by " + clientUsername);
   }

   //The handleDownloadAll method checks if the client is logged in and if there are messages available. If so, it sends the message count followed by each message on a new line, ending with "END".
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
//The handleDownloadOne method checks if the client is logged in, if an index was provided, and if the index is valid. If so, it sends the requested message back to the client.
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
//The handleLogoff method sends a logoff success message to the client, updates the loggedIn status, and clears the username.
   private void handleLogoff() throws IOException {
      myStreamSocket.sendMessage("401 Logoff successful");
      System.out.println("User logged off: " + clientUsername);
      loggedIn = false;
      clientUsername = "";
   } 
}