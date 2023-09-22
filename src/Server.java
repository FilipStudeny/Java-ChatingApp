import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Set<String> bannedWords = loadBannedWords();

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

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
        System.out.println("[" + client.getUsername() + "] has left the chat");
    }

    public void broadcastMessage(String message, ClientHandler sender) throws IOException {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender) {
                    client.sendMessage("[" + sender.getUsername() + "] : " + message);
                }
            }
        }
    }

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

    public Set<String> getBannedWords() {
        return bannedWords;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(1234);
    }

    public class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private OutputStream outputStream;
        private String username;
        private final Server server;

        public ClientHandler(Socket socket, Server server) {
            this.clientSocket = socket;
            this.server = server;
        }

        public void run() {
            try (InputStream inputStream = clientSocket.getInputStream();
                 OutputStream outputStream = clientSocket.getOutputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                this.outputStream = outputStream;

                // Read the username sent by the client
                this.username = reader.readLine();
                server.broadcastMessage("[" + username + "] has joined the chat.", this); // Notify others about the new user

                while (!clientSocket.isClosed()) {
                    String message = reader.readLine();

                    if (message == null) {
                        break;
                    }

                    message = censorMessage(message);
                    System.out.println("[" + username + "] : " + message);

                    if (message.trim().equalsIgnoreCase("#exit")) {
                        server.broadcastMessage("[" + this.username + "] has left the chat", this);
                        server.removeClient(this);
                        break;
                    }

                    server.broadcastMessage(message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the unexpected disconnection
                System.out.println("[" + username + "] : " + " disconnected unexpectedly.");
                server.removeClient(this);
                try {
                    server.broadcastMessage("[" + username + "] : " + " disconnected unexpectedly.", this);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } finally {
                try {
                    server.broadcastMessage("[" + username + "] : " + " disconnected unexpectedly.", this);
                    clientSocket.close();
                } catch (IOException e) {
                    try {
                        server.broadcastMessage("[" + username + "] : " + " disconnected unexpectedly.", this);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        }

        public String getUsername() {
            return username;
        }

        public void sendMessage(String message) throws IOException {
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(message);
        }

        private String censorMessage(String message) {
            Set<String> bannedWords = server.getBannedWords();
            String[] words = message.split("\\s+|(?=[,.!?;])|(?<=[,.!?;])");
            StringBuilder censoredMessage = new StringBuilder();

            for (String word : words) {
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
}
