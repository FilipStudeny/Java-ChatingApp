package Controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandler extends AbstractServer {
    @Override
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public FileHandler getFileHandler() {
        return fileHandler;
    }
}