import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class Seven {
  public static void main(String[] args) throws IOException{
    String pathPrefix = "../";
    Set<String> stopwordSet = Set.of(Files.readString(Path.of(pathPrefix+"stop_words.txt")).split(","));
    Map<String,Integer> wordCount = new HashMap<>();
    Files.lines(Path.of(pathPrefix+args[0])).map(String::toLowerCase).map(l->l.replaceAll("[^a-z]"," ")).flatMap(l->Arrays.stream(l.split("\\s+"))).filter(w->w.length() > 1&& !stopwordSet.contains(w)).forEach(w->wordCount.put(w,wordCount.getOrDefault(w, 0)+1));
    wordCount.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder())).limit(25).forEach(e->{
      System.out.println(e.getKey() + " - " + e.getValue());
    });
  }

}