import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

class Lines implements Iterator<String> {
    private BufferedReader reader;
    private String nextLine = null; // have read the line but haven't sent back

    public Lines(String filename) {
        try {
            reader = Files.newBufferedReader(Paths.get(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        // there is one line ready, so no need for read again
        if (nextLine != null) {
            return true;
        }
        try {
            nextLine = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // when read `null` means the file end
        return nextLine != null;
    }

    @Override
    public String next() {
        String result = nextLine;
        nextLine = null; // clear it after sent, so `hasNext` can read again
        return result;
    }
}