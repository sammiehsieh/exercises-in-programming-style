package plugins;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import api.WordExtractor;

public class NormalExtractor implements WordExtractor {
    @Override
    public List<String> extractWords(String path) {
        // 1. read the article
        String file = "";
        try {
            file = Files.readString(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }

        // 2. read the stop words file and transform to set
        Set<String> stopWords = new HashSet<>();
        try {
            String stopWordFile = Files.readString(Paths.get("../stop_words.txt"));
            stopWords.addAll(Arrays.asList(stopWordFile.split(",")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. change words to lower case, split them, and filter to goals
        String lwCase = file.toLowerCase();
        String[] words = lwCase.split("[\\W_]+");
        List<String> moreThan2Words = Arrays.stream(words)
                .filter(w -> w.length() > 1 && !stopWords.contains(w))
                .toList();

        return moreThan2Words;
    }
}