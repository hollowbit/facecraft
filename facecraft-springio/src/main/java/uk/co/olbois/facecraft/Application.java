package uk.co.olbois.facecraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import uk.co.olbois.facecraft.networking.NetworkManager;

@SpringBootApplication
@EnableJpaRepositories
@Import(RepositoryRestMvcConfiguration.class)
@EnableAutoConfiguration
public class Application {

    private static NetworkManager networkManager;

    public static void main(String[] args)  {
        networkManager = new NetworkManager();
        networkManager.start();

        SpringApplication.run(Application.class, args);
    }

    public static NetworkManager getNetworkManager() {
        return networkManager;
    }

}

