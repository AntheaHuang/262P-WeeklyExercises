import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Thirtytwo {
    private static List<String> partition(String dataStr, int nLines){
        List<String> lines = Arrays.asList(dataStr.split("\n"));
        List<String> partitions = new ArrayList<>();
		for(int i = 0; i < lines.size(); i += nLines){
			int endIndex = Math.min(i + nLines, lines.size());
			partitions.add(String.join("\n", lines.subList(i, endIndex)));
		}
		return partitions;
    }

    private static List<Map.Entry<String, Integer>> splitWords(String dataStr)throws IOException {
        //containing scan & remove stopwords
        String pathToStopwordsFile = "../stop_words.txt";
        Set<String> stopwords = Set.of(Files.readString(Path.of(pathToStopwordsFile)).split(","));

        List<String> words = Arrays.stream(Pattern.compile("[\\W_]+")
                        .matcher(dataStr.toLowerCase())
                        .replaceAll(" ")
                        .split("\\s+"))
                        .filter(w -> w.length() > 1 && !stopwords.contains(w))
                        .collect(Collectors.toList());

        List<Map.Entry<String, Integer>> result = new ArrayList<>();
        for (String word : words) {
            result.add(new AbstractMap.SimpleEntry<>(word, 1));
        }
        return result;
    }

    private static Map<String, List<Map.Entry<String, Integer>>> regroup(List<List<Map.Entry<String, Integer>>> pairsList) {
		Map<String, List<Map.Entry<String, Integer>>> mapping = new HashMap<>();
		for(List<Map.Entry<String, Integer>> pairs : pairsList){
			for(Map.Entry<String, Integer> pair : pairs){
				if( mapping.containsKey(pair.getKey()) ){
					mapping.get(pair.getKey()).add(pair);
				} else {
					List<Map.Entry<String, Integer>> newList = new ArrayList<>();
					newList.add(pair);
					mapping.put(pair.getKey(), newList);
				}
			}
		}
		return mapping;
	}

    private static Map.Entry<String, Integer> countWords(Map.Entry<String, List<Map.Entry<String, Integer>>> mapping) {
		int freq = mapping.getValue().stream().map(Map.Entry::getValue).reduce(Integer::sum).orElse(0);

		return new AbstractMap.SimpleEntry<>(mapping.getKey(), freq);
	}

    private static String readFile(String pathToFile) throws IOException {
        String words = Files.readString(Paths.get(pathToFile));
        return words;
	}

    private static List<Map.Entry<String, Integer>> sortFreqs(List<Map.Entry<String, Integer>> wordFreq) {
		return wordFreq.stream()
        .sorted(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder()))
        .collect(Collectors.toList());
	}

    public static void main(String[] args) throws IOException{
        String pathToFile = args[0];

        List<List<Map.Entry<String, Integer>>> splits = partition(readFile(pathToFile), 200).stream()
                .map(dataStr -> {
                    try {
                        return splitWords(dataStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Collections.<Map.Entry<String, Integer>>emptyList();
                    }
                })
                .collect(Collectors.toList());
        
        Map<String, List<Map.Entry<String, Integer>>> splitsPerWord = regroup(splits);
        List<Map.Entry<String, Integer>> wordFreqs = sortFreqs(splitsPerWord.entrySet().stream().map(Thirtytwo::countWords).collect(Collectors.toList()));
        wordFreqs.stream().limit(25).forEach(entry -> System.out.println(entry.getKey() + "  -  " + entry.getValue())); 

    }
}
