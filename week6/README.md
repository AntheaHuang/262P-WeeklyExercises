To use different plugins, go to config.properties and choose.

After navigating to /week6:

```
cd week6
```

- Navigate to /framework, compile and pack the interfaces and Twenty (Main)

  - ```
    cd framework
    ```

  - ```
    javac *.java
    ```

  - ```
    jar cfm framework.jar manifest.mf *.class
    ```

- Navigate to /app, compile and pack the src of different plugins

  - ```
    cd ../app
    ```

  - ```
    javac -cp ../framework/framework.jar *.java
    ```
  - ```
    jar cf Words1.jar Words1.class
    ```

  - ```
    jar cf Words2.jar Words2.class
    ```

  - ```
    jar cf Frequencies1.jar Frequencies1.class
    ```

  - ```
    jar cf Frequencies2.jar Frequencies2.class
    ```

- Navigate to /deploy, and execute the program

  - ```
    cd ../deploy
    ```
  - ```
    cp ../framework/*.jar ../app/*.jar .
    ```
  - ```
    java -jar framework.jar ../../pride-and-prejudice.txt
    ```
