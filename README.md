# Facecraft
Facecraft is a Minecraft Social Media with server management features. You can invite people to servers, interact with the server chat, moderate and more, all from your phone.

## Developer Instructions
Some instructions to get you started with our development environment. Many things have been made simpler using gradle.

### Techstack
We use Gradle for our build systems. JUnit for unit tests. All code is written in Kotlin and Java.

### Starting the Spring.io Server
Simply run this gradle task
```
./gradlew facecraft-springio:run
```
This will start the spring io server. To stop it, you can simply CTRL-C.

### Building the Plugin and Running the Minecraft Server
1. From the terminal, run
```
./gradlew facecraft-plugin:buildPlugin
```
This will build the plugin and save it in the `minecraft-server/plugins` folder as a jar.

2. Run the minecraft server
```
./gradlew runMinecraftServer
```
and to stop it
```
stop
```
When you first run the server, it will take longer since it has to generate a map and create startup files. Running `stop` will properly stop the server and save the map.

### Running the App
The app runs as any other Android Studio project app. When you do your first gradle sync, it will create a run configuration as usual. You can use this to run the app.