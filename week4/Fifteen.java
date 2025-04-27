import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Fifteen {
    public static void main(String[] args){
        String prefix = "../";

        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordFilter stopWordFilter = new StopWordFilter(wfapp);
        DataStorage dataStorage = new DataStorage(wfapp, stopWordFilter);
        WordFrequencyCounter wordFreqCounter = new WordFrequencyCounter(wfapp, dataStorage);

        wfapp.run(prefix+args[0]);
    }
}

interface LoadEventHandler{
    void handleLoad(String filePath);
}

interface DoWorkEventHandler{
    void handleWork();
}

interface EndEventHandler{
    void handleEnd();
}

interface WordEventHandler{
    void handleWord(String word);
}

class WordFrequencyFramework{
    List<LoadEventHandler> loadHandlers= new ArrayList<>();
    List<DoWorkEventHandler> doWorkHandlers= new ArrayList<>();
    List<EndEventHandler> endHandlers= new ArrayList<>();

    public void registerLoadEvent(LoadEventHandler handler) {
        loadHandlers.add(handler);
    }

    public void registerDoWorkEvent(DoWorkEventHandler handler) {
        doWorkHandlers.add(handler);
    }

    public void registerEndEvent(EndEventHandler handler) {
        endHandlers.add(handler);
    }

    public void run(String filePath){
        for (LoadEventHandler loadHandler : loadHandlers) {
            loadHandler.handleLoad(filePath);
        }
        for (DoWorkEventHandler doWorkHandler : doWorkHandlers){
            doWorkHandler.handleWork();
        }
        for (EndEventHandler endHandler : endHandlers){
            endHandler.handleEnd();
        }
    }
}

class DataStorage{
    String data = "";
    StopWordFilter stopWordFilter;
    List<WordEventHandler> wordEventHandlers = new ArrayList<>();

    public DataStorage(WordFrequencyFramework wfapp, StopWordFilter stopWordFilter){
        this.stopWordFilter = stopWordFilter;
        wfapp.registerLoadEvent(this::load);
        wfapp.registerDoWorkEvent(this::produceWords);
    }

    public void load(String filePath){
        try {
            data = new String(Files.readAllBytes(Path.of(filePath)));
            data = data.toLowerCase().replaceAll("[^a-z]"," ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void produceWords(){
        for (String word : data.split("\\s+")) {
            if (!stopWordFilter.isStopWord(word) && word.length() > 1) {
                for (WordEventHandler handler : wordEventHandlers) {
                    handler.handleWord(word);
                }
            }
        }
    }

    public void registerWordEvent(WordEventHandler handler) {
        wordEventHandlers.add(handler);
    }
}

class StopWordFilter{
    String prefix = "../";
    Set<String> stopwordSet;

    public StopWordFilter(WordFrequencyFramework wfapp){
        wfapp.registerLoadEvent(this::load);
    }

    public void load(String ignore){
        try {
            stopwordSet = Set.of(Files.readString(Path.of(prefix+"stop_words.txt")).split(","));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStopWord(String word){
        return stopwordSet.contains(word);
    }
}

class WordFrequencyCounter{
    Map<String, Integer> wordCount = new HashMap<>();

    public WordFrequencyCounter(WordFrequencyFramework wfapp,DataStorage dataStorage){
        dataStorage.registerWordEvent(this::incrementCount);
        wfapp.registerEndEvent(this::print);
    }

    public void incrementCount(String word){
        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
    }

    public void print(){
        wordCount.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder())).limit(25).forEach(e->{
            System.out.println(e.getKey() + " - " + e.getValue());
        });
    }
}

