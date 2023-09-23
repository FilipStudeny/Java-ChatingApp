package Controllers;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends AbstractClient {

    public ClientHandler(Socket socket, ServerHandler server) throws IOException {
        super(socket, server);
    }

    @Override
    protected boolean handleCommand(String message) {
        if (message.trim().equalsIgnoreCase("#exit")) {
            broadcastMessage("[" + username + "] has left the chat");
            server.removeClient(this);
            return true;
        } else if (message.trim().equalsIgnoreCase("#users")) {
            handleUsersRequest();
            return true; // Return true to indicate that the command was handled
        }
        return false;
    }

    public void handleUsersRequest() {
        String userMessage = "Connected Users: " + String.join(", ", server.getConnectedUsers());
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println(userMessage);
    }
}