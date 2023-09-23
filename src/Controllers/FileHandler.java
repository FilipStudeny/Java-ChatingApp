package Controllers;

import Models.IFileHandler;
import Models.Message;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class FileHandler implements IFileHandler {

    private Set<String> bannedWords;
    private final String logFile;

    public FileHandler(String logFile) {
        this.logFile = logFile;
        this.loadBannedWords();
    }

    @Override
    public String censorMessage(String message) {
        Set<String> bannedWords = this.bannedWords;
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

    @Override
    public void loadBannedWords() {
        Set<String> bannedWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("./ban_list.txt"))) {
            String word;
            while ((word = reader.readLine()) != null) {
                bannedWords.add(word.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.bannedWords = bannedWords;
    }

    @Override
    public void saveMessageToFile(Message message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFile, true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedTimestamp = dateFormat.format(message.timestamp());
            String formattedMessage = formattedTimestamp + " - [ " + message.username() + "] => " + message.content();
            writer.write(formattedMessage);
            writer.newLine(); // Add a newline for the next message
            System.out.println("Message saved to " + this.logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
