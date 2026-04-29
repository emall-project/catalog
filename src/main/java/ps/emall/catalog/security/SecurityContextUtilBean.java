package ps.emall.catalog.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ps.emall.catalog.security.dto.StoreRef;
import ps.emall.catalog.security.userdetails.Gender;

import java.util.List;
import java.util.Optional;

@Component("auth")
@RequiredArgsConstructor
public class SecurityContextUtilBean {

    public boolean isAdmin() {
        return SecurityContextUtil.isAdmin();
    }

    public boolean isCustomer() {
        return SecurityContextUtil.isCustomer();
    }

    public boolean isShopOwner() {
        return SecurityContextUtil.isShopOwner();
    }

    public Long getCurrentUserId() {
        return SecurityContextUtil.getCurrentUserId();
    }

    public String getCurrentUsername() {
        return SecurityContextUtil.getCurrentUsername();
    }

    public Optional<Integer> getCurrentAge(){return SecurityContextUtil.getCurrentAge();}

    public Optional<Gender> getCurrentGender(){return SecurityContextUtil.getCurrentGender();}

    public List<StoreRef> getCurrentShopIds(){return SecurityContextUtil.getCurrentShopIds();}

    public Long getMallId(Long shopId){return SecurityContextUtil.getMallId(shopId);}
    /**
     * True if the current user is ADMIN, or is a SHOP_OWNER who owns the given shop.
     * Use this in @PreAuthorize for any shop-scoped write operation.
     */
    public boolean isAdminOrShopOwnerOf(Long shopId) {
        return SecurityContextUtil.isShopOwnerOf(shopId);
    }

    public boolean adminOnly() {
        return SecurityContextUtil.isAdmin();
    }
}