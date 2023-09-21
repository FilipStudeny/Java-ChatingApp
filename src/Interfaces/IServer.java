package Interfaces;

import java.util.Set;

public interface IServer {

    void broadcastMessage(String message, String username);
    void connectClients();
    void run();
    void readMessages();
    Set<String> loadBannedWords();
    String censorMessage(String message);
}

