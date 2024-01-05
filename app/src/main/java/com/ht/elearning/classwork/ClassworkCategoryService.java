package com.ht.elearning.classwork;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.classwork.dtos.CreateClassworkCategoryDto;
import com.ht.elearning.classwork.dtos.UpdateClassworkCategoryDto;
import com.ht.elearning.config.HttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassworkCategoryService {
    private final ClassworkCategoryRepository classworkCategoryRepository;
    private final ClassroomService classroomService;


    public ClassworkCategory create(CreateClassworkCategoryDto createClassworkCategoryDto, String classroomId, String userId) {
        var classroom = classroomService.findById(classroomId);
        var isMember = classroomService.isMember(classroom, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        var category = ClassworkCategory.builder()
                .name(createClassworkCategoryDto.getName())
                .classroom(classroom)
                .build();

        return classworkCategoryRepository.save(category);
    }


    public ClassworkCategory update(UpdateClassworkCategoryDto updateClassworkCategoryDto, String categoryId, String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        var category = classworkCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new HttpException("Category not found", HttpStatus.BAD_REQUEST));

        Optional.ofNullable(updateClassworkCategoryDto.getName()).ifPresent(category::setName);

        return classworkCategoryRepository.save(category);
    }


    public boolean deleteById(String categoryId, String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);
        var category = classworkCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new HttpException("Category not found", HttpStatus.BAD_REQUEST));

        classworkCategoryRepository.delete(category);
        return true;
    }


    public List<ClassworkCategory> findAllByClassroomId(String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException("You are not member of this class", HttpStatus.FORBIDDEN);

        return classworkCategoryRepository.findAllByClassroomId(classroomId);
    }


    public ClassworkCategory findById(String categoryId) {
        return classworkCategoryRepository.findById(categoryId).orElseThrow(() -> new HttpException("Category not found", HttpStatus.BAD_REQUEST));
    }


    public ClassworkCategory findByIdAndClassroomId(String categoryId, String classroomId) {
        return classworkCategoryRepository.findByIdAndClassroomId(categoryId, classroomId).orElseThrow(() -> new HttpException("Category not found", HttpStatus.BAD_REQUEST));
    }
}