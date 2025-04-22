import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Eight {
    public static void main(String[] args) throws IOException{
        String pathPrefix = "../";
        List<String> words;
        Map<String,Integer> wordCount = new HashMap<>();

        Set<String> stopwordSet = Set.of(Files.readString(Path.of(pathPrefix+"stop_words.txt")).split(","));

        try(BufferedReader br = new BufferedReader(new FileReader(pathPrefix+args[0]))){
            words = parse(br,new ArrayList<>(),stopwordSet);
        }

        for (String word : words){
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
      

        List<Map.Entry<String,Integer>> wordCountList = new ArrayList<>(wordCount.entrySet());
        wordCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        int i=0;

        for(Map.Entry<String,Integer> entry:wordCountList){
            String res = entry.getKey() + " - "+entry.getValue();
            System.out.println(res);
            i++;
            if(i == 25){
                break;
            }
        }
    }

    private static List<String> parse(BufferedReader br,List<String> words,Set<String> stopwordSet) throws IOException{
        String word = "";
        int ch;

        while((ch=br.read()) != -1){
            char c = Character.toLowerCase((char) ch);
            
            if(c>='a' && c<='z'){
                word = word + c;
            }else{
                if(!stopwordSet.contains(word) && word.length() > 1){
                    words.add(word);
                    break;
                }
                word = "";
            }
        }
        if(ch == -1){
            if(!stopwordSet.contains(word) && word.length() > 1){
                words.add(word);
            }
            return words;
        }
        return parse(br, words, stopwordSet);
    }
}
