package Interfaces;

import Controllers.AbstractClient;
import Controllers.ClientHandler;

import java.util.List;

public interface IServer {
    void start(int port);

    void removeClient(AbstractClient client);

    List<String> getConnectedUsers();
}