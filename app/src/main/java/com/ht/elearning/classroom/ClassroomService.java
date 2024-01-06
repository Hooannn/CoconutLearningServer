package com.ht.elearning.classroom;

import com.ht.elearning.classroom.dtos.CreateClassroomDto;
import com.ht.elearning.classroom.dtos.InviteDto;
import com.ht.elearning.classroom.dtos.RemoveInviteDto;
import com.ht.elearning.classroom.dtos.UpdateClassroomDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationRepository;
import com.ht.elearning.invitation.InvitationType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.user.UserService;
import com.ht.elearning.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserService userService;
    private final InvitationRepository invitationRepository;
    private final NotificationProcessor notificationProcessor;

    public Classroom create(CreateClassroomDto createClassroomDto, String ownerId) {
        Random random = new Random();
        int randomLength = random.nextInt(7 + 1 - 5) + 5;
        String inviteCode = Helper.generateRandomSecret(randomLength);
        if (classroomRepository.existsByInviteCode(inviteCode))
            throw new HttpException("Something went wrong. Please try again", HttpStatus.CONFLICT);

        var owner = userService.findById(ownerId);
        var classroom = Classroom
                .builder()
                .name(createClassroomDto.getName())
                .room(createClassroomDto.getRoom())
                .coverImageUrl(createClassroomDto.getCoverImageUrl() == null ? null : "/Honors_thumb.jpg")
                .description(createClassroomDto.getDescription())
                .course(createClassroomDto.getCourse())
                .inviteCode(inviteCode)
                .owner(owner)
                .build();

        return classroomRepository.save(classroom);
    }

    public Classroom find(String classroomId, String userId) {
        var classroom = classroomRepository.findById(classroomId).orElseThrow(
                () -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST)
        );
        if (
                classroom.getUsers().stream().noneMatch(u -> u.getId().equals(userId))
                        &&
                        !classroom.getOwner().getId().equals(userId)
                        &&
                        classroom.getProviders().stream().noneMatch(u -> u.getId().equals(userId))
        )
            throw new HttpException("Forbidden request", HttpStatus.FORBIDDEN);

        return classroom;
    }

    public Classroom findById(String classroomId) {
        return classroomRepository.findById(classroomId).orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));
    }

    public boolean invite(InviteDto inviteDto, String ownerId) {
        var classroom = classroomRepository.findByIdAndOwnerId(inviteDto.getClassId(), ownerId).orElseThrow(
                () -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST)
        );

        if (classroom.getOwner().getEmail().equals(inviteDto.getEmail()))
            throw new HttpException("Illegal request", HttpStatus.NOT_ACCEPTABLE);

        if (classroom.getUsers().stream().anyMatch(u -> u.getEmail().equals(inviteDto.getEmail()))
                || classroom.getProviders().stream().anyMatch(u -> u.getEmail().equals(inviteDto.getEmail())))
            throw new HttpException("User already joined", HttpStatus.BAD_REQUEST);

        if (invitationRepository.existsByEmailAndClassroomId(inviteDto.getEmail(), inviteDto.getClassId()))
            throw new HttpException("Invitation already exists", HttpStatus.BAD_REQUEST);

        var invitation = Invitation.builder()
                .email(inviteDto.getEmail())
                .classroom(classroom)
                .type(inviteDto.getType() == null ? InvitationType.USER : inviteDto.getType())
                .build();

        var savedInvitation = invitationRepository.save(invitation);

        notificationProcessor.processClassroomInvitation(savedInvitation, classroom);

        return true;
    }


    public boolean join(String inviteCode, String userId) {
        var classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));

        if (classroom.getOwner().getId().equals(userId))
            throw new HttpException("Illegal request", HttpStatus.NOT_ACCEPTABLE);

        if (classroom.getUsers().stream().anyMatch(u -> u.getId().equals(userId))
                || classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId)))
            throw new HttpException("Already joined", HttpStatus.BAD_REQUEST);

        var user = userService.findById(userId);

        classroom.getUsers().add(user);

        invitationRepository.deleteByEmailAndClassroomId(user.getEmail(), classroom.getId());
        var saved = classroomRepository.save(classroom);
        notificationProcessor.processClassroomJoining(saved, user);

        return true;

    }


    public boolean accept(String inviteCode, String userId) {
        var classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));

        var user = userService.findById(userId);

        var invitation = invitationRepository.findByEmailAndClassroomId(user.getEmail(), classroom.getId()).orElseThrow(
                () -> new HttpException("Invitation not found", HttpStatus.BAD_REQUEST));

        if (classroom.getOwner().getId().equals(userId))
            throw new HttpException("Illegal request", HttpStatus.NOT_ACCEPTABLE);

        if (classroom.getUsers().stream().anyMatch(u -> u.getId().equals(userId))
                || classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId)))
            throw new HttpException("Already joined", HttpStatus.BAD_REQUEST);

        if (invitation.getType() == InvitationType.PROVIDER) {
            classroom.getProviders().add(user);
        } else {
            classroom.getUsers().add(user);
        }

        invitationRepository.deleteByEmailAndClassroomId(user.getEmail(), classroom.getId());
        var saved = classroomRepository.save(classroom);
        notificationProcessor.processClassroomJoining(saved, user);

        return true;
    }


    public boolean refuse(String inviteCode, String userId) {
        var classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));

        var user = userService.findById(userId);

        var exists = invitationRepository.existsByEmailAndClassroomId(user.getEmail(), classroom.getId());

        if (!exists) throw new HttpException("Bad request", HttpStatus.BAD_REQUEST);

        invitationRepository.deleteByEmailAndClassroomId(user.getEmail(), classroom.getId());

        return true;
    }


    public boolean leave(String classroomId, String userId) {
        var classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));

        if (classroom.getOwner().getId().equals(userId))
            throw new HttpException("Illegal request", HttpStatus.NOT_ACCEPTABLE);

        if (classroom.getUsers().stream().noneMatch(u -> u.getId().equals(userId))
                && classroom.getProviders().stream().noneMatch(u -> u.getId().equals(userId)))
            throw new HttpException("You are not a member of this classroom", HttpStatus.BAD_REQUEST);

        var user = userService.findById(userId);

        classroom.getUsers().remove(user);
        classroom.getProviders().remove(user);

        var saved = classroomRepository.save(classroom);
        notificationProcessor.processClassroomLeaving(saved, user);

        return true;
    }


    public Classroom resetClassCode(String classroomId, String ownerId) {
        var classroom = classroomRepository.findByIdAndOwnerId(classroomId, ownerId).orElseThrow(
                () -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST)
        );

        Random random = new Random();
        int randomLength = random.nextInt(7 + 1 - 5) + 5;
        String newClassCode = Helper.generateRandomSecret(randomLength);
        if (classroomRepository.existsByInviteCode(newClassCode))
            throw new HttpException("Something went wrong. Please try again", HttpStatus.CONFLICT);

        classroom.setInviteCode(newClassCode);

        return classroomRepository.save(classroom);
    }


    public boolean isMember(String classId, String userId) {
        var classroom = classroomRepository.findById(classId).orElse(null);
        if (classroom == null) return false;

        return classroom.getUsers().stream().anyMatch(u -> u.getId().equals(userId))
                || classroom.getOwner().getId().equals(userId)
                || classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId));
    }


    public boolean isMember(Classroom classroom, String userId) {
        return classroom.getUsers().stream().anyMatch(u -> u.getId().equals(userId))
                || classroom.getOwner().getId().equals(userId)
                || classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId));
    }


    public List<Classroom> findTeachingClassrooms(String userId) {
        var teachingClassrooms = classroomRepository.findAllTeachingClassrooms(userId);
        var createdClassrooms = classroomRepository.findAllByOwnerId(userId);
        return Stream.concat(teachingClassrooms.stream(), createdClassrooms.stream()).toList();
    }


    public List<Classroom> findRegisteredClassrooms(String userId) {
        return classroomRepository.findAllRegisteredClassrooms(userId);
    }


    public boolean deleteById(String classroomId, String ownerId) {
        var classroom = classroomRepository.findByIdAndOwnerId(classroomId, ownerId).orElseThrow(
                () -> new HttpException("CLassroom not found", HttpStatus.BAD_REQUEST)
        );
        classroomRepository.delete(classroom);
        return true;
    }


    public Classroom update(UpdateClassroomDto updateClassroomDto, String classroomId, String userId) {
        var classroom = classroomRepository.findByIdAndOwnerId(classroomId, userId).orElseThrow(
                () -> new HttpException("CLassroom not found", HttpStatus.BAD_REQUEST)
        );
        Optional.ofNullable(updateClassroomDto.getName()).ifPresent(classroom::setName);
        Optional.ofNullable(updateClassroomDto.getCourse()).ifPresent(classroom::setCourse);
        Optional.ofNullable(updateClassroomDto.getDescription()).ifPresent(classroom::setDescription);
        Optional.ofNullable(updateClassroomDto.getRoom()).ifPresent(classroom::setRoom);
        Optional.ofNullable(updateClassroomDto.getCoverImageUrl()).ifPresent(classroom::setCoverImageUrl);
        return classroomRepository.save(classroom);
    }


    public boolean removeMember(String classroomId, String memberId, String userId) {
        var classroom = classroomRepository.findByIdAndOwnerId(classroomId, userId).orElseThrow(
                () -> new HttpException("CLassroom not found", HttpStatus.BAD_REQUEST)
        );
        var memberToRemove = userService.findById(memberId);

        classroom.getProviders().remove(memberToRemove);
        classroom.getUsers().remove(memberToRemove);

        classroomRepository.save(classroom);
        return true;
    }


    public boolean removeInvite(RemoveInviteDto removeInviteDto, String classroomId, String userId) {
        var exists = classroomRepository.existsByIdAndOwnerId(classroomId, userId);
        if (!exists)
            throw new HttpException("Classroom not found or you don't have permission to do this.", HttpStatus.FORBIDDEN);
        var invitation = invitationRepository.findByEmailAndClassroomId(removeInviteDto.getEmail(), classroomId)
                .orElseThrow(() -> new HttpException("Invitation not found", HttpStatus.BAD_REQUEST));

        invitationRepository.delete(invitation);
        return true;
    }
}
