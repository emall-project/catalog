package ps.emall.catalog.client.accounts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Boolean isActive;
    private String role;
}