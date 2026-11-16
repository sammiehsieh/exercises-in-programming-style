import java.util.*;
import java.io.IOException;
import java.nio.file.*;

public class StopWordManager extends ActiveWFObject {
    private HashSet<String> stopWords = new HashSet<>();
    private ActiveWFObject wordFreqManager;

    @Override
    protected void dispatch(Object[] message) {
        String mess = message[0].toString();
        if (mess.equals("init")) {
            init((ActiveWFObject) message[1]);
        } else if (mess.equals("filter")) {
            filter((String) message[1]);
        } else {
            // no changes and send to `WordFreqManager`
            send(wordFreqManager, message);
        }
    }

    private void init(ActiveWFObject wfManager) {
        wordFreqManager = wfManager;
        try {
            String text = Files.readString(Path.of("../../stop_words.txt"));
            String[] parts = text.split(",");
            stopWords.addAll(Arrays.asList(parts));
        } catch (IOException e) {
            System.out.println("Error when reading stop words file: " + e.getMessage());
        }
    }

    private void filter(String word) {
        if (!stopWords.contains(word) && word.length() > 1) {
            send(wordFreqManager, new Object[] { "word", word });
        }
    }
}