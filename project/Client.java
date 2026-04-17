import java.io.*;

/**
* This class holds the presentation for the SMP client. 
* It shows a menu to the user and receives the user's input via the command line. 
* When the user selects an option, it will call the corresponding method of ClientHelper 
* to perform the communication through the network. 
* 
* The client can be started with the specified location of the truststore on the command line so that 
* the SSL-connection could check the server's certificate. The command is:
*   java -Djavax.net.ssl.trustStore=client_truststore.jks -Djavax.net.ssl.trustStorePassword=password Client
* 
* References:
* M. L. Liu, EchoClient2.java, class materials
* Lab 2 SSL 2023, class materials
* 
**/

public class Client {
    public static void main(String[] args) {
        InputStreamReader inputReader = new InputStreamReader(System.in);
        BufferedReader bufferReader = new BufferedReader(inputReader);

        try {

            System.out.println("Welcome to Client.\nWhat is the name of the server host?");
            String hostName = bufferReader.readLine();

            if (hostName.length() == 0){
                hostName = "localhost";
            } 

            System.out.println("What is the port number of the server host?");
            String portNum = bufferReader.readLine();
            if (portNum.length() == 0){
                portNum = "7";
            }

            // Create the client helper which sets the SSL connection
            ClientHelper helper = new ClientHelper(hostName, portNum);
            System.out.println("Connected to server at " + hostName + ":" + portNum);
            
            //My code adds a login loop that keeps asking for credentials until the server accepts them (code 101).
            //The user can type "exit" instead of a username to quit.
            boolean loggedIn = false;
            boolean showMenu = false;
            while(!loggedIn){
                System.out.println("LOGIN\nPlease enter your username:");
                String username = bufferReader.readLine();

                if(username.equalsIgnoreCase("exit")){
                    System.out.println("Exiting client.");
                    return;
                }

                System.out.println("Please enter your password:");
                String password = bufferReader.readLine();

                // Send the login request to the server and get the response
                String response = helper.login(username, password);
                System.out.println("Server response: " + response);

                if (response.startsWith("101")) {
                    loggedIn = true;
                    showMenu = true;
                    System.out.println("Welcome, " + username + "!");
                }
                else {
                    System.out.println("Login failed. Please try again.");
                }
                System.out.println("");

                //My code implements a menu-driven loop that gives the user four choices: upload, download all, download one, and logoff.
                //Each choice calls a different method in ClientHelper. The loop ends when the user chooses option 4 (logoff),
                //which sets showMenu to false.  
                while (showMenu) {
                    System.out.println("MENU");
                    System.out.println("1. Upload a message");
                    System.out.println("2. Download all messages");
                    System.out.println("3. Download a specific message");
                    System.out.println("4. Log off");
                    System.out.println("Enter your choice (1-4):");
        
                    String choice = bufferReader.readLine();

                    switch (choice) {
                        //Reads a message from the user and sends it to the server using the SMP protocol code 200. 
                        //The server will respond with a message that starts with 201 if the upload is successful, or an error code if it fails.
                        case "1":
                            System.out.println("Enter the message to upload:");
                            String messageText = bufferReader.readLine();

                            if (messageText.length() == 0) {
                                System.out.println("Error: message cannot be empty.");
                            }
                            else {
                                String response1 = helper.uploadMessage(messageText);
                                System.out.println("Server response: " + response1);
                            }
                            break;
                            
                        //Requests all stored messages from the server using the SMP protocol code 300. 
                        case "2":
                            String response2 = helper.downloadAll();
                            System.out.println("Server response:");
                            System.out.println(response2);
                            break;

                        //Asks for an index and requests that specific message using the SMP protocol code 350.
                        case "3":
                            System.out.println("Enter the message index number:");
                            String indexStr = bufferReader.readLine();
                            try {
                                int index = Integer.parseInt(indexStr.trim());
                                String response3 = helper.downloadOne(index);
                                System.out.println("Server response: " + response3);
                            }
                            catch (NumberFormatException ex) {
                                System.out.println("Error: please enter a valid number.");
                            }
                            
                            break;
                        //The code sends the SMP logoff command (code 400) through helper.logoff() and sets showMenu to false to exit the loop.
                        case "4":
                            String response4 = helper.logoff();
                            System.out.println("Server response: " + response4);
                            System.out.println("Goodbye.");
                            showMenu = false;
                            break;
                    
                        //The code checks for invalid menu choices. If the user enters something other than 1, 2, 3, or 4, it will print an error message and show the menu again.
                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                            break;
                    }
                }
            }
        } catch (Exception ex) {
         System.out.println("Error: " + ex.getMessage());
         ex.printStackTrace();
        }
    }
}