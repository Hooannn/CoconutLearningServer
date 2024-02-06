package com.ht.elearning.classwork;

import com.ht.elearning.classwork.dtos.CreateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.CreateClassworkDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkDto;
import com.ht.elearning.classwork.projections.StudentClassworkView;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/classwork")
@CrossOrigin
public class ClassworkController {
    private final ClassworkCategoryService classworkCategoryService;
    private final ClassworkService classworkService;

    @Operation(summary = "Find all todo classwork for user")
    @GetMapping("/todo")
    public ResponseEntity<Response<List<StudentClassworkView>>> findTodo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findTodo(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all done classwork for user")
    @GetMapping("/done")
    public ResponseEntity<Response<List<StudentClassworkView>>> findDone() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findDone(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all need review classwork for user")
    @GetMapping("/need-review")
    public ResponseEntity<Response<List<Classwork>>> findNeedReview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findNeedReview(authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all classwork for user in calendar view by date range")
    @GetMapping("/calendar")
    public ResponseEntity<Response<List<?>>> findCalendar(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") Date endDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findCalendar(startDate, endDate, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all classwork for user in calendar view by date range and by classroom")
    @GetMapping("/{classroomId}/calendar")
    public ResponseEntity<Response<List<?>>> findCalendarByClassroomId(
            @PathVariable String classroomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") Date endDate
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findCalendarByClassroomId(classroomId, startDate, endDate, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all classwork categories by classroom")
    @GetMapping("/{classroomId}/categories")
    public ResponseEntity<Response<List<ClassworkCategory>>> findCategoriesByClassroomId(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var categories = classworkCategoryService.findAllByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        categories
                )
        );
    }

    @Operation(summary = "Find all upcoming classwork for provider by classroom")
    @GetMapping("/{classroomId}/upcoming/provider")
    public ResponseEntity<Response<List<?>>> findUpcomingByClassroomIdForProvider(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findUpcomingByClassroomId(classroomId, authentication.getPrincipal().toString(), true);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all upcoming classwork for student by classroom")
    @GetMapping("/{classroomId}/upcoming/student")
    public ResponseEntity<Response<List<?>>> findUpcomingByClassroomIdForStudent(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findUpcomingByClassroomId(classroomId, authentication.getPrincipal().toString(), false);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Create new classwork category for classroom")
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
                        ResponseMessage.CREATED,
                        true,
                        classworkCategory
                )
        );
    }

    @Operation(summary = "Update classwork category for classroom by its id")
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
                        ResponseMessage.UPDATED,
                        true,
                        classworkCategory
                )
        );
    }

    @Operation(summary = "Delete classwork category for classroom by its id")
    @DeleteMapping("/{classroomId}/categories/{categoryId}")
    public ResponseEntity<Response<?>> deleteCategory(@PathVariable String categoryId, @PathVariable String classroomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var success = classworkCategoryService.deleteById(categoryId, classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.DELETED,
                        success,
                        null
                )
        );
    }

    @Operation(summary = "Find all classwork by classroom")
    @GetMapping("/{classroomId}")
    public ResponseEntity<Response<List<?>>> findByClassroomId(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findAllByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find all classwork with type is EXAM by classroom")
    @GetMapping("/{classroomId}/exam")
    public ResponseEntity<Response<List<?>>> findAllExamByClassroomId(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findAllExamByClassroomId(classroomId, authentication.getPrincipal().toString());
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Find classwork by its id")
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
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Create new classwork for classroom")
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
                        ResponseMessage.CREATED,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Update classwork for classroom by its id")
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
                        ResponseMessage.UPDATED,
                        true,
                        classwork
                )
        );
    }

    @Operation(summary = "Delete classwork for classroom by its id")
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
                        ResponseMessage.DELETED,
                        success,
                        null
                )
        );
    }
}
