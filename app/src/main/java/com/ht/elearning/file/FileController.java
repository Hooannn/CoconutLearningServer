package com.ht.elearning.file;

import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import com.ht.elearning.file.dtos.RemoveManyFilesDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/files")
@CrossOrigin
public class FileController {
    private final FileService fileService;


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
