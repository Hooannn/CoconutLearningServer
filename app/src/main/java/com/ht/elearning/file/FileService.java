package com.ht.elearning.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.file.dtos.RemoveManyFilesDto;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserService;
import com.ht.elearning.utils.MultipartFileConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    private final WebClient fileMasterApiClient;
    private final WebClient fileVolumeApiClient;
    private final FileRepository fileRepository;
    private final UserService userService;

    public List<File> upload(List<MultipartFile> files, String createdBy) {
        var creator = userService.findById(createdBy);
        return files.stream().map(file -> upload(file, creator)).toList();
    }

    public boolean remove(String fileId, String createdBy) {
        var file = fileRepository.findByIdAndCreatorId(fileId, createdBy).orElseThrow(() -> new HttpException("File not found", HttpStatus.BAD_REQUEST));
        String endpoint = "/" + fileId;
        var res = fileVolumeApiClient.delete()
                .uri(endpoint)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            throw new HttpException("Something occurred while removing. Please try again", HttpStatus.BAD_REQUEST);
                        })
                .bodyToMono(RemoveFileResponse.class)
                .block();

        if (res == null)
            throw new HttpException("Something occurred while removing. Please try again", HttpStatus.BAD_REQUEST);

        fileRepository.delete(file);
        return true;
    }


    public File update(String fileId, MultipartFile multipartFile, String createdBy) {
        var file = fileRepository.findByIdAndCreatorId(fileId, createdBy).orElseThrow(() -> new HttpException("File not found", HttpStatus.BAD_REQUEST));
        String endpoint = "/" + fileId;
        String contentType = file.getContentType();
        var uploadResponse = fileVolumeApiClient.post()
                .uri(endpoint)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", MultipartFileConverter.convert(multipartFile)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            throw new HttpException("Something occurred while uploading. Please try again", HttpStatus.BAD_REQUEST);
                        })
                .bodyToMono(UploadFileResponse.class)
                .block();

        if (uploadResponse == null)
            throw new HttpException("Something occurred while uploading. Please try again", HttpStatus.BAD_REQUEST);

        file.setETag(uploadResponse.getETag());
        file.setSize(uploadResponse.getSize());
        file.setName(uploadResponse.getName());
        file.setContentType(contentType);

        return fileRepository.save(file);
    }

    private File upload(MultipartFile file, User creator) {
        var readDirAssignResponse = fileMasterApiClient
                .get()
                .uri("/dir/assign")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            throw new HttpException("Something occurred while uploading. Please try again", HttpStatus.BAD_REQUEST);
                        })
                .bodyToMono(ReadDirAssignResponse.class)
                .block();

        if (readDirAssignResponse == null)
            throw new HttpException("Something occurred while uploading. Please try again", HttpStatus.BAD_REQUEST);

        var fileId = readDirAssignResponse.getFid();
        String endpoint = "/" + fileId;
        String contentType = file.getContentType();
        var uploadResponse = fileVolumeApiClient.post()
                .uri(endpoint)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", MultipartFileConverter.convert(file)))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> {
                            throw new HttpException("Something occurred while uploading. Please try again", HttpStatus.BAD_REQUEST);
                        })
                .bodyToMono(UploadFileResponse.class)
                .block();

        if (uploadResponse == null)
            throw new HttpException("Something occurred while uploading. Please try again", HttpStatus.BAD_REQUEST);

        File fileToSave = File.builder()
                .id(fileId)
                .creator(creator)
                .eTag(uploadResponse.getETag())
                .name(uploadResponse.getName())
                .size(uploadResponse.getSize())
                .contentType(contentType)
                .build();

        return fileRepository.save(fileToSave);
    }


    public List<File> findMyFiles(String userId) {
        return fileRepository.findAllByCreatorId(userId);
    }


    public boolean removeMany(RemoveManyFilesDto removeManyFilesDto, String ownerId) {
        var files = fileRepository.findAllByIdInAndCreatorId(removeManyFilesDto.getFileIds(), ownerId);
        for (File file : files) {
            String endpoint = "/" + file.getId();
            var res = fileVolumeApiClient.delete()
                    .uri(endpoint)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> {
                                throw new HttpException("Something occurred while removing. Please try again", HttpStatus.BAD_REQUEST);
                            })
                    .bodyToMono(RemoveFileResponse.class)
                    .block();

            if (res == null)
                throw new HttpException("Something occurred while removing. Please try again", HttpStatus.BAD_REQUEST);

            fileRepository.delete(file);
        }

        return true;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ReadDirAssignResponse {
    private String fid;
    private String url;
    private String publicUrl;
    private int count;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UploadFileResponse {
    private String name;
    private long size;
    @JsonProperty("eTag")
    private String eTag;
}


@Data
@NoArgsConstructor
@AllArgsConstructor
class RemoveFileResponse {
    private long size;
}