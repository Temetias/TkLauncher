package main;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class IPCHandler implements Runnable {

    Stage stage;

    public IPCHandler(Stage mainStage) {
        this.stage = mainStage;
    }

    public void run() {

        try {
            // Start socket.
            System.out.println("Starting IPC connection...");
            ServerSocket welcomeSocket = new ServerSocket(6969);

            // Run python script.
            System.out.println("Running python script...");
            Runtime.getRuntime().exec("pythonw src/hotkeyAssigner.pyw");
            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(
                                connectionSocket.getInputStream()
                        )
                );
                // Wait for the data. NOTE: If we want to expand the python script functionality, store this to a variable.
                inFromClient.readLine();

                // Call main thread.
                Platform.runLater(() -> stage.setIconified(false));

                // TODO: Only restart this script when the window minimizes.
                Runtime.getRuntime().exec("pythonw src/hotkeyAssigner.pyw");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
