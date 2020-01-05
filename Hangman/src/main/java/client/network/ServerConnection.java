package client.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;


import common.Message;
import common.MessageType;

/**
 * Manages all communication with the server.
 */
public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;

    /**
     * Creates a new instance and connects to the specified server. Also starts a listener thread
     * receiving broadcast messages from server.
     *
     * @param host             Host name or IP address of server.
     * @param port             Server's port number.
     * @throws IOException If failed to connect.
     */
    public void connect(String host, int port, OutputHandler broadcastHandler) throws
            IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_MINUTE);
        connected = true;
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        new Thread(new Listener(broadcastHandler)).start();
    }

    /**
     * Closes the connection with the server and stops the broadcast listener thread.
     *
     * @throws IOException If failed to close socket.
     */
    public void disconnect() throws IOException {
        sendMsg(MessageType.DISCONNECT, null);
        socket.close();
        socket = null;
        connected = false;
    }

    public void startNewGame() throws IOException {
        sendMsg(MessageType.START,"");
    }

    public void sendGuess(String guess) throws IOException {
        sendMsg(MessageType.GUESS, guess);
    }


    private void sendMsg(MessageType type, String body) throws IOException {
        body = body.toUpperCase();
        Message msg = new Message(type, body);
        System.out.println("Sending a message: " + msg.toString());
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
    }

    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            while (connected) {
                try {
                    Message msg = (Message) fromServer.readObject();
                    if (msg.type != MessageType.ILLEGAL_RESPONSE) {
                        System.out.println("Message recieved from server " + msg.toString());                    }
                } catch (SocketException ex) {
                    connected = false;
                } catch (IOException | ClassNotFoundException ex) {
                }
            }
        }
    }
}
