import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Ten {
    private Object value;

    public Ten(Object v){
        value = v;
    }

    public Ten bind(IFunction func){
        value = func.call(value);
        return this;
    }

    public void PrintMe(){
        System.out.println(value);
    }

    public static void main(String[] args){
        String prefix = "../";
        Ten program = new Ten(prefix + args[0]);
        program.bind(new readFile()).bind(new wordCount()).bind(new sort()).bind(new top25()).PrintMe();
    }

}

interface IFunction{
    Object call(Object arg);
}

class readFile implements IFunction{
    public Object call(Object obj){
        List<String> wordList = new ArrayList<>();
        String fileInput = (String) obj;

        try(BufferedReader br = new BufferedReader(new FileReader(fileInput))){
            String line;
            while((line = br.readLine()) != null){
                //turn into lower case
                line = line.toLowerCase();
                //replace non-letter characters with " "
                line = line.replaceAll("[^a-z]", " ");
                //split with whitespace characters
                for(String word:line.split("\\s+")){
                wordList.add(word);
                }
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }

        return wordList;
    }
}

class wordCount implements IFunction{
    public Object call(Object obj){
        HashMap<String,Integer> wordCount = new HashMap<>();
        List<String> wordList = (ArrayList<String>) obj;
        String prefix = "../";
        Set<String> stopwordSet = new HashSet<>();

        try(BufferedReader br = new BufferedReader(new FileReader(prefix+"stop_words.txt"))){
            //there's only 1 line inside the stop_words.txt
            String words = br.readLine();
            //split the line with ","
            String[] stopwordList = words.split(",");
    
            for(String stopword:stopwordList){
                //add the word into the set 
                stopwordSet.add(stopword);
            }
            
        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    
        for(String word:wordList){
            //words with length < 2 and stopwords don't count
            if(word.length() <= 1 ||stopwordSet.contains(word)){
                continue;
            }
            //store/update the word count
            wordCount.put(word,wordCount.getOrDefault(word, 0)+1);
        }

        return wordCount;
    }
}

class sort implements IFunction{
    public Object call(Object obj){
        HashMap<String,Integer> wordCount = (HashMap<String,Integer>) obj;

        List<Map.Entry<String,Integer>> wordCountList = new ArrayList<>(wordCount.entrySet());       
        wordCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        
        return wordCountList;
    }
}

class top25 implements IFunction{
    public Object call(Object obj){
        StringBuffer res = new StringBuffer();
        int i=0;
        List<Map.Entry<String,Integer>> wordCountList = (List<Map.Entry<String,Integer>>) obj;
        for(Map.Entry<String,Integer> entry: wordCountList){
            res.append(entry.getKey() + " - "+entry.getValue() + "\n");
            i++;
            if(i == 25){
            break;
            }
        }
        return res.toString();
    }
}
