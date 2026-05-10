package ps.emall.catalog.brand;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;
import ps.emall.catalog.product.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandServiceHelper {
    private final ProductRepository productRepository;
    private final MediaManagerClient mediaManagerClient;

    public void activation(Brand brand) {
        // TODO:  notify  vendor how have products attached to this brand
        productRepository.activateProductsByBrandId(brand.getId());
    }


    public void deactivation(Brand brand) {
        productRepository.deactivateProductsByBrandId(brand.getId());
        // TODO:  notify vendor how have products attached to this brand
    }


    public BrandDto injectImageUrl(BrandDto dto) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(dto.getImageId());
            dto.setImage(response.getData());
            return dto;

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw BrandExceptions.imageNotFound();
            }
            throw e;
        }
    }
}
