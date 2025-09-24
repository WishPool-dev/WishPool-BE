package WishPool.Be;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@OpenAPIDefinition(servers = {@Server(url = "https://api.wishpool.store", description = "Default Server URL")})
public class BeApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeApplication.class, args);
	}
}
