package ps.emall.catalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class health {
    @GetMapping("/health")
    public String index() {
        return "Catalog is up!";
    }
}
