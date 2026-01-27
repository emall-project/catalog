package ps.emall.catalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class health {
    @GetMapping("/redirect-to-login")
    public String redirectToLogin() {
        return "redirect:/login";
    }
}
