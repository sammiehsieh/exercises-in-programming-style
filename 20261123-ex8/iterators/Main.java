import java.util.List;
import java.util.Map;

public static void main(String[] args) {
    CountAndSort data = new CountAndSort(new NonStopWords(new Words(new Lines(args[0])), "../../stop_words.txt"));
    while (data.hasNext()) {
        System.out.println("-----------------------------");
        List<Map.Entry<String, Integer>> freqs = data.next();
        for (int i = 0; i < 25 && i < freqs.size(); i++) {
            System.out.println(freqs.get(i).getKey() + "  -  " + freqs.get(i).getValue());
        }
    }
}