package ps.emall.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ps.emall.catalog.config.FeignConfig;

import java.util.TimeZone;

@SpringBootApplication
@EnableFeignClients(defaultConfiguration = FeignConfig.class)
public class CatalogApplication {

    private static final String APPLICATION_TIME_ZONE = "Asia/Gaza";

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone(APPLICATION_TIME_ZONE));
        SpringApplication.run(CatalogApplication.class, args);
    }

}
