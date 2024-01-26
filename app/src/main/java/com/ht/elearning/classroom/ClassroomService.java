package com.ht.elearning.classroom;

import com.ht.elearning.classroom.dtos.*;
import com.ht.elearning.classwork.ClassworkRepository;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationRepository;
import com.ht.elearning.invitation.InvitationType;
import com.ht.elearning.notification.NotificationService;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserService;
import com.ht.elearning.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserService userService;
    private final InvitationRepository invitationRepository;
    private final ClassworkRepository classworkRepository;
    private final NotificationProcessor notificationProcessor;
    private final NotificationService notificationService;

    public Classroom create(CreateClassroomDto createClassroomDto, String ownerId) {
        Random random = new Random();
        int randomLength = random.nextInt(7 + 1 - 5) + 5;
        String inviteCode = Helper.generateRandomSecret(randomLength);
        if (classroomRepository.existsByInviteCode(inviteCode))
            throw new HttpException(ErrorMessage.SOMETHING_WRONG, HttpStatus.CONFLICT);

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
                () -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST)
        );

        if (!isMember(classroom, userId))
            throw new HttpException(ErrorMessage.FORBIDDEN, HttpStatus.FORBIDDEN);

        return classroom;
    }

    public Classroom findById(String classroomId) {
        return classroomRepository.findById(classroomId)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public boolean invite(InviteDto inviteDto, String ownerId) {
        var classroom = classroomRepository.findByIdAndOwnerId(inviteDto.getClassId(), ownerId).orElseThrow(
                () -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST)
        );

        if (classroom.getMembers().stream().anyMatch(u -> u.getEmail().equals(inviteDto.getEmail())))
            throw new HttpException(ErrorMessage.USER_ALREADY_JOINED, HttpStatus.BAD_REQUEST);

        if (classroom.getInvitations().stream().anyMatch(i -> i.getEmail().equals(inviteDto.getEmail())))
            throw new HttpException(ErrorMessage.INVITATION_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);

        var invitation = Invitation.builder()
                .email(inviteDto.getEmail())
                .classroom(classroom)
                .type(inviteDto.getType() == null ? InvitationType.USER : inviteDto.getType())
                .build();

        var savedInvitation = invitationRepository.save(invitation);

        notificationProcessor.invitationDidCreate(savedInvitation, classroom);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);

        return true;
    }

    public boolean inviteMany(InviteManyDto inviteManyDto, String ownerId) {
        var classroom = classroomRepository.findByIdAndOwnerId(inviteManyDto.getClassId(), ownerId).orElseThrow(
                () -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST)
        );
        //Illegal email list includes member emails and existed invitation emails
        Set<String> illegalEmails = classroom.getMembers().stream().map(User::getEmail).collect(Collectors.toSet());
        illegalEmails.add(classroom.getOwner().getEmail());
        illegalEmails.addAll(classroom.getInvitations().stream().map(Invitation::getEmail).collect(Collectors.toSet()));

        if (inviteManyDto.getEmails().stream().anyMatch(illegalEmails::contains))
            throw new HttpException(ErrorMessage.ILLEGAL_EMAILS, HttpStatus.BAD_REQUEST);

        var invitationType = inviteManyDto.getType();
        var invitations = inviteManyDto.getEmails().stream().map(email -> Invitation.builder()
                .type(invitationType)
                .classroom(classroom)
                .email(email)
                .build()).toList();

        var savedInvitations = invitationRepository.saveAll(invitations);

        notificationProcessor.invitationsDidCreate(savedInvitations, classroom);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);

        return true;
    }

    public boolean join(String inviteCode, String userId) {
        var classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (isMember(classroom, userId))
            throw new HttpException(ErrorMessage.USER_ALREADY_JOINED, HttpStatus.BAD_REQUEST);

        var user = userService.findById(userId);

        classroom.getUsers().add(user);

        classroom.getInvitations().stream().filter(i -> i.getEmail().equals(user.getEmail())).findFirst().ifPresent(
                invitationRepository::delete
        );
        var saved = classroomRepository.save(classroom);
        notificationProcessor.memberDidJoin(saved, user);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);
        return true;
    }


    public boolean accept(String inviteCode, String notificationId, String userId) {
        var classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST));

        var user = userService.findById(userId);

        var invitation = classroom.getInvitations().stream()
                .filter(i -> i.getEmail().equals(user.getEmail()))
                .findFirst()
                .orElseThrow(() -> new HttpException(ErrorMessage.INVITATION_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (isMember(classroom, userId))
            throw new HttpException(ErrorMessage.USER_ALREADY_JOINED, HttpStatus.BAD_REQUEST);

        if (invitation.getType() == InvitationType.PROVIDER) {
            classroom.getProviders().add(user);
        } else {
            classroom.getUsers().add(user);
        }

        invitationRepository.delete(invitation);
        var saved = classroomRepository.save(classroom);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);
        notificationProcessor.memberDidJoin(saved, user);
        notificationService.markAsDone(notificationId);
        return true;
    }


    public boolean refuse(String inviteCode, String notificationId, String userId) {
        var classroom = classroomRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new HttpException(ErrorMessage.CLASSROOM_NOT_FOUND, HttpStatus.BAD_REQUEST));

        var user = userService.findById(userId);

        var invitation = classroom.getInvitations().stream().filter(i -> i.getEmail().equals(user.getEmail())).findFirst().orElseThrow(
                () -> new HttpException(ErrorMessage.INVITATION_NOT_FOUND, HttpStatus.BAD_REQUEST)
        );

        invitationRepository.delete(invitation);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);
        notificationService.markAsDone(notificationId);
        return true;
    }


    public boolean leave(String classroomId, String userId) {
        var classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));

        if (!isMember(classroom, userId))
            throw new HttpException("You are not a member of this classroom", HttpStatus.BAD_REQUEST);

        var user = userService.findById(userId);

        classroom.getUsers().remove(user);
        classroom.getProviders().remove(user);

        var saved = classroomRepository.save(classroom);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);
        notificationProcessor.memberDidLeave(saved, user);

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

        var savecClassroom = classroomRepository.save(classroom);

        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSROOM);

        return savecClassroom;
    }


    public boolean isMember(String classId, String userId) {
        var classroom = classroomRepository.findById(classId).orElse(null);
        if (classroom == null) return false;

        return classroom.getMembers().stream().anyMatch(u -> u.getId().equals(userId));
    }


    public boolean isMember(Classroom classroom, String userId) {
        return classroom.getMembers().stream().anyMatch(u -> u.getId().equals(userId));
    }


    public boolean isProvider(String classId, String userId) {
        var classroom = classroomRepository.findById(classId).orElse(null);
        if (classroom == null) return false;

        return classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId)) || classroom.getOwner().getId().equals(userId);
    }


    public boolean isProvider(Classroom classroom, String userId) {
        return classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId)) || classroom.getOwner().getId().equals(userId);
    }


    public boolean hasClasswork(String classroomId, String classworkId) {
        return classworkRepository.existsByIdAndClassroomId(classworkId, classroomId);
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
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSROOM);
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
        var savedClassroom = classroomRepository.save(classroom);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.CLASSROOM);
        return savedClassroom;
    }


    public boolean removeMember(String classroomId, String memberId, String userId) {
        var classroom = classroomRepository.findByIdAndOwnerId(classroomId, userId).orElseThrow(
                () -> new HttpException("CLassroom not found", HttpStatus.BAD_REQUEST)
        );
        var memberToRemove = userService.findById(memberId);

        classroom.getProviders().remove(memberToRemove);
        classroom.getUsers().remove(memberToRemove);

        classroomRepository.save(classroom);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);
        return true;
    }


    public boolean removeInvite(RemoveInviteDto removeInviteDto, String classroomId, String userId) {
        var classroom = classroomRepository.findByIdAndOwnerId(classroomId, userId).orElseThrow(
                () -> new HttpException("Classroom not found or you don't have permission to do this.", HttpStatus.FORBIDDEN)
        );
        var invitation = invitationRepository.findByEmailAndClassroomId(removeInviteDto.getEmail(), classroomId)
                .orElseThrow(() -> new HttpException("Invitation not found", HttpStatus.BAD_REQUEST));

        invitationRepository.delete(invitation);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEMBER);
        return true;
    }
}
