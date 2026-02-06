package ps.emall.catalog.attribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ps.emall.catalog.common.response.EMallsResponseEntity;
import ps.emall.catalog.common.validation.OnCreate;
import ps.emall.catalog.common.validation.OnUpdate;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping
    public EMallsResponseEntity<Page<AttributeDto>> getAll(
            AttributeSpec spec,
            Pageable pageable
    ) {
        return EMallsResponseEntity.ok(
                attributeService.getAll(spec, pageable)
        );
    }

    @GetMapping("/all")
    public EMallsResponseEntity<List<AttributeDto>> getAllList(AttributeSpec spec) {
        return EMallsResponseEntity.ok(
                attributeService.getAllList(spec)
        );
    }

    @GetMapping("/{id}")
    public EMallsResponseEntity<AttributeDto> getById(@PathVariable Long id) {
        log.info("Getting attribute by id {}", id);
        return EMallsResponseEntity.ok(
                attributeService.findById(id)
        );
    }

    @GetMapping("/slug/{slug}")
    public EMallsResponseEntity<AttributeDto> getBySlug(@PathVariable String slug) {
        return EMallsResponseEntity.ok(
                attributeService.findBySlug(slug)
        );
    }

    @PostMapping
    public EMallsResponseEntity<AttributeDto> create(
            @Validated(OnCreate.class) @RequestBody AttributeDto dto
    ) {
        return EMallsResponseEntity.created(
                attributeService.create(dto)
        );
    }

    @PutMapping
    public EMallsResponseEntity<AttributeDto> update(@Validated(OnUpdate.class) @RequestBody AttributeDto dto) {
        return EMallsResponseEntity.ok(
                attributeService.update(dto)
        );
    }

    @DeleteMapping("/{id}")
    public EMallsResponseEntity<Void> delete(@PathVariable Long id) {
        attributeService.delete(id);
        return EMallsResponseEntity.noContent(null);
    }
}
