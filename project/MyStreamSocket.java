import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
* A class that uses a Socket object to send and receive messages. 
* The Wrapper Class is built off the MyStreamSocket used in the class material. 
* It creates a simple method for calling other methods for sending and receiving messages with text using TCP. 
*
* I have kept the Wrapper Class because it allows you to hide all of the detail of setting up the input/output streams from the rest of your code. 
* Therefore, Server and Client Classes can simply call sendMessage() and receiveMessage(), instead of having to deal with creating BufferedReader and/or PrintWriter objects.
*
* Reference:
* M. L. Liu (original) MyStreamSocket.java, provided in class materials.
*/

public class MyStreamSocket {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    // Constructor for client side: creates a socket and connects to the server
    MyStreamSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);
        setStreams();
    }

    // Constructor for server side: accepts a socket from the server socket and sets up streams
    MyStreamSocket(Socket socket) throws IOException {
        this.socket = socket;
        setStreams();
    }

    // Sets up the input and output streams for the socket
    private void setStreams() throws IOException {
        InputStream inStream = socket.getInputStream();
        input = new BufferedReader(new InputStreamReader(inStream));
        OutputStream outStream = socket.getOutputStream();
        output = new PrintWriter(new OutputStreamWriter(outStream));
    }

    public void sendMessage(String message) throws IOException {
        output.print(message + "\n");
        output.flush();
    }

    public String receiveMessage() throws IOException {
        String message = input.readLine();
        return message;
    }
    
    public java.net.InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public void close() throws IOException {
        socket.close();
    }
}
