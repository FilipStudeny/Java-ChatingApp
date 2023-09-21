package Models;

import Interfaces.IClient;
import Interfaces.IUser;

import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;

public class Client implements IClient {
    private final Socket socket;
    private final PrintWriter writer;
    private IUser user;

    public Client(Socket socket, PrintWriter writer) {
        this.socket = socket;
        this.writer = writer;
        this.user = new User((Inet4Address) this.socket.getInetAddress());
    }

    public String getUsername(){
        return this.user.getUsername();
    }

    public void setUsername(String username){
        this.user.setUsername(username);
    }
    @Override
    public Socket getSocket() {
        return this.socket;
    }
    @Override
    public PrintWriter getWriter() {
        return this.writer;
    }

    @Override
    public String getAddress() {
        return this.user.getAddress().toString();
    }

    @Override
    public IUser getUser() {
        return this.user;
    }
    @Override
    public void setUser(IUser user) {
        this.user = user;
    }
}