package api;

import java.util.List;

public interface WordExtractor {
    List<String> extractWords(String path);
}