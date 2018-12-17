package uk.co.olbois.facecraft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import uk.co.olbois.facecraft.controller.ConsoleOutputRepository;
import uk.co.olbois.facecraft.networking.ConsoleOutputPacketListener;
import uk.co.olbois.facecraft.controller.MessageRepository;
import uk.co.olbois.facecraft.model.Message;
import uk.co.olbois.facecraft.networking.NetworkManager;
import uk.co.olbois.facecraft.controller.EventRepository;
import uk.co.olbois.facecraft.controller.ServerRepository;
import uk.co.olbois.facecraft.controller.UserRepository;
import uk.co.olbois.facecraft.networking.OwnerPacketListener;

@SpringBootApplication
@EnableJpaRepositories
@Import(RepositoryRestMvcConfiguration.class)
@EnableAutoConfiguration

public class Application implements CommandLineRunner {

    private static Application instance;

    private static NetworkManager networkManager;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsoleOutputRepository consoleOutputRepository;

    private OwnerPacketListener ownerPacketListener;
    private ConsoleOutputPacketListener consoleOutputPacketListener;
  
    @Autowired
    private MessageRepository messageRepository;

    public static void main(String[] args)  {
        networkManager = new NetworkManager();
        networkManager.start();

        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        instance = this;

        // register packet listeners here
        ownerPacketListener = new OwnerPacketListener(networkManager);
        consoleOutputPacketListener = new ConsoleOutputPacketListener(networkManager);
    }

    public EventRepository getEventRepository() {
        return eventRepository;
    }

    public ServerRepository getServerRepository() {
        return serverRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public ConsoleOutputRepository getConsoleOutputRepository() { return consoleOutputRepository; }
    public MessageRepository getMessageRepository() {return messageRepository;}

    public static Application getInstance() {
        return instance;
    }
    public static NetworkManager getNetworkManager() {
        return networkManager;
    }

}

