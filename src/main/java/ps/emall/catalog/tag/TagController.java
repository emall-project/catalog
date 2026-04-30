package ps.emall.catalog.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.page.PaginatedResponse;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public EMallsResponseEntity<PaginatedResponse<TagDto>> getAll(TagSpec spec, Pageable pageable) {
        return EMallsResponseEntity.ok(tagService.getAll(spec, pageable));
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<TagDto>> getTags(TagSpec spec) {
        return EMallsResponseEntity.ok(tagService.getAllTagsList(spec));
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<TagDto> getById(@PathVariable Long id) {
        return EMallsResponseEntity.ok(tagService.findById(id));
    }

    @GetMapping("/name/{name}")
    public EMallsResponseEntity<TagDto> getByName(@PathVariable String name) {
        return EMallsResponseEntity.ok(tagService.findByName(name));
    }

    @PostMapping
    @PreAuthorize("@auth.isAdminOrShopOwner()")
    public EMallsResponseEntity<TagDto> create(
            @Validated(OnCreate.class) @RequestBody TagDto dto) {
        return EMallsResponseEntity.created(tagService.create(dto));
    }

    @PutMapping
    @PreAuthorize("@auth.isAdminOrShopOwner()")
    public EMallsResponseEntity<TagDto> update(@Validated(OnUpdate.class) @RequestBody TagDto dto) {
        return EMallsResponseEntity.ok(tagService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@auth.isAdminOrShopOwner()")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
