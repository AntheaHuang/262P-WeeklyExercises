import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frequencies2 implements ICount{
    public List<Map.Entry<String,Integer>> countWords(List<String> wordList){
        HashMap<String,Integer> wordCount = new HashMap<>();

        for(String word:wordList){
            //count according to first letter
            String first = word.substring(0,1);
            wordCount.put(first,wordCount.getOrDefault(first, 0)+1);
        }

        List<Map.Entry<String,Integer>> wordCountList = new ArrayList<>(wordCount.entrySet());  
        wordCountList.sort(Comparator.comparing(Map.Entry::getKey));

        return wordCountList;
    }
}

