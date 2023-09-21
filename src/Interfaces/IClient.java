package Interfaces;

import java.io.PrintWriter;
import java.net.Socket;

public interface IClient {

    Socket getSocket();
    PrintWriter getWriter();
    String getAddress();

    IUser getUser();
    void setUser(IUser user);
}
