package ps.emall.catalog.client.accounts;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ps.emall.catalog.config.service.AccountsFeignConfig;

@FeignClient(
        name = "accounts-service",
        url = "${services.accounts.host}:${services.accounts.port}",
        configuration = AccountsFeignConfig.class
)
public interface AccountsClient {

    @GetMapping("/api/users/{userId}/info")
    AccountsResponse getUserById(@PathVariable("userId") Long userId);
}