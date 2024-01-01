package com.ht.elearning.push_notification;

import com.google.firebase.messaging.*;
import com.ht.elearning.config.HttpException;
import com.ht.elearning.push_notification.dtos.Platform;
import com.ht.elearning.push_notification.dtos.RegisterTokenDto;
import com.ht.elearning.push_notification.dtos.RemoveTokenDto;
import com.ht.elearning.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PushNotificationService {
    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

    public FcmToken registerToken(RegisterTokenDto registerTokenDto, String userId) {
        FcmToken token = fcmTokenRepository.findById(userId).orElse(null);
        String value = registerTokenDto.getToken();
        Platform platform = registerTokenDto.getPlatform();
        if (token == null) {
            var builder = FcmToken.builder().uid(userId);
            switch (platform) {
                case IOS -> builder.ios(value);
                case WEB -> builder.web(value);
                case ANDROID -> builder.android(value);
            }
            token = builder.build();
        } else {
            switch (platform) {
                case IOS -> token.setIos(value);
                case WEB -> token.setWeb(value);
                case ANDROID -> token.setAndroid(value);
            }
        }
        return fcmTokenRepository.save(token);
    }

    public FcmToken removeToken(RemoveTokenDto removeTokenDto, String userId) {
        FcmToken token = fcmTokenRepository.findById(userId)
                .orElseThrow(() -> new HttpException("Token not found", HttpStatus.BAD_REQUEST));
        Platform platform = removeTokenDto.getPlatform();
        switch (platform) {
            case IOS -> token.setIos(null);
            case WEB -> token.setWeb(null);
            case ANDROID -> token.setAndroid(null);
        }
        return fcmTokenRepository.save(token);
    }

    public BatchResponse push(List<String> userIds, Notification notification, Map<String, String> messageData) throws Exception {
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllById(userIds);
        if (fcmTokens.isEmpty()) throw new Exception("Token not found");
        List<Message> messages = buildMessages(fcmTokens, notification, messageData);
        return firebaseMessaging.sendAll(messages);
    }

    public BatchResponse push(List<String> userIds, Notification notification, Map<String, String> messageData, boolean dryRun) throws Exception {
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllById(userIds);
        if (fcmTokens.isEmpty()) throw new Exception("Token not found");
        List<Message> messages = buildMessages(fcmTokens, notification, messageData);
        return firebaseMessaging.sendAll(messages, dryRun);
    }

    public BatchResponse push(String userId, Notification notification, Map<String, String> messageData) throws Exception {
        FcmToken fcmToken = fcmTokenRepository.findById(userId).orElseThrow(() -> new Exception("Token not found"));
        List<Message> messages = buildMessages(fcmToken, notification, messageData);
        return firebaseMessaging.sendAll(messages);
    }

    public BatchResponse push(String userId, Notification notification, Map<String, String> messageData, boolean dryRun) throws Exception {
        FcmToken fcmToken = fcmTokenRepository.findById(userId).orElseThrow(() -> new Exception("Token not found"));
        List<Message> messages = buildMessages(fcmToken, notification, messageData);
        return firebaseMessaging.sendAll(messages, dryRun);
    }

    private List<Message> buildMessages(FcmToken fcmToken, Notification notification, Map<String, String> messageData) {
        List<String> messageTokens = extractTokens(fcmToken);
        return messageTokens
                .stream()
                .map(token -> {
                    return Message.builder()
                            .setNotification(notification)
                            .putAllData(messageData)
                            .setToken(token)
                            .build();
                })
                .toList();
    }

    private List<Message> buildMessages(List<FcmToken> fcmTokens, Notification notification, Map<String, String> messageData) {
        List<String> messageTokens = extractTokens(fcmTokens);
        return messageTokens
                .stream()
                .map(token -> {
                    return Message.builder()
                            .setNotification(notification)
                            .putAllData(messageData)
                            .setToken(token)
                            .build();
                })
                .toList();
    }

    private List<String> extractTokens(FcmToken fcmToken) {
        return Stream.of(fcmToken.getIos(), fcmToken.getAndroid(), fcmToken.getWeb())
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> extractTokens(List<FcmToken> fcmTokens) {
        return fcmTokens
                .stream()
                .flatMap(fcmToken -> extractTokens(fcmToken).stream())
                .toList();
    }
}
