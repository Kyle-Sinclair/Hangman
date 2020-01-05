package server.net;


import common.Definitions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import server.controller.ServerController;
import java.util.logging.Level;
import java.util.logging.Logger;
import common.*;

import static java.lang.Thread.sleep;


public class ClientHandler implements Runnable {

    private  Socket clientSocket;
    private ServerController controller;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private boolean connected;

    public static final int MS_LINGER = 18000000;

    public ClientHandler(Socket clientSocket) throws SocketException, IOException {
        this.clientSocket = clientSocket;
        clientSocket.setSoLinger(true, MS_LINGER);
        clientSocket.setSoTimeout(Definitions.MS_TIMEOUT);
        controller = new ServerController();

        toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        fromClient = new ObjectInputStream(clientSocket.getInputStream());

        connected = true;
        System.out.println("A client handler thread has been started");


    }

    @Override
    public void run() {


        while (connected) {
            try {
                Message msg = (Message) fromClient.readObject();
                System.out.println("client handler has recieved a message: " + msg.toString());

                switch (msg.type) {

                    case GUESS:

                        String guess = (String) msg.payload;
                        System.out.println("Player has attempted a guess of " + guess);
                        sendAnswer(controller.guess(guess));
                        break;

                    case DISCONNECT:
                        disconnect();

                        break;
                    default:
                        throw new MessageException("Received corrupt message: " + msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnect();
                throw new MessageException(e);
            }
        }
    }

    void sendAnswer(Message answer) throws IOException {
        toClient.writeObject(answer);
        toClient.flush();
        toClient.reset();
    }

    void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        connected = false;
    }
}
