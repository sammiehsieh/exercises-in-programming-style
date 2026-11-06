package api;

import java.util.List;
import java.util.Map;

public interface WordCounter {
    Map<String, Integer> count(List<String> words);
}