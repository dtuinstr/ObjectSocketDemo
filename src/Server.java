import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is a simple server class for exchanging character (text)
 * information with a client. The exchanges are line-based: client
 * input is expected to be a newline-terminated string of text.
 * This server processes that line and sends back a single-line
 * response. The connection terminates when the client sends an
 * empty string.
 */
public class Server
{
    // For strings sent to client.
    private static final String GREETING =
            "[Server listening. 'Logout' (case insensitive) closes connection.]";
    private static final String GOOD_BYE =
            "[Closing connection, good-bye.]";

    // Object variables.
    private final int port;

    /**
     * Creates a server for character-based exchanges.
     *
     * @param port the port to listen on.
     * @throws IllegalArgumentException if port not in range [1024, 49151].
     */
    public Server(int port)
            throws IllegalArgumentException
    {
        if (port < 1024 || port > 49151) {
            throw new IllegalArgumentException(
                    "Port " + port + " not in range 1024-49151.");
        }
        this.port = port;
    }

    /**
     * Starts this server, listening on the port it was
     * constructed with.
     *
     * @throws IOException if ServerSocket creation, connection
     *                     acceptance, wrapping, or IO fails.
     */
    public void start() throws IOException, ClassNotFoundException
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server starting on port " + port + ".");
            System.out.println("Ctrl + C to exit.");
            try (
                    // Wait for connection.
                    Socket clientSocket = serverSocket.accept();

                    // Build streams on the socket.
                    InputStream inStream = clientSocket.getInputStream();
                    //BufferedInputStream inBuf = new BufferedInputStream(inStream);
                    ObjectInputStream inObj = new ObjectInputStream(inStream);

                    OutputStream outStream = clientSocket.getOutputStream();
                    //BufferedOutputStream outBuf = new BufferedOutputStream(outStream);
                    ObjectOutputStream outObj = new ObjectOutputStream(outStream)
            )
            {
                // Connection made. Greet client.
                outObj.writeObject(new TextMessage("server", GREETING));
                outObj.flush();

                // Converse with client.
                Message inMsg;
                Message outMsg;
                do {
                    inMsg = (Message) inObj.readObject();
                    System.out.println("inMsg.getClass() = " + inMsg.getClass());

                    // Process received message
                    outMsg = switch (inMsg.getMsgType()) {
                        case LISTUSERS -> new TextMessage("server", "LISTUSERS requested");
                        case LOGOUT -> new TextMessage("server", "LOGOUT requested");
                        case TEXT -> new TextMessage("server",
                                "TEXT: " + ((TextMessage) inMsg).getText());
                    };

                    System.out.println(outMsg);  // server console, for DEBUG.
                    outObj.writeObject(outMsg);
                    outObj.flush();
                } while (outMsg.getMsgType() != MsgType.LOGOUT);

                outObj.writeObject(new TextMessage("server", GOOD_BYE));
                outObj.flush();
                System.out.println("Client terminated connection.");
            }   // Streams and socket closed by try-with-resources.
        } // Server socket closed by try-with-resources.
    }
}
