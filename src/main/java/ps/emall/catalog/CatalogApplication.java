package ps.emall.catalog;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CatalogApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("OPENAI_MODERATION_API_KEY", dotenv.get("OPENAI_MODERATION_API_KEY"));

        SpringApplication.run(CatalogApplication.class, args);
    }

}
