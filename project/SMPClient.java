import java.io.*;
import java.net.*;

public class SMPClient {
    public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        try {
            System.out.println("Welcome to Client.\n \"What is the name of the server host?");

            String hostName = br.readLine();
            if (hostName.length() == 0) 
            hostName = "localhost";

            System.out.println("What is the port number of the server host?");
            String portNum = br.readLine();
            if (portNum.length() == 0)
                portNum = "7";

            Socket socket = new Socket(hostName, Integer.parseInt(portNum));

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Login
            System.out.println("Username?");
            String user = br.readLine();
            System.out.println("Password?");
            String pass = br.readLine();
            output.println("LOGIN|" + user + "|" + pass);
            System.out.println(input.readLine());

            boolean done = false;
            while (!done) {
                System.out.println("Command: UPLOAD <msg>, DOWNLOAD <id>, DOWNLOAD_ALL, LOGOFF");
                String cmd = br.readLine();
                output.println(cmd);
                String resp = input.readLine();
                System.out.println(resp);

                if (cmd.startsWith("LOGOFF")) done = true;
            }

            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace(); // Error handling
        }
    }
}