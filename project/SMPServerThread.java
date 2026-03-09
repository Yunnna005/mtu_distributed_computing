import java.io.*;
import java.net.*;
import java.util.List;

public class SMPServerThread extends Thread {
    private Socket socket;
    private boolean loggedIn = false;

    public SMPServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            String line;
            while ((line = input.readLine()) != null) {
                String[] parts = line.split("\\|");
                String command = parts[0];

                if (command.equals("LOGIN")) {
                    loggedIn = true;
                    output.println("OK|Logged in");
                    System.out.println("User logged in"); 

                } else if (!loggedIn) {
                    output.println("ERROR|Not logged in");

                } else if (command.equals("UPLOAD")) {
                    if (parts.length < 2 || parts[1].length() > 140) {
                        output.println("ERROR|Invalid message");
                    } else {
                        SMPServer.addMessage(parts[1]);
                        output.println("OK|Message ID: " + SMPServer.getAllMessages().size());
                        System.out.println("Message uploaded"); 
                    }

                } else if (command.equals("DOWNLOAD")) {
                    if (parts.length < 2) {
                        output.println("ERROR|Invalid ID");
                    } else {
                        try {
                            int id = Integer.parseInt(parts[1]);
                            String msg = SMPServer.getMessage(id);
                            if (msg == null) {
                                output.println("ERROR|Message not found");
                            } else {
                                output.println("OK|ID:" + id + "|Text:" + msg);
                            }
                        } catch (NumberFormatException e) {
                            output.println("ERROR|Invalid ID");
                        }
                    }

                } else if (command.equals("DOWNLOAD_ALL")) {
                    List<String> all = SMPServer.getAllMessages();
                    if (all.isEmpty()) {
                        output.println("ERROR|No messages");
                    } else {
                        StringBuilder sb = new StringBuilder("OK|NUM_MESSAGES:" + all.size());
                        for (int i = 0; i < all.size(); i++) {
                            sb.append("|ID:").append(i + 1).append("|Text:").append(all.get(i));
                        }
                        output.println(sb.toString());
                    }

                } else if (command.equals("LOGOFF")) {
                    output.println("OK|Logged off");
                    break;

                } else {
                    output.println("ERROR|Invalid command");
                }
            }
        } catch (IOException ex) {
            System.out.println("Error in session: " + ex); 
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }
}