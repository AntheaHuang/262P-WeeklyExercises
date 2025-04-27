import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Nine {
    private String filePath;

    public Nine(String filePath){
        this.filePath = filePath;
        //function calling order: readFile() -> wordCount() -> sort() -> print()
        //take in the filepath, and wordCount() will be called at the end
        new readFile().call(this.filePath, new wordCount());
    }

    public static void main(String[] args){
        String prefix = "../";
        new Nine(prefix + args[0]);
    }

}


interface IFunction{
    void call(Object arg, IFunction func);
}

class readFile implements IFunction{
    public void call(Object obj, IFunction func){
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

        //pass the wordlist to the function wordCount(), and sort() will be called at the end of wordCount()
        func.call(wordList, new sort());
    }
}

class wordCount implements IFunction{
    public void call(Object obj, IFunction func){
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

        //pass the wordCount map to the function sort(), and print() will be called at the end of sort()
        func.call(wordCount,new print());
    }
}

class sort implements IFunction{
    public void call(Object obj, IFunction func){
        HashMap<String,Integer> wordCount = (HashMap<String,Integer>) obj;

        List<Map.Entry<String,Integer>> wordCountList = new ArrayList<>(wordCount.entrySet());       
        wordCountList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        //pass the sorted wordCountlist to the function print()
        func.call(wordCountList, null);
    }
}

class print implements IFunction{
    public void call(Object obj, IFunction func){
        int i=0;
        List<Map.Entry<String,Integer>> wordCountList = (List<Map.Entry<String,Integer>>) obj;
        for(Map.Entry<String,Integer> entry: wordCountList){
            String res = entry.getKey() + " - "+entry.getValue();
            System.out.println(res);
            i++;
            if(i == 25){
            break;
            }
        }
        return;
    }
}