package ps.emall.catalog.attribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;
import ps.emall.catalog.security.SecurityContextUtilBean;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;
    private final SecurityContextUtilBean auth;

    @GetMapping
    public EMallsResponseEntity<Page<AttributeDto>> getAll(@ModelAttribute AttributeFilter filter, Pageable pageable) {
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        return EMallsResponseEntity.ok(
                attributeService.getAll(filter, pageable)
        );
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<AttributeDto>> getAllList(@ModelAttribute AttributeFilter filter) {
        if (!auth.isAdmin()) {
            filter.setIsActive(true);
        }
        return EMallsResponseEntity.ok(
                attributeService.getAllList(filter)
        );
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<AttributeDto> getById(@PathVariable Long id) {
        log.info("Getting attribute by id {}", id);
        boolean isAdmin = auth.isAdmin();
        return EMallsResponseEntity.ok(
                isAdmin ? attributeService.findById(id) : attributeService.findActiveById(id)
        );
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<AttributeDto> getBySlug(@PathVariable String slug) {
        boolean isAdmin = auth.isAdmin();

        return EMallsResponseEntity.ok(
                isAdmin ? attributeService.findBySlug(slug) : attributeService.findActiveBySlug(slug)
        );
    }

    @PostMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<AttributeDto> create(
            @Validated(OnCreate.class) @RequestBody AttributeDto dto
    ) {
        return EMallsResponseEntity.created(
                attributeService.create(dto)
        );
    }

    @PutMapping
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<AttributeDto> update(@Validated(OnUpdate.class) @RequestBody AttributeDto dto) {
        return EMallsResponseEntity.ok(
                attributeService.update(dto)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@auth.isAdmin()")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        attributeService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
