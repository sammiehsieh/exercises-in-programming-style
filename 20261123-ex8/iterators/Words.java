import java.util.Iterator;

class Words implements Iterator<String> {
    private Iterator<String> lines; // upstream: from line-by-line
    private String[] words = new String[0]; // words split from the current line
    private int index = 0; // index of the next word to send

    public Words(Iterator<String> lines) {
        this.lines = lines;
    }

    @Override
    public boolean hasNext() {
        while (true) {
            // skip empty string
            while (index < words.length && words[index].isEmpty()) {
                index++;
            }
            // there is still words in this line
            if (index < words.length) {
                return true;
            }
            // no more words and it should get next line from upstream
            // end only when there is no more words from upstream
            if (!lines.hasNext()) {
                return false;
            }
            words = lines.next().toLowerCase().split("[^a-z0-9]+");
            index = 0;
        }
    }

    @Override
    public String next() {
        String result = words[index];
        index++;
        return result;
    }
}