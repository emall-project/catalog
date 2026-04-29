package ps.emall.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ps.emall.catalog.config.FeignConfig;

@SpringBootApplication
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
public class CatalogApplication {

    public static void main(String[] args) {

        SpringApplication.run(CatalogApplication.class, args);
    }

}
