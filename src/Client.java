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
                ObjectOutputStream outObj = new ObjectOutputStream(outStream);
//                BufferedReader inReader =
//                        new BufferedReader(
//                                new InputStreamReader(socket.getInputStream()));
                InputStream inStream = socket.getInputStream();
                ObjectInputStream inObj = new ObjectInputStream(inStream)
//                PrintWriter outWriter =
//                        new PrintWriter(socket.getOutputStream(), true)
        )
        {
            String userStr;
            String serverStr;

            // Wait for server to speak first.
            serverStr = (String) inObj.readObject();
            System.out.println(serverStr);

            // Our turn
            do {
                System.out.print(prompt);
                userStr = keyboard.nextLine();

                outObj.writeObject(userStr);
                outObj.flush();

                serverStr = (String) inObj.readObject();
                System.out.println(serverStr);
            } while (!userStr.isEmpty());
        }   // Streams and sockets closed by try-with-resources

        System.out.println("Connection to " + hostname + ":" + port
                + " closed, exiting.");
    }
}

