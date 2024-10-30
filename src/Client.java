import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This is a simple client class for exchanging character (text)
 * information with a server. The exchanges are line-based: user
 * input is sent to the server when the user hits ENTER at the
 * end of a line of input. The server's response is assumed to be
 * a single line of text, which is printed when received.
 */
public class Client
{
    private final String hostname;
    private final int port;
    private final String prompt;

    /**
     * Creates a client for character-based exchanges with a server.
     *
     * @param hostname the hostname of the server.
     * @param port     the service's port on the server.
     * @throws IllegalArgumentException if port not in range [1-49151]
     */
    public Client(String hostname, int port)
    {
        if (port < 1 || port > 49151) {
            throw new IllegalArgumentException(
                    "Port " + port + " not in range 1 - 49151.");
        }
        this.hostname = hostname;
        this.port = port;
        this.prompt = "hostname:" + port + "> ";
    }

    /**
     * Starts this client, connecting to the server and port that
     * it was given when constructed.
     *
     * @throws UnknownHostException if hostname is not resolvable.
     * @throws IOException          if socket creation, wrapping, or IO fails.
     */
    public void start() throws UnknownHostException, IOException, ClassNotFoundException
    {
        System.out.println("Attempting connection to " + hostname + ":" + port);
        Scanner keyboard = new Scanner(System.in);

        try (
                // Create client socket on local port.
                Socket socket = new Socket(hostname, port);

                // Build streams on top of socket.
                OutputStream outStream = socket.getOutputStream();
                //BufferedOutputStream outBuf = new BufferedOutputStream(outStream);
                ObjectOutputStream outObj = new ObjectOutputStream(outStream);

                InputStream inStream = socket.getInputStream();
                //BufferedInputStream inBuf = new BufferedInputStream(inStream);
                ObjectInputStream inObj = new ObjectInputStream(inStream)
        )
        {
            String userInput;
            Message inMsg;
            Message outMsg;

            // Take turns talking. Server goes first.
            do {
                // Get server message and show it to user.
                inMsg = (Message) inObj.readObject();
                if (inMsg.getMsgType() == MsgType.TEXT) {
                    inMsg = (TextMessage) inMsg;
                    System.out.println(((TextMessage) inMsg).getText());
                } else {
                    System.out.println("UNRECOGNIZED RESPONSE: " + inMsg.toString());
                }

                // Get user input
                System.out.print(prompt);
                userInput = keyboard.nextLine();
                String[] inTokens = userInput.split("\\s");

                // Construct Message based on user input.
                if (inTokens[0].equalsIgnoreCase("LOGOUT")) {
                    outMsg = new LogoutMessage("client");
                } else if (inTokens[0].equalsIgnoreCase("LISTUSERS")) {
                    outMsg = new ListUsersMessage("client");
                } else {
                    outMsg = new TextMessage("client", userInput);
                }

                // Send it to server.
                outObj.writeObject(outMsg);
                outObj.flush();
            } while (outMsg.getMsgType() != MsgType.LOGOUT);

            // Get server's closing reply and show it to user.
            inMsg = (Message) inObj.readObject();
            if (inMsg.getMsgType() == MsgType.TEXT) {
                inMsg = (TextMessage) inMsg;
                System.out.println(((TextMessage) inMsg).getText());
            } else {
                System.out.println("UNRECOGNIZED RESPONSE: " + inMsg.toString());
            }

        }   // Streams and sockets closed by try-with-resources

        System.out.println("Connection to " + hostname + ":" + port
                + " closed, exiting.");
    }
}

