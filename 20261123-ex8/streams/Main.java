import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        // read the file line-by-line and make a line an element
        Stream<String> stream = Files.lines(Path.of(args[0]));

        // flatMap puts all words into one stream
        Stream<String> words = stream.flatMap(line -> {
            String[] sp = line.toLowerCase().split("[^a-z0-9]+");
            return Arrays.stream(sp);
        });

        Set<String> stopwords = Files.lines(Path.of("../../stop_words.txt"))
                .flatMap(line -> Arrays.stream(line.split(",")))
                .collect(Collectors.toSet());

        Stream<String> nonStop = words.filter(w -> w.length() >= 2 && !stopwords.contains(w));

        // count each word, nothing runs until this collect is called
        Map<String, Long> freqs = nonStop.collect(Collectors.groupingBy(n -> n, Collectors.counting()));

        freqs.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(25)
                .forEach(e -> System.out.println(e.getKey() + "  -  " + e.getValue()));
    }
}