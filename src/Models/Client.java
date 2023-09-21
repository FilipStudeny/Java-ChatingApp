package Models;

import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public String username;
    public Socket socket;
    public InetAddress address;

    public Client(String username, Socket socket, InetAddress address) {
        this.username = username;
        this.socket = socket;
        this.address = address;
    }
}