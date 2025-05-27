import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.stream.Collectors;

public class Twentynine{
    abstract static class ActiveWFObject extends Thread{
        public String name;
        public BlockingQueue<Object[]> queue;
        protected boolean stopMe;

        public ActiveWFObject(){
            this.name = this.getClass().toString();
            this.queue = new LinkedBlockingQueue<>();
            this.stopMe = false;
            start();
        }

        public void run(){
            while(!this.stopMe){
                Object[] message;
                try {
                    message = queue.take();
                    this.dispatch(message);

                    if(((String) message[0]).equals("die")){
                        this.stopMe = true;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        abstract void dispatch(Object[] message);
            
    }  

    static public void send(ActiveWFObject receiver, Object[] message){
        receiver.queue.offer(message);
    }

    static class DataStorageManager extends ActiveWFObject{
        private String data;
        public StopWordMananger stopWordMananger;

        public DataStorageManager(){
            this.data = "";
        }

        public void dispatch(Object[] message){
            if(((String) message[0]).equals("init")){
                this.init(Arrays.copyOfRange(message, 1, message.length));
            }else if(((String) message[0]).equals("sendWordFreqs")){
                this.processWords(Arrays.copyOfRange(message, 1, message.length));
            }else{
                send(this.stopWordMananger,message);
            }
        }

        private void init(Object[] message){
            String pathToFile = (String) message[0];
            this.stopWordMananger = (StopWordMananger) message[1];
            try {
                this.data = Files.readString(Paths.get(pathToFile)).replaceAll("[\\W_]+", " ").toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processWords(Object[] message){
            ActiveWFObject recipient = (ActiveWFObject) message[0];
            String dataStr = String.join("",this.data);
            String[] words = dataStr.split(" ");
            for(String word:words){
                send(this.stopWordMananger,new Object[]{"filter",word});
            }
            send(this.stopWordMananger,new Object[]{"top25",recipient});
        }

    }

    static class StopWordMananger extends ActiveWFObject{
        private Set<String> stopwords;
        public WordFrequencyManager wordFrequencyManager;

        public StopWordMananger(){
            this.stopwords = new HashSet<>();
        }

        public void dispatch(Object[] message){
            if(((String) message[0]).equals("init")){
                this.init(Arrays.copyOfRange(message, 1, message.length));
            }else if(((String) message[0]).equals("filter")){
                this.filter(Arrays.copyOfRange(message, 1, message.length));
            }else{
                send(this.wordFrequencyManager,message);
            }
        }

        private void init(Object[] message){
            String pathToStopwordsFile = "../stop_words.txt";

            try {
                this.stopwords = Set.of(Files.readString(Path.of(pathToStopwordsFile)).split(","));
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.wordFrequencyManager = (WordFrequencyManager) message[0];
        }

        private void filter(Object[] message){
            String word = (String) message[0];
            if(!this.stopwords.contains(word) && word.length() > 1){
                send(this.wordFrequencyManager,new Object[]{"word",word});
            }
        }
    }

    static class WordFrequencyManager extends ActiveWFObject{
        private Map<String,Integer> wordFreqs;

        public WordFrequencyManager(){
            this.wordFreqs = new HashMap<>();
        }

        public void dispatch(Object[] message){
            if(((String) message[0]).equals("word")){
                this.increCount(Arrays.copyOfRange(message, 1, message.length));
            }else if(((String) message[0]).equals("top25")){
                this.top25(Arrays.copyOfRange(message, 1, message.length));
            }
        }

        private void increCount(Object[] message){
            String word = (String) message[0];
            this.wordFreqs.put(word,this.wordFreqs.getOrDefault(word, 0)+1);
        }

        private void top25(Object[] message){
            WordFrequencyController recipient = (WordFrequencyController) message[0];
            List<Map.Entry<String,Integer>> freqsSorted = this.wordFreqs.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
            send(recipient, new Object[]{"top25",freqsSorted});
        }

    }

    static class WordFrequencyController extends ActiveWFObject{
        public DataStorageManager storageManager;

        public WordFrequencyController(){

        }

        public void dispatch(Object[] message){
            if(((String) message[0]).equals("run")){
                this.run(Arrays.copyOfRange(message, 1, message.length));
            }else if(((String) message[0]).equals("top25")){
                this.display(Arrays.copyOfRange(message, 1, message.length));
            }else{
                throw new IllegalArgumentException("Message not understood " + message[0]);
            }
        }

        private void run(Object[] message){
            this.storageManager = (DataStorageManager) message[0];
            send(this.storageManager, new Object[]{"sendWordFreqs", this});
        }

        private void display(Object[] message){
            List<Map.Entry<String,Integer>> wordFreqs = (List<Map.Entry<String,Integer>>) message[0];
            wordFreqs.stream().limit(25).forEach(e->{
                System.out.println(e.getKey() + " - " + e.getValue());
              });
            send(this.storageManager, new Object[]{"die"});
            this.stopMe = true;
        }
    }

    public static void main(String[] args){
        String filePath = args[0];

        WordFrequencyManager wordFrequencyManager = new WordFrequencyManager();

        StopWordMananger stopWordMananger = new StopWordMananger();
        send(stopWordMananger,new Object[]{"init",wordFrequencyManager});

        DataStorageManager storageManager = new DataStorageManager();
        send(storageManager,new Object[]{"init",filePath,stopWordMananger});

        WordFrequencyController controller = new WordFrequencyController();
        send(controller,new Object[]{"run",storageManager});

        try {
            wordFrequencyManager.join();
            stopWordMananger.join();
            storageManager.join();
            controller.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}