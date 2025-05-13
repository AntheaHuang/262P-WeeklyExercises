import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;


public class Twenty {
    public static String configPath = "../config.properties";

	public static IExtract Extractor;
	private static ICount Counter;

	public static void load_plugins() {
		Properties prop = new Properties();

		//load properties file
		try {
			prop.load(new FileInputStream(configPath));
		} catch(Exception e){
			e.printStackTrace();
		} 

		//extract the plugins according to the properties
		String extractClass = prop.getProperty("words");
		String extractPlugin = "../deploy/" + extractClass + ".jar";
		URL extractJarURL = null;
		try {
			extractJarURL = new File(extractPlugin).toURI().toURL();
		} catch(Exception e){
			e.printStackTrace();
		} 

		String freqClass = prop.getProperty("frequencies");
		String freqPlugin = "../deploy/" + freqClass + ".jar";
		URL freqJarURL = null;
		try {
			freqJarURL = new File(freqPlugin).toURI().toURL();
		} catch(Exception e){
			e.printStackTrace();
		} 

		URLClassLoader classLoader1 = new URLClassLoader(new URL[]{extractJarURL});
		URLClassLoader classLoader2 = new URLClassLoader(new URL[]{freqJarURL});

		try {
			//load the class and get the instance
			Extractor = (IExtract) classLoader1.loadClass(extractClass).getDeclaredConstructor().newInstance();     
			Counter = (ICount) classLoader2.loadClass(freqClass).getDeclaredConstructor().newInstance();
		} catch(Exception e){
			e.printStackTrace();
		} 

	}

	public static void main(String[] args) {
        String filePath = args[0];

		load_plugins();

		try{
			List<Map. Entry<String, Integer>> wordCount = Counter.countWords(Extractor.extractWords(filePath));
			for(Map.Entry<String, Integer> entry : wordCount) {
				System.out.println(entry.getKey() + "  -  " + entry.getValue());
			}
		}catch(Exception e){
			e.printStackTrace();
		} 

		
	}
}
