package Interfaces;

import java.io.BufferedWriter;
import java.io.IOException;

public interface IServer_Client {
    void run();
    void sendMessage() throws IOException;
    void sendUsername(BufferedWriter out) throws IOException;
    void receiveMessagesThread();
}
