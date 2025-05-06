import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClasses {
    @SuppressWarnings("deprecation")
    public static void main (String args[]){
        //get the jar file
        String jarFilePath = args[0];
        File jarFile = new File(jarFilePath);
        //use List to store classes
        List<String> jarClasses = new ArrayList<>();

        try(JarFile jar = new JarFile(jarFile)){
            //get all the entires (files and directories) in the jar file
            Enumeration<JarEntry> entries = jar.entries();

            //loop through all entries
            while(entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                if(entry.getName().endsWith(".class")){
                    //normalization
                    String jarClass = entry.getName().replace('/', '.').replace(".class", "");
                    jarClasses.add(jarClass);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        Collections.sort(jarClasses);

        try{
            //create the url which points inside the jar file
            URL[] urls = {new URL("jar:file:" + jarFilePath + "!/")};
            //create classLoader 
            URLClassLoader classLoader = URLClassLoader.newInstance(urls);

            for(String jarClass:jarClasses){
                //reflection - load the class and its methods and fields at runtime 
                Class<?> currClass = Class.forName(jarClass,false,classLoader);
                //count methods and fields
                Method[] methods = currClass.getDeclaredMethods();
                Field[] fields = currClass.getDeclaredFields();

                int cntPublic = 0;
                int cntPrivate = 0;
                int cntProtected = 0;
                int cntStatic = 0;

                for(Method method:methods){
                    //get the flag for the method
                    int methodFlag = method.getModifiers();
                    if(Modifier.isPublic(methodFlag)) cntPublic++;
                    if(Modifier.isPrivate(methodFlag)) cntPrivate++;
                    if(Modifier.isProtected(methodFlag)) cntProtected++;
                    if(Modifier.isStatic(methodFlag)) cntStatic++;
                }

                System.out.println("----------" + jarClass + "----------");
                System.out.println("Public methods: " + cntPublic);
                System.out.println("Private methods: " + cntPrivate);
                System.out.println("Protected methods: " + cntProtected);
                System.out.println("Static methods: " + cntStatic);
                System.out.println("Fields: " + fields.length);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        
        
    }
}
