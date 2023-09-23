package Controllers;

import Interfaces.IServer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractServer implements IServer {
    protected List<AbstractClient> clients;
    protected ExecutorService executorService;
    protected FileHandler fileHandler;

    public AbstractServer() {
        this.clients = new CopyOnWriteArrayList<>();
        this.executorService = Executors.newFixedThreadPool(10);
        this.fileHandler = new FileHandler("messages.txt");
    }

    public List<AbstractClient> getClients() {
        return this.clients;
    }

    @Override
    public void removeClient(AbstractClient client) {
        clients.remove(client);
        System.out.println("[" + client.getUsername() + "] has left the chat");
    }

    @Override
    public List<String> getConnectedUsers() {
        List<String> connectedUsers = new CopyOnWriteArrayList<>();
        for (AbstractClient client : clients) {
            connectedUsers.add(client.getUsername());
        }
        return connectedUsers;
    }

    public FileHandler getFileHandler(){
        return this.fileHandler;
    }
}
