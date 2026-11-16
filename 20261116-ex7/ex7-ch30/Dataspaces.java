import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dataspaces {

    // Data space 1: all the words waiting to be counted
    static BlockingQueue<String> wordSpace = new LinkedBlockingQueue<String>();
    // Data space 2: the partial word counts, one map from each thread
    static BlockingQueue<Map<String, Integer>> freqSpace = new LinkedBlockingQueue<Map<String, Integer>>();

    static Set<String> stopWords = new HashSet<String>();

    // Each worker thread runs this class
    static class WordWorker implements Runnable {
        public void run() {
            // This map only belongs to this thread, so it is the partial count
            Map<String, Integer> wordFreqs = new HashMap<String, Integer>();

            while (true) {
                String word;
                try {
                    // wait at most 1 second for a word
                    // `poll` take the word out and remove it from queue
                    // and this action is locked by `LinkedBlockingQueue` internal
                    word = wordSpace.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    break;
                }

                // null means nothing came in 1 second, so there is no more work
                if (word == null) {
                    break;
                }

                if (!stopWords.contains(word)) {
                    if (wordFreqs.containsKey(word)) {
                        wordFreqs.put(word, wordFreqs.get(word) + 1);
                    } else {
                        wordFreqs.put(word, 1);
                    }
                }
            }

            // put the partial count into the second data space
            freqSpace.add(wordFreqs);
        }
    }

    public static void main(String[] args) throws Exception {
        // read the stop words
        String stopText = new String(Files.readAllBytes(Paths.get("../../stop_words.txt")));
        for (String w : stopText.trim().split(",")) {
            stopWords.add(w);
        }

        // The main thread puts all the words into the word space first
        String text = new String(Files.readAllBytes(Paths.get(args[0]))).toLowerCase();
        Matcher matcher = Pattern.compile("[a-z]{2,}").matcher(text);
        while (matcher.find()) {
            wordSpace.add(matcher.group());
        }

        // create 5 worker threads and start them
        List<Thread> workers = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++) {
            workers.add(new Thread(new WordWorker()));
        }
        for (Thread t : workers) {
            t.start();
        }

        // wait until all the workers are done
        for (Thread t : workers) {
            t.join();
        }

        // Merge the partial counts from the second data space
        Map<String, Integer> wordFreqs = new HashMap<String, Integer>();
        while (!freqSpace.isEmpty()) {
            Map<String, Integer> partial = freqSpace.take();
            for (Map.Entry<String, Integer> entry : partial.entrySet()) {
                String word = entry.getKey();
                if (wordFreqs.containsKey(word)) {
                    wordFreqs.put(word, wordFreqs.get(word) + entry.getValue());
                } else {
                    wordFreqs.put(word, entry.getValue());
                }
            }
        }

        // sort by count from high to low
        List<Map.Entry<String, Integer>> sorted = new ArrayList<Map.Entry<String, Integer>>(wordFreqs.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        // print the top 25
        for (int i = 0; i < 25 && i < sorted.size(); i++) {
            String word = sorted.get(i).getKey();
            String count = sorted.get(i).getValue().toString();
            System.out.println(word + "  -  " + count);
        }
    }
}