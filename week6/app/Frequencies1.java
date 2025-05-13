import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frequencies1 implements ICount{
    public List<Map.Entry<String,Integer>> countWords(List<String> wordList){
        HashMap<String,Integer> wordCount = new HashMap<>();

        for(String word:wordList){
            wordCount.put(word,wordCount.getOrDefault(word, 0)+1);
        }

        List<Map.Entry<String,Integer>> wordCountList = new ArrayList<>(wordCount.entrySet());    
        wordCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        return wordCountList.subList(0, 25);
    }
}
