import Interfaces.IServer;
import Models.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Server implements IServer {
    private ServerSocket serverSocket;
    final private ArrayList<Client> clients = new ArrayList<>();

    public static void main(String[] args) {
        new Server().run();
    }

    public Server() {
        try {
            this.serverSocket = new ServerSocket(8080);
            System.out.print("Server config succesfull, \n\t Awaiting clients...\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        connectClients();
        readMessages();
    }

    @Override
    public void connectClients() {
        Thread acceptThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                        Client client = new Client(clientSocket, writer);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
                        client.setUsername(reader.readLine());

                        synchronized (clients) {
                            clients.add(client);
                        }
                        System.out.println("[" + client.getUsername() + "]" + " has connected.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        acceptThread.start();
    }


    @Override
    public void readMessages() {
        while (true) {
            ArrayList<Client> clientsToRemove = new ArrayList<>();
            synchronized (clients) {
                Iterator<Client> iterator = clients.iterator();
                while (iterator.hasNext()) {
                    Client MClientHandler = iterator.next();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(MClientHandler.getSocket().getInputStream()));

                        if (reader.ready()) {
                            String message = reader.readLine();
                            String clientUsername = MClientHandler.getUsername();
                            System.out.println("Message from [" + clientUsername + "] : " + message);

                            if (message.equals("#exit")) {
                                System.out.println("[" + clientUsername + "]" + " disconnected");
                                clientsToRemove.add(MClientHandler);
                            } else {
                                broadcastMessage(message, clientUsername);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // Remove disconnected clients outside the synchronized block
                clients.removeAll(clientsToRemove);
            }
        }
    }

    @Override
    public void broadcastMessage(String message, String username) {
        synchronized (clients) {
            for (Client MClientHandler : clients) {
                String censoredMessage = censorMessage(message); // Replace banned words with asterisks
                MClientHandler.getWriter().println("[" + username + "] : " + censoredMessage);
                MClientHandler.getWriter().flush();
            }
        }
    }


    @Override
    public Set<String> loadBannedWords() {
        Set<String> bannedWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("./ban_list.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) {
                bannedWords.add(word.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bannedWords;
    }

    @Override
    public String censorMessage(String message) {
        Set<String> bannedWords = loadBannedWords();
        String[] words = message.split("\\s+|(?=[,.!?;])|(?<=[,.!?;])");
        StringBuilder censoredMessage = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String censoredWord;
            if (bannedWords.contains(word.toLowerCase())) {
                censoredWord = "*".repeat(word.length());
            } else {
                censoredWord = word;
            }
            censoredMessage.append(censoredWord).append(" ");
        }
        return censoredMessage.toString().trim();
    }
}
