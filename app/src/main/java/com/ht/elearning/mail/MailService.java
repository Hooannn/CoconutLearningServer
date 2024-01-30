package com.ht.elearning.mail;

import com.ht.elearning.classwork.Classwork;
import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.invitation.InvitationType;
import com.ht.elearning.meeting.Meeting;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${client.web.url}")
    private String clientWebUrl;


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("support@coconut.online");
        javaMailSender.send(message);
    }


    public void sendAccountVerificationMail(String to, String subject, String signature) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("code", signature);
        String htmlContent = templateEngine.process("account-verification", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendResetPasswordVerificationMail(String to, String subject, String signature) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("code", signature);

        String htmlContent = templateEngine.process("reset-password-verification", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendWelcomeMail(String to, String subject) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("welcomeText", "Welcome to Coconut Online Classroom!");
        context.setVariable("welcomeMessage", "We are so excited to have you here. Coconut Online Classroom is a platform that allows teachers to create online classrooms and manage their students. We hope you enjoy using our platform.");
        context.setVariable("homeUrl", clientWebUrl);
        String htmlContent = templateEngine.process("welcome", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendClassroomInvitationMail(Invitation invitation, String subject) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("acceptUrl", clientWebUrl + "/webhook?invite_code=" + invitation.getClassroom().getInviteCode());
        context.setVariable("invitationText", "You have been invited to join \"" + invitation.getClassroom().getName() + "\" as a " + (invitation.getType() == InvitationType.USER ? "student" : "co-teacher") + ".");

        String htmlContent = templateEngine.process("classroom-invitation", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(invitation.getEmail());
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendClassroomInvitationMails(List<Invitation> invitations, String subject) {
        List<MimeMessage> mimeMessages = invitations.stream().map(invitation -> {
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = null;
                helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                Context context = new Context();
                context.setVariable("acceptUrl", clientWebUrl + "/webhook?invite_code=" + invitation.getClassroom().getInviteCode());
                context.setVariable("invitationText", "You have been invited to join \"" + invitation.getClassroom().getName() + "\" as a " + (invitation.getType() == InvitationType.USER ? "student" : "co-teacher") + ".");
                String htmlContent = templateEngine.process("classroom-invitation", context);
                helper.setFrom("support@coconut.online");
                helper.setTo(invitation.getEmail());
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                return mimeMessage;
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();

        javaMailSender.send(mimeMessages.toArray(new MimeMessage[0]));
    }


    public void sendNewClassworkMail(String to, String subject, Classwork savedClasswork) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("className", savedClasswork.getClassroom().getName());
        context.setVariable("classworkTitle", savedClasswork.getTitle());
        context.setVariable("classworkDescription", savedClasswork.getDescription());
        context.setVariable("classworkLink", clientWebUrl + "/classroom/" + savedClasswork.getClassroom().getId() + "/classwork/" + savedClasswork.getId());

        String htmlContent = templateEngine.process("new-classwork", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendClassworkReminderMail(String to, String subject, Classwork classwork) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("className", classwork.getClassroom().getName());
        context.setVariable("classworkTitle", classwork.getTitle());
        context.setVariable("classworkDeadline", classwork.getDeadline());
        context.setVariable("classworkLink", clientWebUrl + "/classroom/" + classwork.getClassroom().getId() + "/classwork/" + classwork.getId());

        String htmlContent = templateEngine.process("classwork-reminder", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendNewMeetingMail(String to, String subject, Meeting savedMeeting) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        DateFormatter dateFormatter = new DateFormatter("dd/MM/yyyy HH:mm");
        var time = dateFormatter.print(savedMeeting.getStartAt(), Locale.ENGLISH);
        context.setVariable("className", savedMeeting.getClassroom().getName());
        context.setVariable("meetingName", savedMeeting.getName());
        context.setVariable("meetingTime", time);
        context.setVariable("meetingLink", clientWebUrl + "/classroom/" + savedMeeting.getClassroom().getId() + "?tab=meeting");

        String htmlContent = templateEngine.process("new-meeting", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }

    public void sendMeetingReminderMail(String to, String subject, Meeting meeting) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        DateFormatter dateFormatter = new DateFormatter("dd/MM/yyyy HH:mm");
        var time = dateFormatter.print(meeting.getStartAt(), Locale.ENGLISH);
        context.setVariable("className", meeting.getClassroom().getName());
        context.setVariable("meetingName", meeting.getName());
        context.setVariable("meetingTime", time);
        context.setVariable("meetingLink", clientWebUrl + "/classroom/" + meeting.getClassroom().getId() + "?tab=meeting");

        String htmlContent = templateEngine.process("meeting-reminder", context);

        helper.setFrom("support@coconut.online");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }
}
