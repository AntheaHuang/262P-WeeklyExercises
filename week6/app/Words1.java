import java.io.*;
import java.util.*;



public class Words1 implements IExtract{
    @Override
    public List<String> extractWords(String filePath) throws IOException {
        String fileStopwords = "../../stop_words.txt";
        String fileInput = filePath;
        List<String> tempWordList = new ArrayList<>();
        Set<String> stopwordSet = new HashSet<>();
        List<String> result = new ArrayList<>();

        // Read txt file
        try(BufferedReader br = new BufferedReader(new FileReader(fileInput))){
            String line;
            while((line = br.readLine()) != null){
              //turn into lower case
              line = line.toLowerCase();
              //replace non-letter characters with " "
              line = line.replaceAll("[^a-z]", " ");
              //split with whitespace characters
              for(String word:line.split("\\s+")){
                tempWordList.add(word);
              }
            }  
        }catch(IOException e){
            System.err.println(e.getMessage());
        }

        // Read stop words file
        try(BufferedReader br = new BufferedReader(new FileReader(fileStopwords))){
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

        // Filter
        for (String word : tempWordList) {
            if (word.length() > 1 && !stopwordSet.contains(word)) {
                result.add(word);
            }
        }

        return result;
    }
}
