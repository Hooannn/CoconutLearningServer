package com.ht.elearning.classwork;

import com.ht.elearning.classwork.dtos.CreateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.CreateClassworkDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkDto;
import com.ht.elearning.classwork.projections.StudentClassworkView;
import com.ht.elearning.config.Response;
import com.ht.elearning.constants.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/classwork")
@CrossOrigin
public class ClassworkController {
    private final ClassworkCategoryService classworkCategoryService;
    private final ClassworkService classworkService;

    @GetMapping("/todo")
    public ResponseEntity<Response<List<?>>> findTodo() {
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

    @GetMapping("/done")
    public ResponseEntity<Response<List<?>>> findDone() {
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

    @GetMapping("/need-review")
    public ResponseEntity<Response<List<?>>> findNeedReview() {
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

    @GetMapping("/calendar")
    public ResponseEntity<Response<List<?>>> findCalendar(
            @RequestParam Date startDate,
            @RequestParam Date endDate
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

    @GetMapping("/{classroomId}/upcoming/provider")
    public ResponseEntity<Response<List<Classwork>>> findUpcomingClassworkByClassroomIdForProvider(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findUpcomingClassworkByClassroomId(classroomId, authentication.getPrincipal().toString(), true);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
                )
        );
    }

    @GetMapping("/{classroomId}/upcoming/student")
    public ResponseEntity<Response<List<Classwork>>> findUpcomingClassworkByClassroomIdForStudent(
            @PathVariable String classroomId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var classwork = classworkService.findUpcomingClassworkByClassroomId(classroomId, authentication.getPrincipal().toString(), false);
        return ResponseEntity.ok(
                new Response<>(
                        HttpStatus.OK.value(),
                        ResponseMessage.OK,
                        true,
                        classwork
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
                        ResponseMessage.CREATED,
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
                        ResponseMessage.UPDATED,
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
                        ResponseMessage.DELETED,
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
                        ResponseMessage.OK,
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
                        ResponseMessage.OK,
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
                        ResponseMessage.CREATED,
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
                        ResponseMessage.UPDATED,
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
                        ResponseMessage.DELETED,
                        success,
                        null
                )
        );
    }
}
