package Controllers;

import Interfaces.IClientHandler;
import Models.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public abstract class AbstractClient implements Runnable, IClientHandler {
    protected final Socket clientSocket;
    protected final OutputStream outputStream;
    protected final BufferedReader reader;
    protected String username;
    protected AbstractServer server;

    public AbstractClient(Socket socket, AbstractServer server) throws IOException {
        this.server = server;
        this.clientSocket = socket;
        this.outputStream = clientSocket.getOutputStream();
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            this.username = reader.readLine();
            broadcastMessage("[" + username + "] has joined the chat.");

            String message;
            while ((message = reader.readLine()) != null) {
                handleCommand(message);

                message = server.getFileHandler().censorMessage(message);
                System.out.println("[" + username + "] : " + message);

                Message clientMessage = new Message(username, message, new Date());
                server.getFileHandler().saveMessageToFile(clientMessage);

                if (!message.trim().equalsIgnoreCase("#users") && !message.trim().equalsIgnoreCase("#exit")){
                    broadcastMessage("[" + username + "] : " + message);
                }

            }
        } catch (SocketException e) {
            handleSocketException(e);
        } catch (IOException e) {
            handleIOException(e);
        } finally {
            closeClientSocket();
        }
    }

    protected abstract boolean handleCommand(String message);

    protected void handleSocketException(SocketException e) {
        System.out.println("[" + username + "] : " + "disconnected unexpectedly.");
        server.removeClient(this);
    }

    protected void handleIOException(IOException e) {
        System.out.println("[" + username + "] : " + "disconnected unexpectedly.");
        server.removeClient(this);
    }

    protected void closeClientSocket() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void broadcastMessage(String message) {
        for (AbstractClient client : server.getClients()) {
            if (client != this) {
                PrintWriter writer = new PrintWriter(client.outputStream, true);
                writer.println(message);
            }
        }
    }
}