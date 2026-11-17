import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    // since this program doesn't have to create `Main` object,
    // just use `Main` to be a container,
    // so we should add static to every classes and functions

    static class Pair {
        String word;
        int count;

        Pair(String word, int count) {
            this.word = word;
            this.count = count;
        }
    }

    /**
     * cuts the file data into chunks
     * 
     * @param dataStr file data
     * @param nlines  how many lines in a chunk
     */
    static List<String> partition(String dataStr, int nlines) {
        String[] lines = dataStr.split("\n");
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < lines.length; i += nlines) {
            int end = Math.min(i + nlines, lines.length);
            // join these lines back into one string (one chunk)
            chunks.add(String.join("\n", Arrays.copyOfRange(lines, i, end)));
        }
        return chunks;
    }

    /**
     * the worker of the first map: take one chunk, returns a list of (word, 1)
     * 
     * @param dataStr file data
     */
    static List<Pair> splitWords(String dataStr) throws IOException {
        // step 1: replace non-alphanumeric characters
        String[] words = dataStr.replaceAll("[\\W_]+", " ").toLowerCase().trim().split(" ");

        // step 2: load the stop words
        String content = new String(Files.readAllBytes(Paths.get("../../stop_words.txt")));
        Set<String> stopWords = new HashSet<>(Arrays.asList(content.trim().split(",")));

        // step 3: keep only the real words and give each one a count of 1
        List<Pair> result = new ArrayList<>();
        for (String w : words) {
            if (!w.isEmpty() && !stopWords.contains(w) && w.length() > 1) {
                result.add(new Pair(w, 1));
            }
        }
        return result;
    }

    /**
     * take the list of lists of pairs from the first map and groups them by word:
     * { word: [(word, 1), (word, 1), ...] }
     * 
     * @param pairsList outer: result of each chunk, inner: all the (word, 1)
     */
    static Map<String, List<Pair>> regroup(List<List<Pair>> pairsList) {
        Map<String, List<Pair>> mapping = new HashMap<>();
        for (List<Pair> pairs : pairsList) {
            for (Pair p : pairs) {
                if (!mapping.containsKey(p.word)) {
                    mapping.put(p.word, new ArrayList<>());
                }
                mapping.get(p.word).add(p);
            }
        }
        return mapping;
    }

    /**
     * take (word, [(word, 1), (word, 1), ...]) and returns (word, frequency)
     * 
     * @param mapping one word and all of its (word, 1) pairs
     */
    static Pair countWords(Map.Entry<String, List<Pair>> mapping) {
        // reduce part: many pairs are merged into one number
        int total = 0;
        for (Pair pair : mapping.getValue()) {
            total = total + pair.count;
        }
        return new Pair(mapping.getKey(), total);
    }

    /**
     * give every chunk to the thread pool, one task per chunk
     * 
     * @param chunks
     * @param pool   thread pool
     */
    static List<List<Pair>> mapSplitWords(List<String> chunks, ExecutorService pool) throws Exception {
        // send all the tasks first, so the threads can start working together
        List<Future<List<Pair>>> futures = new ArrayList<>();
        for (String chunk : chunks) {
            futures.add(pool.submit(() -> splitWords(chunk)));
        }
        // collect the answers in the same order (get waits if a task is not done yet)
        List<List<Pair>> results = new ArrayList<>();
        for (Future<List<Pair>> f : futures) {
            results.add(f.get());
        }
        return results;
    }

    /**
     * give every word to the thread pool, one task per word
     * 
     * @param entries
     * @param pool    thread pool
     */
    static List<Pair> mapCountWords(
            List<Map.Entry<String, List<Pair>>> entries, ExecutorService pool)
            throws Exception {
        // send all the tasks first
        List<Future<Pair>> futures = new ArrayList<>();
        for (Map.Entry<String, List<Pair>> entry : entries) {
            futures.add(pool.submit(() -> countWords(entry)));
        }
        // then collect the answers
        List<Pair> results = new ArrayList<>();
        for (Future<Pair> f : futures) {
            results.add(f.get());
        }
        return results;
    }

    static String readFile(String pathToFile) throws IOException {
        return new String(Files.readAllBytes(Paths.get(pathToFile)));
    }

    static List<Pair> sort(List<Pair> wordFreq) {
        List<Pair> sorted = new ArrayList<>(wordFreq);
        sorted.sort((a, b) -> b.count - a.count);
        return sorted;
    }

    public static void main(String[] args) throws Exception {
        // a pool of 4 worker threads
        ExecutorService pool = Executors.newFixedThreadPool(4);

        // map 1: each chunk is turned into (word, 1) pairs, in parallel
        List<String> chunks = partition(readFile(args[0]), 200);
        List<List<Pair>> splits = mapSplitWords(chunks, pool);

        // reshuffle: group the pairs by word
        Map<String, List<Pair>> splitsPerWord = regroup(splits);

        // map 2: each word is counted on its own, in parallel
        List<Map.Entry<String, List<Pair>>> entries = new ArrayList<>(splitsPerWord.entrySet());
        List<Pair> wordFreqs = sort(mapCountWords(entries, pool));

        pool.shutdown();

        for (int i = 0; i < 25 && i < wordFreqs.size(); i++) {
            System.out.println(wordFreqs.get(i).word + "  -  " + wordFreqs.get(i).count);
        }
    }
}