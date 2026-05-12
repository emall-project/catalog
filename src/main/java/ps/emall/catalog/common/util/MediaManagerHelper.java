package ps.emall.catalog.common.util;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ps.emall.catalog.category.CategoryExceptions;
import ps.emall.catalog.client.media_manager.FileDto;
import ps.emall.catalog.client.media_manager.FileLightDto;
import ps.emall.catalog.client.media_manager.MediaManagerClient;
import ps.emall.catalog.client.media_manager.MediaResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaManagerHelper {
    private final MediaManagerClient mediaManagerClient;

    public Map<UUID, FileDto> getMedia(List<UUID> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }

        try {
            // TODO REPLACE WITH endpoint that's return FileLightDto
            MediaResponse<List<FileDto>> response = mediaManagerClient.getByIds(imageIds);

            // validate response not empty
            if (response == null || response.getData() == null) {
                throw CategoryExceptions.imageNotFound();
            }
            //inject image File
            Map<UUID, FileDto> fileDtoMap = new HashMap<>();
            for (FileDto fileDto : response.getData()) {
                fileDtoMap.put(fileDto.getId(), fileDto);
            }
            return fileDtoMap;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            log.error("Could not fetch media File from MediaManager status={}, message={}",
                    e.status(), e.getMessage()
            );
            throw e;
        }
    }
    public boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }


    public FileDto getAndValidatedImage(UUID imageId) {
        try {
            MediaResponse<FileDto> response = mediaManagerClient.getById(imageId);
            // validate response not empty
            if (response == null || response.getData() == null) {
                throw CategoryExceptions.imageCouldNotBeValidated();
            }

            FileDto fileDto = response.getData();

            // validate file type
            if (!isImage(fileDto.getMimeType())) {
                throw CategoryExceptions.invalidFileType();
            }

            return response.getData();

        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            throw CategoryExceptions.imageCouldNotBeValidated();
        }
    }


    public Map<UUID, FileLightDto> getLightMedia(List<UUID> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return null;
        }

        try {
            // TODO REPLACE WITH endpoint that's return FileLightDto
            MediaResponse<List<FileDto>> response = mediaManagerClient.getByIds(imageIds);

            // validate response not empty
            if (response == null || response.getData() == null) {
                throw CategoryExceptions.imageNotFound();
            }
            //inject image File
            Map<UUID, FileLightDto> fileDtoMap = new HashMap<>();
            for (FileDto fileDto : response.getData()) {
                FileLightDto fileLightDto = new FileLightDto();
                fileLightDto.setId(fileDto.getId());
                fileLightDto.setSmallFileUrl(fileDto.getSmallFileUrl());
                fileDtoMap.put(fileDto.getId(), fileLightDto);
            }
            return fileDtoMap;
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw CategoryExceptions.imageNotFound();
            }
            log.error("Could not fetch light media File from MediaManager status={}, message={}",
                    e.status(), e.getMessage()
            );
            throw e;
        }
    }



}
