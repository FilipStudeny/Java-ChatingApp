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

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
        System.out.println("WARNING [" + client.getUsername() + "] has left the chat");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(1234);
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

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private OutputStream outputStream;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (InputStream inputStream = clientSocket.getInputStream();
                 OutputStream outputStream = clientSocket.getOutputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                this.outputStream = outputStream;

                // Read the username sent by the client
                this.username = reader.readLine();
                broadcastMessage("[" + username + "] has joined the chat."); // Notify others about the new user

                while (!clientSocket.isClosed()) {
                    String message = (reader.readLine());

                    if (message == null) {
                        break;
                    }

                    message = censorMessage(message);
                    System.out.println("[" + username + "] : " + message);

                    if (message.trim().equalsIgnoreCase("#exit")) {
                        removeClient(this);
                        break;
                    }

                    broadcastMessage("[" + username + "] : " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the unexpected disconnection
                System.out.println("[" + username + "] : " + " disconnected unexpectedly.");
                removeClient(this);
                try {
                    broadcastMessage("[" + username + "] : " + " disconnected unexpectedly.");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getUsername() {
            return username;
        }

        private void broadcastMessage(String message) throws IOException {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client != this) {
                        PrintWriter writer = new PrintWriter(client.outputStream, true);
                        writer.println(message);
                    }
                }
            }
        }



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
}
