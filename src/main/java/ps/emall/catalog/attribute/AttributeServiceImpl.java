package ps.emall.catalog.attribute;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionDto;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionMapper;
import ps.emall.catalog.attribute.attribute_options.AttributeOptionsExceptions;
import ps.emall.catalog.product.product_variant.ProductVariantRepository;
import ps.emall.catalog.product.product_variant.variant_attribute.VariantAttributeRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final VariantAttributeRepository variantAttributeRepository;
    private final AttributeSpecificationBuilder attributeSpecificationBuilder;

    @Override
    @Transactional(readOnly = true)
    public Page<AttributeDto> getAll(AttributeFilter filter, Pageable pageable) {
        Specification<Attribute> spec = attributeSpecificationBuilder.build(filter);

        return attributeRepository.findAll(spec, pageable)
                .map(AttributeMapper::toDto)
                .map(this::withProductsCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeDto> getAllList(AttributeFilter filter) {
        Specification<Attribute> spec = attributeSpecificationBuilder.build(filter);

        List<Attribute> attributes = (spec == null)
                ? attributeRepository.findAll()
                : attributeRepository.findAll(spec);

        return attributes.stream()
                .map(AttributeMapper::toDto)
                .map(this::withProductsCount)
                .toList();
    }

    @Override
    public AttributeDto create(AttributeDto dto) {
        if (attributeRepository.existsBySlug(dto.getSlug())) {
            throw AttributeExceptions.slugExists();
        }
        validateOptionList(dto.getOptions());
        Attribute saved = attributeRepository.save(AttributeMapper.toEntity(dto));
        return withProductsCount(AttributeMapper.toDto(saved));
    }

    @Override
    public AttributeDto update(AttributeDto dto) {
        Attribute existing = attributeRepository.findById(dto.getId())
                .orElseThrow(AttributeExceptions::attributeNotFound);

        if (!existing.getSlug().equals(dto.getSlug())
                && attributeRepository.existsBySlug(dto.getSlug())) {
            throw AttributeExceptions.slugExists();
        }

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setAttributeType(dto.getAttributeType());
        existing.setIsActive(dto.getIsActive());

        existing.getOptions().clear();
        dto.getOptions().stream()
                .map(AttributeOptionMapper::toEntity)
                .toList().forEach(existing::addOption);


        Attribute saved = attributeRepository.save(existing);
        return withProductsCount(AttributeMapper.toDto(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDto findById(Long id) {
        return attributeRepository.findById(id)
                .map(AttributeMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(AttributeExceptions::attributeNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDto findActiveById(Long id) {
        return attributeRepository.findByIdAndIsActiveTrue(id)
                .map(AttributeMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(AttributeExceptions::attributeNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDto findBySlug(String slug) {
        return attributeRepository.findBySlug(slug)
                .map(AttributeMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(AttributeExceptions::attributeNotFound);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDto findActiveBySlug(String slug) {
        return attributeRepository.findBySlugAndIsActiveTrue(slug)
                .map(AttributeMapper::toDto)
                .map(this::withProductsCount)
                .orElseThrow(AttributeExceptions::attributeNotFound);
    }

    @Override
    public void delete(Long id) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(AttributeExceptions::attributeNotFound);

        if (variantAttributeRepository.existsByAttribute_Id(attribute.getId())) {
            throw AttributeExceptions.attributeHasProducts();
        }
        attributeRepository.delete(attribute);
    }

    private boolean validateOptionList(List<AttributeOptionDto> options) {
        HashSet<String> value = new HashSet<>();
        HashSet<Integer> orders = new HashSet<>();
        for (AttributeOptionDto option : options) {
            value.add(option.getValue().trim().toLowerCase());
            orders.add(option.getSortOrder());
        }
        if (options.size() != value.size()) throw AttributeOptionsExceptions.duplicationInValue();
        if (options.size() != orders.size()) throw AttributeOptionsExceptions.duplicationInOrderSort();
        return true;
    }

    private AttributeDto withProductsCount(AttributeDto dto) {
        dto.setProductsCount(variantAttributeRepository.countDistinctProductsByAttributeId(dto.getId()));
        return dto;
    }
}
