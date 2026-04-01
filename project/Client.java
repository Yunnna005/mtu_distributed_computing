import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        InputStreamReader inputReader = new InputStreamReader(System.in);
        BufferedReader bufferReader = new BufferedReader(inputReader);

        try {

            System.out.println("Welcome to Client.\n \"What is the name of the server host?");
            String hostName = bufferReader.readLine();

            if (hostName.length() == 0){
                hostName = "localhost";
            } 

            System.out.println("What is the port number of the server host?");
            String portNum = bufferReader.readLine();
            if (portNum.length() == 0){
                portNum = "7";
            }

            ClientHelper helper = new ClientHelper(hostName, portNum);
            System.out.println("Connected to server at " + hostName + ":" + portNum);
            
            boolean loggedIn = false;
            while(!loggedIn){
                System.out.println("LOGIN\nPlease enter your username:");
                String username = bufferReader.readLine();

                if(username.equalsIgnoreCase("exit")){
                    System.out.println("Exiting client.");
                    return;
                }

                System.out.println("Please enter your password:");
                String password = bufferReader.readLine();

                String response = helper.login(username, password);
                System.out.println("Server response: " + response);

                if (response.startsWith("101")) {
                    loggedIn = true;
                    System.out.println("Welcome, " + username + "!");
                }
                else {
                    System.out.println("Login failed. Please try again.");
                }
                System.out.println("");
                    
                boolean done = false;
                while (!done) {
                    System.out.println("MENU");
                    System.out.println("1. Upload a message");
                    System.out.println("2. Download all messages");
                    System.out.println("3. Download a specific message");
                    System.out.println("4. Log off");
                    System.out.println("Enter your choice (1-4):");
        
                    String choice = bufferReader.readLine();

                    switch (choice) {
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
                            
                        case "2":
                            String response2 = helper.downloadAll();
                            System.out.println("Server response:");
                            System.out.println(response2);
                            break;

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
                        case "4":
                            String response4 = helper.logoff();
                            System.out.println("Server response: " + response4);
                            System.out.println("Goodbye.");
                            done = true;
                            break;
                    
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