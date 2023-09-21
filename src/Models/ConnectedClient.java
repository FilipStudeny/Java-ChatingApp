package Models;

import java.net.Socket;

public class ConnectedClient {
    private String ipAddress;
    private String username;
    private Socket socket;

    public ConnectedClient(String ipAddress, String username, Socket socket) {
        this.ipAddress = ipAddress;
        this.username = username;
        this.socket = socket;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
