import api.WordExtractor;
import api.WordCounter;

import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. load the config file
        Properties config = new Properties();
        config.load(new FileInputStream("config.properties"));

        // 2. load the class dynamically and transform to interface
        String extractorName = config.getProperty("extractor");
        WordExtractor extractor = (WordExtractor) Class.forName(extractorName).getDeclaredConstructor().newInstance();
        String counterName = config.getProperty("counter");
        WordCounter counter = (WordCounter) Class.forName(counterName).getDeclaredConstructor().newInstance();

        // 3. only call through interface and show the results
        List<String> words = extractor.extractWords(args[0]);
        Map<String, Integer> freqs = counter.count(words);
        for (Map.Entry<String, Integer> e : freqs.entrySet()) {
            System.out.println(e.getKey() + "  -  " + e.getValue());
        }
    }
}
