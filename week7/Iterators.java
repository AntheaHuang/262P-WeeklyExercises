import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Iterators{

    //read the characters the txt file using Iterator
    static class CharactersIterator implements Iterator<Character>{
        private BufferedReader br;
        private String line;
        private int indexOfChar; 

        public CharactersIterator(String fileInput) throws IOException{
            br = new BufferedReader(new FileReader(fileInput));
            line = br.readLine();
            //To avoid the last word of a line stick together with the first word of the next line
            if(line != null){
                line += "\n";
            }
            indexOfChar = 0;
        }

        @Override
        public boolean hasNext(){
            try{
                //After reaching then end of the line, need to go to the next one
                while(line != null && indexOfChar >= line.length()){
                    line = br.readLine();
                    if(line != null){
                        line += "\n";
                    }
                    //update the index
                    indexOfChar = 0;
                }
                //if line == null, hasNext() should return false
                return line != null;
            }catch (IOException e){
                return false;
            }      
        }

        @Override
        public Character next(){    
            //after returning the current Char, update the index
            return line.charAt(indexOfChar++);
        }
    }

    static class AllWordsIterator implements Iterator<String>{
        private Iterator<Character> charIterator;
        private String nextWord;

        public AllWordsIterator(Iterator<Character> charIterator){
            this.charIterator = charIterator;
            getWord();
        }

        private void getWord(){
            //initialize every time
            nextWord = null;
            StringBuilder word = new StringBuilder();
            boolean startChar = true;
            while(charIterator.hasNext()){
                char c = charIterator.next();
                if(startChar){
                    if(Character.isLetter(c)){
                        //start of the word
                        word.append(Character.toLowerCase(c));
                        startChar = false;
                    }
                }else{
                    if(Character.isLetter(c)){
                        //following characters of the word
                        word.append(Character.toLowerCase(c));
                    }else {
                        //startChar == false && reach a non-letter character -> end of a word
                        nextWord = word.toString();
                        return;
                    }
                }
            
            }

            //after adding "/n" after eacth line, this part of code is not needed
            // if(word.length() > 0){
            //     nextWord = word.toString();
            // }
        }

        @Override
        public boolean hasNext(){
            return nextWord != null;
        }

        @Override
        public String next(){
            String next = nextWord;
            getWord();
            return next;
        }
    }

    static class NonStopWordsIterator implements Iterator<String> {
        private Iterator<String> wordIterator;
        private Set<String> stopWords;
        private String nextNonStopword;
        
        public NonStopWordsIterator(Iterator<String> wordIterator) throws IOException {
            this.wordIterator = wordIterator;
            stopWords = new HashSet<>(List.of(Files.readString(Path.of("../stop_words.txt")).split(",")));
            for (char c = 'a'; c <= 'z'; c++) {
                stopWords.add(String.valueOf(c));
            }
            getNonStopWord();
        }

        private void getNonStopWord(){
            nextNonStopword = null;
            while(wordIterator.hasNext()){
                String word = wordIterator.next();
                if(!stopWords.contains(word)){
                    nextNonStopword = word;
                    break;
                }
            }
        }

        @Override
        public boolean hasNext(){
            return nextNonStopword != null;
        }

        @Override
        public String next(){
            String next = nextNonStopword;
            getNonStopWord();
            return next;
        }
    }

    static class CountAndSortIterator implements Iterator<List<Map.Entry<String,Integer>>>{
        private Iterator<String> nonStopWordIterator;
        private Map<String,Integer> freqsMap;
        private List<Map.Entry<String,Integer>> nextSortedGroup;
        private int cnt;
        private boolean finished = false;

        public CountAndSortIterator(Iterator<String> nonStopWordIterator){
            this.nonStopWordIterator = nonStopWordIterator;
            freqsMap = new HashMap<>();
            cnt = 1;
            getFreqs();
        }

        private void getFreqs(){
            nextSortedGroup = null;
            while(nonStopWordIterator.hasNext()){
                String nonStopWord = nonStopWordIterator.next();
                freqsMap.put(nonStopWord,freqsMap.getOrDefault(nonStopWord, 0)+1);
                cnt++;
                //mimic the given python code
                if(cnt % 5000 == 0){
                    nextSortedGroup = sortCurrMap();
                    return;
                }
            }

            //after hasNext() == false, sort the whole freqsMap
            if(!finished){
                nextSortedGroup = sortCurrMap();
                finished = true;
            }
        }

        private List<Map.Entry<String,Integer>> sortCurrMap(){
            List<Map.Entry<String,Integer>> currList = new ArrayList<>(freqsMap.entrySet());
            Collections.sort(currList,(e1,e2)->(e2.getValue()-e1.getValue()));
            return currList;
        }

        @Override
        public boolean hasNext(){
            return nextSortedGroup != null;
        }

        @Override
        public List<Map.Entry<String,Integer>> next(){
            List<Map.Entry<String,Integer>> next = nextSortedGroup;
            getFreqs();
            return next;
        }
    }

    public static void main(String[] args) throws IOException {
        String fileInput = "../"+ args[0];
    
        CharactersIterator charIter = new CharactersIterator(fileInput);
        AllWordsIterator wordIter = new AllWordsIterator(charIter);
        NonStopWordsIterator nonStopIter = new NonStopWordsIterator(wordIter);
        CountAndSortIterator countSortIter = new CountAndSortIterator(nonStopIter);
    
        while (countSortIter.hasNext()) {
            System.out.println("-----------------------------");
            List<Map.Entry<String, Integer>> topList = countSortIter.next();
            for (int i = 0; i < 25; i++) {
                Map.Entry<String, Integer> entry = topList.get(i);
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
        }
    }
}