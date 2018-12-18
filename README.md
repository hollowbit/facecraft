# Facecraft
Facecraft is a Minecraft Social Media with server management features. You can invite people to servers, interact with the server chat, moderate and more, all from your phone.

## Developer Instructions
Some instructions to get you started with our development environment. Many things have been made simpler using gradle.

### Android Studio
Start by importing the repository into Android Studio as a Gradle project.

### Techstack
We use Gradle for our build systems. JUnit for unit tests. All code is written in Kotlin and Java.

### Starting the Spring.io Server
1. Load the database
```
mysql -h 159.203.29.241 -u root -p facecraft < scripts/facecraft.sql
```
NOTE: We have a remote database server running for the assignment (that's why we have -h above). Spring io is configured to connect to it by default.

2. Then simply run this gradle task
```
./gradlew bootRun
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
cd minecraft-server
./run.sh
```
This will start a command line application to manage the server.
To stop it gracefully, you can run:
```
stop
```
When you first run the server, it will take longer since it has to generate a map and create startup files. Running `stop` will properly stop the server and save the map.

### Registering and Connecting a Minecraft Server
Once your Minecraft server and Spring.io server is running, you will want to register the Minecraft server to use it in our app. You can do this through the Minecraft server terminal:
```
facecraft register <address (ex: mc.hypixel.net)> <new-password> <Name...>
facecraft connect <address> <password>
```
Once this is done, to be able to manage the server from the app, you need to assign users as owners. Once you have registered a user in the App, you can make them an owner like this:
```
facecraft addowner <app-username>
```
and to remove them
```
facecraft removeowner <app-username>
```

### Running the App
The app runs as any other Android Studio project app. When you do your first gradle sync, it will create a run configuration as usual. You can use this to run the app.