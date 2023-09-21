import Interfaces.IServer_Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements IServer_Client {

    private final BufferedReader serverReader;
    private final BufferedWriter out;

    public static void main(String[] args) throws IOException {
        new Client().run();
    }

    public Client() throws IOException {
        Socket clientSocket = new Socket("localhost", 8080);
        this.serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); // vytvoření BufferedWriter
    }


    @Override
    public void run() {
        try {
            sendUsername(out);
            receiveMessagesThread();
            sendMessage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage() throws IOException {
        Scanner in = new Scanner(System.in);
        while (true) {
            String message = in.nextLine();
            if (message.equals("exit")) {
                out.write("exit" + "\r\n");
                out.flush();
                System.exit(0);
            } else {
                out.write(message + "\r\n");
                out.flush();
                System.out.println("------------*Message send*------------");
            }
        }
    }

    @Override
    public void sendUsername(BufferedWriter out) throws IOException {
        System.out.print("Enter your username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        out.write(username + "\r\n");
        out.flush();
    }

    @Override
    public void receiveMessagesThread() {
        Thread receiveThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String message = serverReader.readLine();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

}
