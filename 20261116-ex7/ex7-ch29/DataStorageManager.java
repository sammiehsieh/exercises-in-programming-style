import java.util.*;
import java.io.IOException;
import java.nio.file.*;

public class DataStorageManager extends ActiveWFObject {
    private String data = "";
    private ActiveWFObject stopWordManager;

    @Override
    protected void dispatch(Object[] message) {
        String mess = message[0].toString();
        if (mess.equals("init")) {
            init((String) message[1], (ActiveWFObject) message[2]);
        } else if (mess.equals("send_word_freqs")) {
            processWords((ActiveWFObject) message[1]);
        } else {
            send(stopWordManager, message);
        }
    }

    private void init(String pathToFile, ActiveWFObject swManager) {
        stopWordManager = swManager;
        try {
            String text = Files.readString(Path.of(pathToFile));
            String usefulText = text.replaceAll("[\\W_]+", " ").toLowerCase();
            data = usefulText;
        } catch (IOException e) {
            System.out.println("Error when reading text file: " + e.getMessage());
        }
    }

    private void processWords(ActiveWFObject recipient) {
        String[] words = data.trim().split("\\s+");
        for (String w : words) {
            send(stopWordManager, new Object[] { "filter", w });
        }
        send(stopWordManager, new Object[] { "top25", recipient });
    }
}