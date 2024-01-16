package com.ht.elearning.classwork;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.dtos.CreateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.CreateClassworkDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkDto;
import com.ht.elearning.config.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/classwork")
@CrossOrigin
public class ClassworkController {
    private final ClassworkCategoryService classworkCategoryService;
    private final ClassworkService classworkService;
    private final ClassroomService classroomService;


    @GetMapping("/{classroomId}/categories")
    public ResponseEntity<Response<List<ClassworkCategory>>> findCategoriesByClassroomId(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var categories = classworkCategoryService.findAllByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "ok",
                        true,
                        categories
                )
        );
    }


    @PostMapping("/{classroomId}/categories")
    public ResponseEntity<Response<ClassworkCategory>> createCategory(
            @Valid @RequestBody CreateClassworkCategoryDto createClassworkCategoryDto,
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classworkCategory = classworkCategoryService.create(createClassworkCategoryDto, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created successfully",
                        true,
                        classworkCategory
                )
        );
    }


    @PutMapping("/{classroomId}/categories/{categoryId}")
    public ResponseEntity<Response<ClassworkCategory>> updateCategory(
            @PathVariable String categoryId,
            @PathVariable String classroomId,
            @Valid @RequestBody UpdateClassworkCategoryDto updateClassworkCategoryDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classworkCategory = classworkCategoryService.update(updateClassworkCategoryDto, categoryId, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Updated successfully",
                        true,
                        classworkCategory
                )
        );
    }


    @DeleteMapping("/{classroomId}/categories/{categoryId}")
    public ResponseEntity<Response<?>> deleteCategory(@PathVariable String categoryId, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classworkCategoryService.deleteById(categoryId, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Deleted",
                        success,
                        null
                )
        );
    }


    @GetMapping("/{classroomId}")
    public ResponseEntity<Response<List<?>>> findByClassroomId(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findAllByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "ok",
                        true,
                        classwork
                )
        );
    }


    @GetMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<?>> findByClassroomIdAndClassworkId(
            @PathVariable String classroomId,
            @PathVariable String classworkId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findByClassroomIdAndClassworkId(classroomId, classworkId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "ok",
                        true,
                        classwork
                )
        );
    }


    @PostMapping("/{classroomId}")
    public ResponseEntity<Response<Classwork>> create(
            @Valid @RequestBody CreateClassworkDto createClassworkDto,
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.create(createClassworkDto, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.created(null).body(
                new Response<>(
                        HttpStatus.CREATED.value(),
                        "Created successfully",
                        true,
                        classwork
                )
        );
    }


    @PutMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<Classwork>> update(
            @Valid @RequestBody UpdateClassworkDto updateClassworkDto,
            @PathVariable String classroomId,
            @PathVariable String classworkId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.update(updateClassworkDto, classworkId, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Updated successfully",
                        true,
                        classwork
                )
        );
    }


    @DeleteMapping("/{classroomId}/{classworkId}")
    public ResponseEntity<Response<Classwork>> delete(
            @PathVariable String classroomId,
            @PathVariable String classworkId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classworkService.deleteById(classworkId, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        "Deleted",
                        success,
                        null
                )
        );
    }
}
