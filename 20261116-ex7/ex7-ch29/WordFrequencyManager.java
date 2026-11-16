import java.util.*;

public class WordFrequencyManager extends ActiveWFObject {
    private HashMap<String, Integer> wordFreqs = new HashMap<>();

    @Override
    protected void dispatch(Object[] message) {
        String mess = message[0].toString();
        if (mess.equals("word")) {
            incrementCount((String) message[1]);
        }
        if (mess.equals("top25")) {
            ActiveWFObject recipient = (ActiveWFObject) message[1];
            top25(recipient);
        }
    }

    private void incrementCount(String word) {
        if (wordFreqs.containsKey(word)) {
            Integer count = wordFreqs.get(word);
            count++;
            wordFreqs.put(word, count);
        } else {
            wordFreqs.put(word, 1);
        }
    }

    private void top25(ActiveWFObject recipient) {
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(wordFreqs.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        send(recipient, new Object[] { "top25", list });
    }
}
