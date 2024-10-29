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
//                        BufferedReader inReader =
//                                new BufferedReader(
//                                        new InputStreamReader(
//                                                clientSocket.getInputStream()));
                    InputStream inStream = clientSocket.getInputStream();
                    ObjectInputStream inObj = new ObjectInputStream(inStream);

//                        PrintWriter outWriter =
//                                new PrintWriter(clientSocket.getOutputStream(),
//                                        true)
                    OutputStream outStream = clientSocket.getOutputStream();
                    ObjectOutputStream outObj = new ObjectOutputStream(outStream)
            )
            {
                // Connection made. Greet client.
//                    outWriter.println(GREETING);
                outObj.writeObject(GREETING);

                // Converse with client.
//                    String inString = inReader.readLine();
                String inString = (String) inObj.readObject();
                String reply = "Received: '" + inString + "'";

                System.out.println(reply);  // server console, for DEBUG.
                outObj.writeObject(reply);
                outObj.flush();

                while (!inString.isEmpty()) {
                    inString = (String) inObj.readObject();
                    reply = "Received: '" + inString + "'";

                    System.out.println(reply);  // server console, for DEBUG.
                    outObj.writeObject(reply);
                    outObj.flush();
                }
                outObj.writeObject(GOOD_BYE);
                outObj.flush();
                System.out.println("Client terminated connection.");
            }   // Streams and socket closed by try-with-resources.
        } // Server socket closed by try-with-resources.
    }
}
