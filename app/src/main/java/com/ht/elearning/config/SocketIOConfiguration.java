package com.ht.elearning.config;


import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.SocketIOServer;
import com.ht.elearning.jwt.JwtService;
import io.netty.handler.codec.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SocketIOConfiguration {
    @Value("${socket-server.host}")
    private String host;
    @Value("${socket-server.port}")
    private Integer port;

    private final JwtService jwtService;
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setAuthorizationListener(authorizationListener());
        return new SocketIOServer(config);
    }
    private AuthorizationListener authorizationListener() {
        return handshakeData -> {
            HttpHeaders headers = handshakeData.getHttpHeaders();
            String authorization = headers.get("Authorization");
            if (authorization == null) return AuthorizationResult.FAILED_AUTHORIZATION;
            var subject = jwtService.extractSub(authorization);
            var isAuthorized = jwtService.isTokenValid(authorization);
            if (isAuthorized) handshakeData.getHttpHeaders().set("x-auth-id", subject);
            return isAuthorized ? AuthorizationResult.SUCCESSFUL_AUTHORIZATION : AuthorizationResult.FAILED_AUTHORIZATION;
        };
    }
}