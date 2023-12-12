package com.ht.elearning.classroom;

import com.ht.elearning.classroom.dtos.CreateClassroomDto;
import com.ht.elearning.classroom.dtos.InviteDto;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationRepository;
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
                    classroom.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null) == null
                            &&
                            !classroom.getOwner().getId().equals(userId)
                            &&
                            classroom.getProviders().stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null) == null
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
            // TODO: Implement
            var classroom = classroomRepository.findByIdAndOwnerId(inviteDto.getClassId(), ownerId).orElseThrow(
                    () -> new HttpException("Classroom not found", HttpStatus.BAD_REQUEST)
            );
            if (invitationRepository.existsByEmailAndClassroomId(inviteDto.getEmail(), inviteDto.getClassId()))
                throw new HttpException("Invitation already exists", HttpStatus.BAD_REQUEST);

            var invitation = Invitation.builder().email(inviteDto.getEmail()).classroom(classroom).build();

            invitationRepository.save(invitation);

            return true;
        } catch (HttpException e) {
            throw new HttpException(e.getMessage(), e.getStatus());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
