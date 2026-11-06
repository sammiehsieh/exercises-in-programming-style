package plugins;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import api.WordCounter;

public class FirstLetterCounter implements WordCounter {
    @Override
    public Map<String, Integer> count(List<String> words) {
        Map<String, Integer> freqs = new TreeMap<>();

        for (String w : words) {
            String firstLetter = w.substring(0, 1);
            freqs.put(firstLetter, freqs.getOrDefault(firstLetter, 0) + 1);
        }

        return freqs
                // separate Map into each object, and the type is Map.Entry<String, Integer>
                // equal to `.entries` in javascript
                .entrySet().stream()
                // sorted to descending
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                // only keep the top 25
                .limit(25)
                // transform from stream to Map:
                // (1) how to get key
                // (2) how to get value
                // (3) if key duplicates, keep which one
                // (4) into what kind of Map
                // equal to `.fromEntries` in javascript
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue(),
                        (a, b) -> a,
                        () -> new LinkedHashMap<>()));
    }
}
