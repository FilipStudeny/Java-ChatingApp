import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        final String serverAddress = "localhost"; // Change this to the server's IP address if needed
        final int serverPort = 1234; // Change this to the server's port

        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print("Enter your username: ");
            String username = userInput.readLine();
            writer.println(username); // Send the username to the server

            System.out.println("Connected to the server as " + username + ". Type '#exit' to disconnect.");

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = reader.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from the server.");
                }
            });
            receiveThread.start();

            String userInputLine;
            while ((userInputLine = userInput.readLine()) != null) {
                writer.println(userInputLine);
                if (userInputLine.trim().equalsIgnoreCase("#exit")) {
                    break;
                }
            }

            receiveThread.interrupt(); // Stop the receiving thread when done
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
