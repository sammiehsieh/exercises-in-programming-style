import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class CountAndSort implements Iterator<List<Map.Entry<String, Integer>>> {
    private Iterator<String> words; // upstream: non-stop words one-by-one
    private HashMap<String, Integer> freqs = new HashMap<>();
    private int count = 0; // how many words we have counted so far
    private boolean finished = false;

    public CountAndSort(Iterator<String> words) {
        this.words = words;
    }

    @Override
    public boolean hasNext() {
        return !finished;
    }

    @Override
    public List<Map.Entry<String, Integer>> next() {
        while (words.hasNext()) {
            String w = words.next();
            freqs.put(w, freqs.getOrDefault(w, 0) + 1);
            count++;
            if (count % 5000 == 0) {
                // return it every 5000 words
                return sortedSnapshot();
            }
        }
        // there are no more words from upstream
        finished = true;
        return sortedSnapshot();
    }

    private List<Map.Entry<String, Integer>> sortedSnapshot() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(freqs.entrySet());
        list.sort((a, b) -> b.getValue() - a.getValue());
        return list;
    }
}