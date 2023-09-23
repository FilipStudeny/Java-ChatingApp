package Models;

import java.util.Set;

public interface IFileHandler {

    String censorMessage(String message);
    void loadBannedWords();
    void saveMessageToFile(Message message);
}
