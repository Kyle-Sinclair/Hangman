package client.controller;


import java.io.IOException;
import java.io.UncheckedIOException;

import java.util.concurrent.CompletableFuture;
import client.network.ServerConnection;
import client.network.OutputHandler;


public class ClientController {
    private final ServerConnection serverConnection = new ServerConnection();


    public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Connect method called with host string " + host +
                                    " Port Number: " + port);
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {

                throw new UncheckedIOException((IOException) ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }

    public void guess(String guess) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendGuess(guess);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
    public void startNewGame() {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.startNewGame();
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    public void disconnect() {
        try {
            serverConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
