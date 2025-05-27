import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Thirty {
    private static BlockingQueue<String> wordSpace = new LinkedBlockingDeque<>();
    private static BlockingQueue<Map<String,Integer>> freqSpace = new LinkedBlockingDeque<>();
    private static Set<String> stopwords = new HashSet<>();

    static class ProcessWords extends Thread{
        @Override
        public void run(){
            Map<String,Integer> wordFreqs = new HashMap<>();
            while(true){
                String word = wordSpace.poll();
                if(word == null){
                    break;
                }
                if(!stopwords.contains(word) && word.length()>1){
                    wordFreqs.put(word,wordFreqs.getOrDefault(word, 0)+1);
                }
            }
            freqSpace.add(wordFreqs);
        }
    }

    public static void main(String[] args) throws Exception{
        String pathToStopwordsFile = "../stop_words.txt";
        String pathToInputFile = args[0];

        stopwords = Set.of(Files.readString(Path.of(pathToStopwordsFile)).split(","));
        String[] words = Files.readString(Path.of(pathToInputFile)).toLowerCase().replaceAll("[\\W_]+", " ").split("\\s+");
        for(String word:words){
            wordSpace.add(word);
        }

        ProcessWords[] workers = new ProcessWords[5];
        for(int i=0; i<5; i++){
            workers[i] = new ProcessWords();
            workers[i].start();
        }

        for(ProcessWords worker: workers){
            worker.join();
        }

        Map<String,Integer> wordFreqs = new HashMap<>();
        while(!freqSpace.isEmpty()){
            Map<String,Integer> freqs = freqSpace.poll();
            for(Map.Entry<String,Integer> entry:freqs.entrySet()){
                String word = entry.getKey();
                int value = entry.getValue();
                int cnt = wordFreqs.getOrDefault(word, 0)+value;
                wordFreqs.put(word,cnt);
            }
        }

        wordFreqs.entrySet()
            .stream()
            .sorted(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder()))
            .limit(25)
            .forEach(e->{
            System.out.println(e.getKey() + " - " + e.getValue());
        });
    }
}
