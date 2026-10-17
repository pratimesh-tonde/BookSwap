package com.bookexchange.config;

import com.bookexchange.entity.User;
import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * STOMP over WebSocket for the chat.
 *  - client connects to  ws://host:8080/ws  and sends the JWT in the STOMP CONNECT header "Authorization"
 *  - client sends to     /app/chat/{exchangeId}
 *  - client subscribes   /topic/exchange/{exchangeId}   (only the two participants are allowed)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String TOPIC_PREFIX = "/topic/exchange/";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;

    @Value("${app.cors.origin}")
    private String allowedOrigins;

    public WebSocketConfig(JwtUtil jwtUtil, UserRepository userRepository, ExchangeRepository exchangeRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim).toArray(String[]::new);
        registry.addEndpoint("/ws").setAllowedOrigins(origins);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null || accessor.getCommand() == null) {
                    return message;
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String header = accessor.getFirstNativeHeader("Authorization");
                    if (header == null || !header.startsWith("Bearer ")) {
                        throw new MessageDeliveryException("Missing token");
                    }
                    try {
                        Claims claims = jwtUtil.parse(header.substring(7));
                        User user = userRepository.findById(claims.getSubject())
                                .filter(u -> !u.isBlocked())
                                .orElseThrow(() -> new MessageDeliveryException("User not allowed"));
                        accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, List.of()));
                    } catch (MessageDeliveryException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new MessageDeliveryException("Invalid token");
                    }
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith(TOPIC_PREFIX)) {
                        String exchangeId = destination.substring(TOPIC_PREFIX.length());
                        if (!isParticipant(accessor, exchangeId)) {
                            throw new MessageDeliveryException("Not your conversation");
                        }
                    } else {
                        throw new MessageDeliveryException("Unknown destination");
                    }
                }
                return message;
            }
        });
    }

    private boolean isParticipant(StompHeaderAccessor accessor, String exchangeId) {
        if (!(accessor.getUser() instanceof Authentication auth) || !(auth.getPrincipal() instanceof User user)) {
            return false;
        }
        return exchangeRepository.findById(exchangeId)
                .filter(ex -> user.getId().equals(ex.getRequesterId()) || user.getId().equals(ex.getOwnerId()))
                .isPresent();
    }
}