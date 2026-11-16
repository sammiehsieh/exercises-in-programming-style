import java.util.*;

public class WordFrequencyController extends ActiveWFObject {
    private ActiveWFObject storageManager;

    @SuppressWarnings("unchecked")
    @Override
    protected void dispatch(Object[] message) {
        String mess = message[0].toString();
        if (mess.equals("run")) {
            run((ActiveWFObject) message[1]);
        } else if (mess.equals("top25")) {
            display((ArrayList<Map.Entry<String, Integer>>) message[1]);
        } else {
            throw new RuntimeException("Message not understood " + mess);
        }
    }

    private void run(ActiveWFObject sManager) {
        storageManager = sManager;
        send(storageManager, new Object[] { "send_word_freqs", this });
    }

    private void display(ArrayList<Map.Entry<String, Integer>> wordFreqs) {
        for (int i = 0; i < 25 && i < wordFreqs.size(); i++) {
            Map.Entry<String, Integer> wf = wordFreqs.get(i);
            String word = wf.getKey();
            String count = wf.getValue().toString();
            System.out.println(word + "  -  " + count);
        }
        send(storageManager, new Object[] { "die" });
        stopMe = true;
    }
}