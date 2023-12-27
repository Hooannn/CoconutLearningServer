package com.ht.elearning.classroom;

import com.ht.elearning.classroom.dtos.CreateClassroomDto;
import com.ht.elearning.classroom.dtos.InviteDto;
import com.ht.elearning.classroom.dtos.JoinDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationRepository;
import com.ht.elearning.processor.AppProcessor;
import com.ht.elearning.user.Role;
import com.ht.elearning.user.User;
import com.ht.elearning.user.UserRepository;
import com.ht.elearning.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ClassroomService {
    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final AppProcessor appProcessor;
    public Classroom createClassroom(CreateClassroomDto createClassroomDto, String ownerId) {
        try {
            Random random = new Random();
            int randomLength = random.nextInt(7 + 1 - 5) + 5;
            String inviteCode = Helper.generateRandomSecret(randomLength);
            if (classroomRepository.existsByInviteCode(inviteCode))
                throw new HttpException("Something went wrong. Please try again", HttpStatus.CONFLICT);

            var owner = userRepository.findById(ownerId).orElseThrow(() -> new HttpException("User not found", HttpStatus.BAD_REQUEST));
            var classroom = Classroom
                    .builder()
                    .name(createClassroomDto.getName())
                    .room(createClassroomDto.getRoom())
                    .description(createClassroomDto.getDescription())
                    .course(createClassroomDto.getCourse())
                    .inviteCode(inviteCode)
                    .owner(owner)
                    .build();

            return classroomRepository.save(classroom);
        } catch (HttpException e) {
            throw new HttpException(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public Classroom findClassroom(String classroomId, String userId) {
        try {
            var classroom = classroomRepository.findById(classroomId).orElseThrow(
                    () -> new HttpException("Classroom not found",HttpStatus.BAD_REQUEST)
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
        } catch (HttpException e) {
            throw new HttpException(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public boolean invite(InviteDto inviteDto, String ownerId) {
        try {
            var classroom = classroomRepository.findByIdAndOwnerId(inviteDto.getClassId(), ownerId).orElseThrow(
                    () -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST)
            );

            if (classroom.getOwner().getEmail().equals(inviteDto.getEmail()))
                throw new HttpException("Illegal request", HttpStatus.NOT_ACCEPTABLE);

            if (classroom.getUsers().stream().anyMatch(u -> u.getEmail().equals(inviteDto.getEmail()))
                    || classroom.getProviders().stream().anyMatch(u -> u.getId().equals(inviteDto.getEmail())))
                throw new HttpException("User already joined", HttpStatus.BAD_REQUEST);

            if (invitationRepository.existsByEmailAndClassroomId(inviteDto.getEmail(), inviteDto.getClassId()))
                throw new HttpException("Invitation already exists", HttpStatus.BAD_REQUEST);

            var invitation = Invitation.builder().email(inviteDto.getEmail()).classroom(classroom).build();

            var savedInvitation = invitationRepository.save(invitation);

            appProcessor.processClassroomInvitation(savedInvitation);

            return true;
        } catch (HttpException e) {
            throw new HttpException(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    public boolean join(JoinDto joinDto, String userId) {
        try {
            var classroom = classroomRepository.findByInviteCode(joinDto.getInviteCode())
                    .orElseThrow(() -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST));

            if (classroom.getOwner().getId().equals(userId))
                throw new HttpException("Illegal request", HttpStatus.NOT_ACCEPTABLE);

            if (classroom.getUsers().stream().anyMatch(u -> u.getId().equals(userId))
                    || classroom.getProviders().stream().anyMatch(u -> u.getId().equals(userId)))
                throw new HttpException("Already joined", HttpStatus.BAD_REQUEST);

            var user = userRepository.findById(userId).orElseThrow(() -> new HttpException("User not found", HttpStatus.BAD_REQUEST));

            if (user.getRole().equals(Role.USER)) {
                classroom.getUsers().add(user);
            } else {
                classroom.getProviders().add(user);
            }

            invitationRepository.deleteByEmailAndClassroomId(user.getEmail(), classroom.getId());
            classroomRepository.save(classroom);

            return true;
        } catch (HttpException e) {
            throw new HttpException(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
