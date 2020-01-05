package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.controller.ServerController;
import common.Definitions;
import server.model.Game;


public class GameServer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final ServerController contr = new ServerController();
    private final List<ClientHandler> clients = new ArrayList<>();
    private int portNo = 8080;
    static ServerSocket socket;




    public static void main(String[] args) {
        try {
            GameServer.inititalize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GameServer server = new GameServer();

        System.out.println("Server beginning service");
        server.serve();
    }

    private void serve() {
        while (true){
            try {
                Socket clientSock = socket.accept();
                System.out.println("New connection");
                Thread t = new Thread(new ClientHandler(clientSock));
                t.start();
            } catch (IOException ex) {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }



        public static void inititalize() throws IOException {
            socket = new ServerSocket(Definitions.PORT);
            Game.initializeDictionary("C:\\Users\\Joint Account\\Documents\\Hangman\\src\\main\\java\\server\\model\\words.txt");
        }


}
