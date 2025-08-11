import java.util.LinkedHashMap;
import java.util.Map;

public class CharFrequency {
    public static void main(String[] args) {
        String input = "aaabbccccdddddfffff";
        System.out.println(compressString(input));
    }

    public static String compressString(String str) {
        StringBuilder result = new StringBuilder();
        Map<Character, Integer> frequencyMap = new LinkedHashMap<>();

        for (char ch : str.toCharArray()) {
            frequencyMap.put(ch, frequencyMap.getOrDefault(ch, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            result.append(entry.getKey()).append("(").append(entry.getValue()).append(") ");
        }

        return result.toString().trim();
    }
}
