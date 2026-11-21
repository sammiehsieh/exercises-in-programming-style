import java.util.HashSet;
import java.util.Iterator;

class NonStopWords implements Iterator<String> {
    private Iterator<String> words; // upstream: word-by-word
    private HashSet<String> stopWords = new HashSet<>();
    private String nextWord = null; // useful word which was found but not sent yet

    public NonStopWords(Iterator<String> words, String stopFile) {
        this.words = words;
        // read the stop words file line-by-line
        Lines stopLines = new Lines(stopFile);
        while (stopLines.hasNext()) {
            String line = stopLines.next();
            for (String sw : line.split(",")) {
                stopWords.add(sw.strip());
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (nextWord != null) {
            return true;
        }
        // get words from upstream until it isn't in stop words
        while (words.hasNext()) {
            String w = words.next();
            // skip one character words and stop words
            if (w.length() >= 2 && !stopWords.contains(w)) {
                nextWord = w;
                return true;
            }
            // don't do things when stop word, the loop will keep going till it get next word
        }
        // no words from upstream
        return false;
    }

    @Override
    public String next() {
        // send the nextWord and clear it
        String result = nextWord;
        nextWord = null;
        return result;
    }
}