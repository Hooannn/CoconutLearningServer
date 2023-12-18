package com.ht.elearning.processor;

import com.ht.elearning.invitation.Invitation;
import com.ht.elearning.jwt.JwtService;
import com.ht.elearning.mail.MailService;
import com.ht.elearning.redis.RedisService;
import com.ht.elearning.user.User;
import com.ht.elearning.utils.Helper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class AppProcessor {
    private final RedisService redisService;
    private final MailService mailService;
    private final JwtService jwtService;

    @Async
    public void processAccountVerification(User user) throws MessagingException {
        var email = user.getEmail();
        var signature = Helper.generateRandomSecret(12);
        redisService.setValue("account_signature:" + email, signature, 600);
        mailService.sendAccountVerificationMail(email, "ELearning - Account Verification", signature);
    }

    @Async
    public void processWelcomeUser(User user) throws MessagingException {
        var email = user.getEmail();
        mailService.sendWelcomeMail(email, "ELearning - Welcome");
    }

    @Async
    public void processForgotPassword(String email) throws MessagingException {
        var signature = Helper.generateRandomSecret(12);
        redisService.setValue("reset_password_signature:" + email, signature, 600);
        mailService.sendResetPasswordVerificationMail(email, "ELearning - Reset password", signature);
    }

    @Async
    public void processClassroomInvitation(Invitation invitation) throws MessagingException {
        var urlString = "https://example.com/classroom/invitation?invite_code=" + invitation.getClassroom().getInviteCode();
        URL acceptUrl = null;
        try {
            acceptUrl = new URI(urlString).toURL();
            mailService.sendClassroomInvitationMail(invitation.getEmail(), "ELearning - Classroom invitation", acceptUrl.toString());
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
