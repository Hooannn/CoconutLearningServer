package com.ht.elearning.meeting;

import com.ht.elearning.classroom.ClassroomService;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.constants.ErrorMessage;
import com.ht.elearning.meeting.dtos.CreateMeetingDto;
import com.ht.elearning.meeting.dtos.UpdateMeetingDto;
import com.ht.elearning.processor.ClassroomUpdateType;
import com.ht.elearning.processor.NotificationProcessor;
import com.ht.elearning.user.UserService;
import com.ht.elearning.utils.JaaSJwtBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final UserService userService;
    private final MeetingRepository meetingRepository;
    private final ClassroomService classroomService;
    private final NotificationProcessor notificationProcessor;

    @Value("${jitsi.appId}")
    private String appId;

    @Value("${jitsi.publicKey}")
    private String publicKey;

    public Meeting findById(String meetingId) {
        return meetingRepository.findById(meetingId)
                .orElseThrow(() -> new HttpException(ErrorMessage.MEETING_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public Meeting create(CreateMeetingDto createMeetingDto, String classroomId, String userId) {
        var isProvider = classroomService.isProvider(classroomId, userId);
        if (!isProvider) throw new HttpException(ErrorMessage.USER_IS_NOT_PROVIDER, HttpStatus.FORBIDDEN);

        if (createMeetingDto.getEndAt().before(new Date()))
            throw new HttpException(ErrorMessage.MEETING_TIME_IS_INVALID, HttpStatus.BAD_REQUEST);

        var exists = meetingRepository.existsMeetingTime(createMeetingDto.getStartAt(), createMeetingDto.getEndAt());
        if (exists) throw new HttpException(ErrorMessage.MEETING_TIME_IS_CONFLICT, HttpStatus.CONFLICT);
        var classroom = classroomService.findById(classroomId);
        var meeting = Meeting.builder()
                .classroom(classroom)
                .createdBy(userService.findById(userId))
                .startAt(createMeetingDto.getStartAt())
                .endAt(createMeetingDto.getEndAt())
                .name(createMeetingDto.getName())
                .build();

        var savedMeeting = meetingRepository.save(meeting);
        notificationProcessor.meetingDidCreate(savedMeeting);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEETING);
        return savedMeeting;
    }

    public Meeting update(UpdateMeetingDto updateMeetingDto, String meetingId, String userId) {
        var meeting = meetingRepository.findByIdAndCreatedById(meetingId, userId)
                .orElseThrow(() -> new HttpException(ErrorMessage.MEETING_NOT_FOUND, HttpStatus.BAD_REQUEST));

        AtomicBoolean isTimeChanged = new AtomicBoolean(false);

        Optional.ofNullable(updateMeetingDto.getName()).ifPresent(meeting::setName);

        Optional.ofNullable(updateMeetingDto.getStartAt()).ifPresent(d -> {
            isTimeChanged.set(true);
            meeting.setStartAt(d);
        });

        Optional.ofNullable(updateMeetingDto.getEndAt()).ifPresent(d -> {
            if (d.before(new Date()))
                throw new HttpException(ErrorMessage.MEETING_TIME_IS_INVALID, HttpStatus.BAD_REQUEST);
            isTimeChanged.set(true);
            meeting.setEndAt(d);
        });

        var exists = meetingRepository.existsMeetingTime(updateMeetingDto.getStartAt(), updateMeetingDto.getEndAt(), meeting.getId());

        if (exists) throw new HttpException(ErrorMessage.MEETING_TIME_IS_CONFLICT, HttpStatus.CONFLICT);

        var savedMeeting = meetingRepository.save(meeting);

        if (isTimeChanged.get()) notificationProcessor.meetingTimeDidUpdate(savedMeeting);

        notificationProcessor.classroomDidUpdate(savedMeeting.getClassroom(), ClassroomUpdateType.MEETING);

        return savedMeeting;
    }

    public boolean deleteById(String meetingId, String userId) {
        var meeting = meetingRepository.findByIdAndCreatedById(meetingId, userId)
                .orElseThrow(() -> new HttpException(ErrorMessage.MEETING_NOT_FOUND, HttpStatus.BAD_REQUEST));
        var classroom = meeting.getClassroom();
        meetingRepository.delete(meeting);
        notificationProcessor.classroomDidUpdate(classroom, ClassroomUpdateType.MEETING);
        return true;
    }

    public List<Meeting> findAllByClassroomId(String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException(ErrorMessage.USER_IS_NOT_MEMBER, HttpStatus.FORBIDDEN);
        return meetingRepository.findAllByClassroomIdAndEndAtAfterOrderByStartAtAsc(classroomId, new Date());
    }

    public String generateMeetingToken(String meetingId, String userId) {
        try {
            var meeting = findById(meetingId);
            var isMember = classroomService.isMember(meeting.getClassroom(), userId);
            if (!isMember) throw new HttpException(ErrorMessage.USER_IS_NOT_MEMBER, HttpStatus.FORBIDDEN);

            Date now = new Date();
            if (now.before(meeting.getStartAt()) || now.after(meeting.getEndAt()))
                throw new HttpException(ErrorMessage.MEETING_ENDED_OR_NOT_START, HttpStatus.BAD_REQUEST);

            var user = userService.findById(userId);

            var isModerator = meeting.getCreatedBy().getId().equals(user.getId());

            return JaaSJwtBuilder.buildJaasJwt(appId, publicKey, user, isModerator, meeting.getId(), meeting.getEndAt(), meeting.getStartAt());
        } catch (Exception e) {
            throw new HttpException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<Meeting> findUpcomingByClassroomId(String classroomId, String userId) {
        var isMember = classroomService.isMember(classroomId, userId);
        if (!isMember) throw new HttpException(ErrorMessage.USER_IS_NOT_MEMBER, HttpStatus.FORBIDDEN);
        Date now = new Date();
        Date nextWeek = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
        return meetingRepository.findAllByClassroomIdAndEndAtAfterAndStartAtBeforeOrderByStartAtAsc(classroomId, now, nextWeek);
    }
}
