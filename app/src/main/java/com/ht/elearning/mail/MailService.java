package com.ht.elearning.mail;

import com.ht.elearning.classwork.Classwork;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;


    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }


    public void sendAccountVerificationMail(String to, String subject, String signature) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("code", signature);
        String htmlContent = templateEngine.process("account-verification", context);

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

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendWelcomeMail(String to, String subject) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        String htmlContent = templateEngine.process("welcome", context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendClassroomInvitationMail(String to, String subject, String acceptUrl) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        Context context = new Context();
        context.setVariable("acceptUrl", acceptUrl);

        String htmlContent = templateEngine.process("classroom-invitation", context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendClassroomInvitationMails(List<String> toList, String subject, String acceptUrl) {
        List<MimeMessage> mimeMessages = toList.stream().map(email -> {
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = null;
                helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                Context context = new Context();
                context.setVariable("acceptUrl", acceptUrl);
                String htmlContent = templateEngine.process("classroom-invitation", context);
                helper.setTo(email);
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

        String htmlContent = templateEngine.process("new-classwork", context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }
}
