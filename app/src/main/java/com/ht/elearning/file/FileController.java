package com.ht.elearning.file;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.file.dtos.RemoveManyFilesDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/files")
@CrossOrigin
public class FileController {
    private final FileService fileService;

    @Operation(summary = "Find all files in the folder of the current user")
    @GetMapping("folder")
    public ResponseEntity<Response<List<File>>> findMyFiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var files = fileService.findMyFiles(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        files
                )
        );
    }

    @Operation(summary = "Upload files to the folder of the current user")
    @PostMapping("upload")
    public ResponseEntity<Response<List<File>>> upload(@RequestPart("files") List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var uploadedFiles = fileService.upload(files, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        ResponseMessage.UPLOADED,
                        true,
                        uploadedFiles
                )
        );
    }

    @Operation(summary = "Update a file by its id")
    @PutMapping("{fileId}")
    public ResponseEntity<Response<File>> update(@RequestPart("files") List<MultipartFile> files, @PathVariable String fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var file = fileService.update(fileId, files.get(0), authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        true,
                        file
                )
        );
    }

    @Operation(summary = "Delete a file by its id")
    @DeleteMapping("{fileId}")
    public ResponseEntity<Response<List<File>>> remove(@PathVariable String fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = fileService.remove(fileId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.REMOVED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Delete many files by their ids")
    @PostMapping("/remove/many")
    public ResponseEntity<Response<?>> removeMany(@Valid @RequestBody RemoveManyFilesDto removeManyFilesDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = fileService.removeMany(removeManyFilesDto, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.REMOVED,
                        success,
                        null
                )
        );
    }
}
